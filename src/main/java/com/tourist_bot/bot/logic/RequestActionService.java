package com.tourist_bot.bot.logic;

import com.tourist_bot.bot.logic.btn.CallBackBtnName;
import com.tourist_bot.bot.logic.btn.CallbackLanBtn;
import com.tourist_bot.bot.logic.btn.CallbackTourismBtn;
import com.tourist_bot.bot.logic.language.Language;
import com.tourist_bot.bot.session.Session;
import com.tourist_bot.bot.storage.TouristAttraction;
import com.tourist_bot.bot.storage.quad_storage.EmbeddedGeoQuadTreeGeoStorage;
import com.tourist_bot.bot.storage.search.FoundTourismAttraction;
import com.tourist_bot.bot.storage.search.SearchRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static com.tourist_bot.bot.logic.ResponseMessagesBuilder.*;


public class RequestActionService {

    private static final Logger log = LoggerFactory.getLogger(RequestActionService.class.getName());
    private final BotConf botConf;
    private final EmbeddedGeoQuadTreeGeoStorage storage;

    public RequestActionService(BotConf botConf, EmbeddedGeoQuadTreeGeoStorage storage) {
        this.botConf = botConf;
        this.storage = storage;
    }

    public List<BotApiMethodMessage> processNewUserAction(Session session) {
        long userId = session.getClientInfo().userId;
        log.info("[user_id:" + userId + "] " + "new user session. " + session.getClientInfo());
        return getHelpMessages(session);
    }

    public List<BotApiMethodMessage> processTypedResetStep(Session session) {
        long userId = session.getClientInfo().userId;
        log.info("[user_id:" + userId + "] " + "typed /reset");
        resetSession(session);
        return processTypedHelpAction(session);
    }

    public List<BotApiMethodMessage> processHelpCallbackAction(Session session) {
        long userId = session.getClientInfo().userId;
        log.info("[user_id:" + userId + "] " + "pressed on  HELP callback");
        return processTypedHelpAction(session);
    }

    public List<BotApiMethodMessage> processOutdatedCallbackAction(Session session, CallbackQuery callbackQuery) {
        long userId = session.getClientInfo().userId;
        log.info("[user_id:" + userId + "] " + "pressed on outdated btn " + callbackQuery.getData());
        return new ArrayList<>();
    }

    public List<BotApiMethodMessage> processTypedHelpAction(Session session) {
        long userId = session.getClientInfo().userId;
        log.info("[user_id:" + userId + "] " + "typed /help");
        return getHelpMessages(session);
    }

    private List<BotApiMethodMessage> getHelpMessages(Session session) {
        long chatId = session.getClientInfo().chatId;

        return new ArrayList<>() {{
            add(sendHelloForNewUser(
                    chatId,
                    session.getLanguageMessages().getLanguage(),
                    botConf.sessionTimeOutSec / 60,
                    botConf.maxSearchDistanceMeters));
            add(sendOfferToChangeLanguage(chatId, session.sessionId));
        }};
    }

    private void resetSession(Session session) {
        long userId = session.getClientInfo().userId;
        session.resetSession();
        log.info("[user_id:" + userId + "] " + "Session was reset");
    }

    public List<BotApiMethodMessage> processTypedLocationAction(Session session, Message message) {
        long userId = session.getClientInfo().userId;
        Location location = message.getLocation();
        String locationStr = "lon:" + location.getLongitude() + ",lat:" + location.getLatitude();
        log.info("[user_id:" + userId + "] " + "Received new location: " + locationStr);
        resetSession(session);
        session.setLocation(location);
        return findAttractions(session);
    }

    public List<BotApiMethodMessage> processLoadMoreCallback(Session session) {
        long userId = session.getClientInfo().userId;
        log.info("[user_id:" + userId + "] " + "Pressed " + CallBackBtnName.LOAD_MORE);
        return findAttractions(session);
    }

    public List<BotApiMethodMessage> findAttractions(Session session) {
        long userId = session.getClientInfo().userId;
        long chatId = session.getClientInfo().chatId;
        Language userLanguage = session.getLanguageMessages().getLanguage();

        SearchRes newSearch;
        if (session.getLastSearch().distance >= botConf.maxSearchDistanceMeters) {
            log.info("[user_id:" + userId + "] " + "Max search distance is reached. skipping");
            newSearch = session.getLastSearch();
        } else {
            newSearch =
                    storage.getGroupedAttractionsIdSorted(
                            session.getLocation().getLongitude().floatValue(),
                            session.getLocation().getLatitude().floatValue(),
                            session.getLastSearchDistance() + botConf.searchDistanceStep,
                            session.getLastSearch().getNumberOfAttractions() + 1,
                            session.getViewedAttractions());
            log.info("[user_id:" + userId + "] " + "Found attractions " + newSearch.getFoundAttractions());
            session.setLastSearch(newSearch);
        }

        if (newSearch.groupedAttractions.isEmpty()) {
            session.resetSession();
            return new ArrayList<>() {{
                add(buildNoAttractionsFoundSendAnotherLocationMessage(chatId, userLanguage));
            }};
        } else {
            ArrayList<BotApiMethodMessage> responses = new ArrayList<>();
            if (botConf.maxSearchDistanceMeters <= newSearch.distance) {
                responses.add(buildMaxDistanceReachedMessage(chatId, userLanguage, botConf.maxSearchDistanceMeters));
            }
            responses.add(sendRequestTourismType(
                    chatId,
                    newSearch.groupedAttractions,
                    session.getLanguageMessages().getLanguage(),
                    session.sessionId,
                    newSearch.distance,
                    botConf.maxSearchDistanceMeters));
            return responses;
        }
    }

