package com.tourist_bot.bot.logic;

import com.tourist_bot.bot.session.Session;
import com.tourist_bot.bot.session.SessionManager;
import com.tourist_bot.bot.storage.quad_storage.EmbeddedGeoQuadTreeGeoStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.tourist_bot.bot.logic.ClientActions.*;


public class RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class.getName());

    private final SessionManager sessionManager;

    private final RequestActionService requestActionService;

    public RequestHandler(EmbeddedGeoQuadTreeGeoStorage storage,
                          SessionManager sessionManager,
                          BotConf botConf) {
        this.sessionManager = sessionManager;
        this.requestActionService = new RequestActionService(botConf, storage);
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledExecutorService.scheduleWithFixedDelay(() -> {
            log.info("Cleaning sessions");
            int cleanedCount = sessionManager.cleanOldSessions();

            log.info("Cleaned " + cleanedCount + " sessions");
        }, 0, botConf.sessionCleanUpPeriodSec, TimeUnit.SECONDS);
    }

    public static long getMessageDateSec(Update update) {
        if (update.hasMessage()) {
            return update.getMessage().getDate();
        } else if (update.hasCallbackQuery()) {
            return update.getCallbackQuery().getMessage().getDate();
        } else {
            log.error("Unknown message " + update);
            return System.currentTimeMillis() / 1000;
        }

    }

    public List<BotApiMethodMessage> process(Update update) throws Exception {
        ClientInfo clientInfo = ClientInfo.getClientInfo(update);
        long messageDateSeconds = getMessageDateSec(update);
        long userId = clientInfo.userId;
        if (System.currentTimeMillis() / 1000 - messageDateSeconds > 600) {
            log.warn("[user_id:" + userId + "] " + "Skipping since request is too old (passed more than 600 seconds)");
            return new ArrayList<>();
        }

        try (Session userSession = sessionManager.getSession(clientInfo)) {
            boolean isNewSession = userSession.getLastUsedTimeSec() == 0;
            userSession.setLastUsedTimeSec(System.currentTimeMillis() / 1000);
            log.info("[user_id:" + userId + "] " + "Received update from chat: " + clientInfo.chatId);

            String action = getAction(update, userSession, isNewSession);
            log.info("[user_id:" + userId + "] " + "received action " + action);

            switch (action) {
                case RECEIVED_NEW_USER_ACTION:
                    return requestActionService.processNewUserAction(userSession);

                case RECEIVED_TYPED_HELP_ACTION:
                    return requestActionService.processTypedHelpAction(userSession);

                case RECEIVED_OUTDATED_CALLBACK_ACTION:
                    return requestActionService.processOutdatedCallbackAction(userSession, update.getCallbackQuery());

                case RECEIVED_HELP_CALLBACK_ACTION:
                    return requestActionService.processHelpCallbackAction(userSession);

                case RECEIVED_LOCATION_ACTION:
                    return requestActionService.processTypedLocationAction(userSession, update.getMessage());

                case RECEIVED_LOAD_MORE_CALLBACK_ACTION:
                    return requestActionService.processLoadMoreCallback(userSession);

                case RECEIVED_TOURISM_CALLBACK_ACTION:
                    return requestActionService.processCallbackTourismAction(userSession, update.getCallbackQuery());

                case RECEIVED_TYPED_RESET_ACTION:
                    return requestActionService.processTypedResetStep(userSession);

                case RECEIVED_TYPED_LANGUAGE_ACTION:
                    return requestActionService.processTypedLanguageAction(userSession, update.getMessage());

                case RECEIVED_LANGUAGE_CALLBACK_ACTION:
                    return requestActionService.processCallbackLanguageAction(userSession, update.getCallbackQuery());

                case RECEIVED_UNKNOWN_ACTION:
                    return requestActionService.processUnknownAction(userSession, action, update);

                default:
                    log.error("[user_id:" + userId + "] " + " unknown action '" + action + "'");
                    return new ArrayList<>();

            }
        } catch (Throwable e) {
            log.error("[user_id:" + userId + "] " + " Cannot process request", e);
            throw e;
        }
    }

}
