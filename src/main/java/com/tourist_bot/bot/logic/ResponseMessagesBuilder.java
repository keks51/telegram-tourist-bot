package com.tourist_bot.bot.logic;

import com.tourist_bot.bot.logic.btn.CallBackBtnName;
import com.tourist_bot.bot.logic.btn.CallbackBtn;
import com.tourist_bot.bot.logic.btn.CallbackLanBtn;
import com.tourist_bot.bot.logic.btn.CallbackTourismBtn;
import com.tourist_bot.bot.logic.language.Language;
import com.tourist_bot.bot.logic.language.LanguageMessages;
import com.tourist_bot.bot.storage.TouristAttraction;
import com.tourist_bot.bot.storage.search.FoundTourismAttraction;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ResponseMessagesBuilder {

    public static SendMessage buildNoAttractionsFoundSendAnotherLocationMessage(long chatId,
                                                                                Language language) {
        LanguageMessages languageMessages = language.languageMessages;
        return buildMessage(chatId, languageMessages.nothingFound() + " " + languageMessages.sendAnotherLocation());
    }

    public static SendMessage buildNoMorePlacesAvailableSendAnotherLocationMessage(long chatId,
                                                                                   Language language) {
        LanguageMessages languageMessages = language.languageMessages;
        return buildMessage(chatId, languageMessages.noMorePlacesAvailable() + " " + languageMessages.sendAnotherLocation());
    }

    private static SendMessage buildMessage(long chatId, String text) {
        SendMessage outputMessage = new SendMessage();
        outputMessage.setChatId(chatId);
        outputMessage.setText(text);
        return outputMessage;
    }

    private static InlineKeyboardButton getLoadMoreBtn(String btnText, long sessionId) {
        InlineKeyboardButton loadMoreBtn = new InlineKeyboardButton();
        loadMoreBtn.setText(btnText);

        loadMoreBtn.setCallbackData(new CallbackBtn(sessionId, CallBackBtnName.LOAD_MORE).toBtnStr());
        return loadMoreBtn;
    }

    private static InlineKeyboardButton getHelpBtn(long sessionId) {
        InlineKeyboardButton loadMoreBtn = new InlineKeyboardButton();
        loadMoreBtn.setText("HELP");
        loadMoreBtn.setCallbackData(new CallbackBtn(sessionId, CallBackBtnName.HELP).toBtnStr());
        return loadMoreBtn;
    }

    public static SendMessage buildLanguageChanged(long chatId, Language language) {
        LanguageMessages languageMessages = language.languageMessages;
        SendMessage outputMessage = new SendMessage();
        outputMessage.setChatId(chatId);
        outputMessage.setText(languageMessages.getLanguageWasChanged());
        return outputMessage;
    }

    public static SendMessage sendHelloForNewUser(long chatId,
                                                  Language language,
                                                  int sessionTimeoutMin,
                                                  int maxSearchDist) {
        LanguageMessages languageMessages = language.languageMessages;
        SendMessage outputMessage = new SendMessage();
        outputMessage.setChatId(chatId);
        outputMessage.setText(languageMessages.getHelloMessage(sessionTimeoutMin, maxSearchDist));


        ReplyKeyboardMarkup markup = new ReplyKeyboardMarkup();
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow row = new KeyboardRow();


        KeyboardButton keyboardButton = new KeyboardButton();
        keyboardButton.setText(languageMessages.getHelloMessageLocationBtn());
        keyboardButton.setRequestLocation(true);

        row.add(keyboardButton);
        keyboard.add(row);


        markup.setKeyboard(keyboard);
        markup.setResizeKeyboard(true);

        outputMessage.setReplyMarkup(markup);


        outputMessage.setParseMode(ParseMode.MARKDOWNV2);

        return outputMessage;
    }

    public static SendMessage sendOfferToChangeLanguage(long chatId,
                                                        long sessionId) {
        SendMessage outputMessage = new SendMessage();
        outputMessage.setChatId(chatId);
        StringBuilder sb = new StringBuilder();

        for (Language value : Language.values()) {
            sb.append(value.languageMessages.offerToChangeLanguage());
            sb.append("\n");
        }
        outputMessage.setText(sb.toString());

        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        List<InlineKeyboardButton> line = new ArrayList<>();
        int i = 0;
        for (Language value : Language.values()) {
            InlineKeyboardButton btn = new InlineKeyboardButton();
            btn.setText(value.name());
            CallbackLanBtn callbackLanBtn = new CallbackLanBtn(sessionId, value);
            btn.setCallbackData(callbackLanBtn.toBtnStr());
            line.add(btn);

            i++;
            if (i % 2 == 0) {
                rowsInline.add(line);
                line = new ArrayList<>();
            }
        }
        if (!line.isEmpty()) {
            rowsInline.add(line);
        }

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        markupInline.setKeyboard(rowsInline);
        outputMessage.setReplyMarkup(markupInline);
        return outputMessage;
    }

    public static SendMessage buildMaxDistanceReachedMessage(long chatId,
                                                             Language language,
                                                             int distance) {
        LanguageMessages languageMessages = language.languageMessages;
        return buildMessage(chatId, languageMessages.maxRadiusIsReached() + " " + distance + " " + languageMessages.getMeters());
    }

    public static SendMessage buildSelectTourismTypeMessage(long chatId,
                                                             Language language,
                                                             int distance) {
        LanguageMessages languageMessages = language.languageMessages;
        return buildMessage(chatId,
                languageMessages.availableAttractionsInRadius() + " " + distance + " " +
                        languageMessages.getMeters() + "\n" +
                        languageMessages.getSendRequestTourismTypeMessage());
    }

    public static SendMessage sendRequestTourismType(long chatId,
                                                     Map<Integer, ArrayList<FoundTourismAttraction>> touristAttractions,
                                                     Language language,
                                                     long sessionId,
                                                     int distance,
                                                     int maxSearchDistance) {
        LanguageMessages languageMessages = language.languageMessages;
        SendMessage outputMessage = buildSelectTourismTypeMessage(chatId, language, distance);

        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();


        for (Map.Entry<Integer, ArrayList<FoundTourismAttraction>> entry : touristAttractions.entrySet()) {
            int tourismTypeId = entry.getKey();
            int cnt = entry.getValue().size();
            InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();

            String tourismName = TourismType.getByTypeId(tourismTypeId).getByLan(language);
            inlineKeyboardButton.setText(tourismName + ": " + cnt);

            CallbackTourismBtn callbackBtn = new CallbackTourismBtn(sessionId, tourismTypeId);

            inlineKeyboardButton.setCallbackData(callbackBtn.toBtnStr());
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(inlineKeyboardButton);
            rowsInline.add(rowInline);
        }

        if (maxSearchDistance > distance) {
            InlineKeyboardButton loadMoreBtn = getLoadMoreBtn(languageMessages.getLoadMoreBtnName(), sessionId);
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            rowInline.add(loadMoreBtn);
            rowsInline.add(rowInline);
        }


        rowsInline.add(new ArrayList<>() {{
            add(getHelpBtn(sessionId));
        }});
        markupInline.setKeyboard(rowsInline);
        outputMessage.setReplyMarkup(markupInline);
        return outputMessage;

    }

    public static SendMessage buildAttractionsMessage(long chatId,
                                                      Language language,
                                                      FoundTourismAttraction foundTourismAttraction,
                                                      TouristAttraction touristAttraction) {
        LanguageMessages languageMessages = language.languageMessages;
        SendMessage outputMessage = new SendMessage();
        outputMessage.setChatId(chatId);
        String text = parseAttractionsToTelegramText(language, foundTourismAttraction, touristAttraction);
        String escaped = escapeTextForMD2(text);
        String escapedWithYandex = escaped
                + "Yandex maps: [" + languageMessages.viewOnYandexMaps() + "](https://yandex.ru/maps/2/saint-petersburg/?text="
                + foundTourismAttraction.lat + "%2C" + foundTourismAttraction.lon + ")\n";
        outputMessage.setText(escapedWithYandex);
        outputMessage.setParseMode(ParseMode.MARKDOWNV2);
        return outputMessage;
    }

    public static String parseAttractionsToTelegramText(Language language,
                                                        FoundTourismAttraction foundTourismAttraction,
                                                        TouristAttraction touristAttraction) {
        LanguageMessages languageMessages = language.languageMessages;
        String tourismName = touristAttraction.tourismType.getByLan(language);
        StringBuilder sb = new StringBuilder();
        sb
                .append(languageMessages.attractionName() + ": " + touristAttraction.lanNameMap.get(language) + "\n")
                .append(languageMessages.tourismName() + ": " + tourismName + "\n");
        if (!touristAttraction.lanDescMap.get(language).isEmpty()) {
            sb.append(languageMessages.attractionDesc() + ": " + touristAttraction.lanDescMap.get(language) + "\n");
        }
        sb.append(languageMessages.distanceFromYou() + ": " + (int) foundTourismAttraction.dist).append("\n");
        return sb.toString();
    }

    public static String[] escapeCharsMD2 = new String[]{"_", "*", "[", "]", "(", ")", "~", "`", ">", "#", "+", "-", "=", "|", "{", "}", ".", "!"};

    public static String escapeTextForMD2(String text) {
        for (String escapeChar : escapeCharsMD2) {
            text = text.replace(escapeChar, "\\" + escapeChar);
        }
        return text;
    }

}