    public List<BotApiMethodMessage> processCallbackTourismAction(Session session, CallbackQuery callbackQuery) {
        long userId = session.getClientInfo().userId;
        long chatId = session.getClientInfo().chatId;

        CallbackTourismBtn callbackBtn = CallbackTourismBtn.parseButton(callbackQuery.getData());
        log.info("[user_id:" + userId + "] " + "Pressed btn tourism type:" + callbackBtn.tourismType);

        int tourismType = callbackBtn.tourismType;


        SearchRes searchRes = session.getLastSearch();
        ArrayList<FoundTourismAttraction> foundTourismAttractions = searchRes.groupedAttractions.get(tourismType);
        ArrayList<BotApiMethodMessage> response = new ArrayList<>();
        if (foundTourismAttractions == null) {
            log.info("[user_id:" + userId + "] " + "No more attractions for tourismType: '" + tourismType + "'");
            throw new RuntimeException();
        } else {
            int i = 0;
            HashSet<Long> viewedAttractions = session.getViewedAttractions();
            LinkedList<FoundTourismAttraction> attractionsToSendBack = new LinkedList<>();
            while (i < 5 && !foundTourismAttractions.isEmpty()) {
                i++;
                FoundTourismAttraction foundTourismAttraction = foundTourismAttractions.remove(0);
                searchRes.decNumberOfAttractions();
                viewedAttractions.add(foundTourismAttraction.id);
                attractionsToSendBack.add(foundTourismAttraction);
            }

            for (FoundTourismAttraction attractionToSendBack : attractionsToSendBack) {
                TouristAttraction touristAttraction = storage.getAttractionData(attractionToSendBack.id);
                SendMessage sendMessage = buildAttractionsMessage(chatId, session.getLanguageMessages().getLanguage(), attractionToSendBack, touristAttraction);
                response.add(sendMessage);
            }
            if (foundTourismAttractions.isEmpty()) {
                searchRes.groupedAttractions.remove(tourismType);
                log.info("[user_id:" + userId + "] " + "No attractions in this radius can be send back to user for type: " + tourismType);
            }

            if (searchRes.groupedAttractions.isEmpty() && searchRes.distance >= botConf.maxSearchDistanceMeters) {
                log.info("[user_id:" + userId + "] " + "Session is reset");
                session.resetSession();
                response.add(buildMaxDistanceReachedMessage(chatId,session.getLanguageMessages().getLanguage(), botConf.maxSearchDistanceMeters));
                response.add(buildNoMorePlacesAvailableSendAnotherLocationMessage(chatId,session.getLanguageMessages().getLanguage()));
            } else {
                response.add(sendRequestTourismType(
                        chatId,
                        searchRes.groupedAttractions,
                        session.getLanguageMessages().getLanguage(),
                        session.sessionId,
                        searchRes.distance,
                        botConf.maxSearchDistanceMeters));
                String sendIds = attractionsToSendBack.stream().map(e -> e.id + "").collect(Collectors.joining(","));
                log.info("[user_id:" + userId + "] " + "Send attractions back to user [" + sendIds + "]");
            }


        }
        return response;
    }

    public List<BotApiMethodMessage> processTypedLanguageAction(Session session, Message message) {
        long userId = session.getClientInfo().userId;
        String text = message.getText();
        log.info("[user_id:" + userId + "] " + "Received message to change lan to '" + text + "'");
        String lanStr = text.substring(1);
        return getLanguageChangedMessage(session, Language.valueOf(lanStr.toUpperCase()));
    }

    public List<BotApiMethodMessage> processCallbackLanguageAction(Session session, CallbackQuery callbackQuery) {
        long userId = session.getClientInfo().userId;
        CallbackLanBtn callbackLanBtn = CallbackLanBtn.parseButton(callbackQuery.getData());
        log.info("[user_id:" + userId + "] " + "Received callback to change lan to '" + callbackLanBtn.language + "'");
        return getLanguageChangedMessage(session, callbackLanBtn.language);
    }

    private List<BotApiMethodMessage> getLanguageChangedMessage(Session session, Language language) {
        long userId = session.getClientInfo().userId;
        log.info("[user_id:" + userId + "] " + "Changing language to '" + language.name() + "'");
        session.setLanguageMessages(Language.valueOfDefaultEn(language.name()));

        SendMessage message = buildLanguageChanged(session.getClientInfo().chatId, language.languageMessages.getLanguage());
        List<BotApiMethodMessage> helpMessages = getHelpMessages(session);
        helpMessages.add(0, message);
        return helpMessages;
    }

    public List<BotApiMethodMessage> processUnknownAction(Session session, String step, Update update) {
        long userId = session.getClientInfo().userId;

        log.warn("[user_id:" + userId + "] " + "UNKNOWN ACTION '" + step + "'");
        return new ArrayList<>();
    }

}
