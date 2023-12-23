package com.tourist_bot.bot.logic;


import com.tourist_bot.bot.logic.btn.CallbackBtn;
import com.tourist_bot.bot.logic.language.Language;
import com.tourist_bot.bot.session.Session;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


public class ClientActions {

    public final static String RECEIVED_NEW_USER_ACTION = "new_user_action";
    public final static String RECEIVED_LOCATION_ACTION = "received_location_action";
    public final static String RECEIVED_TOURISM_CALLBACK_ACTION = "received_tourism_callback_action";
    public final static String RECEIVED_LOAD_MORE_CALLBACK_ACTION = "received_load_more_callback_action";
    public final static String RECEIVED_TYPED_HELP_ACTION = "received_typed_help_action";
    public final static String RECEIVED_OUTDATED_CALLBACK_ACTION = "received_outdated_callback_action";
    public final static String HELP_TEXT_COMMAND = "/help";
    public final static String RECEIVED_TYPED_LANGUAGE_ACTION = "received_typed_language_action";
    public final static String RECEIVED_LANGUAGE_CALLBACK_ACTION = "received_language_callback_action";
    public final static String RECEIVED_HELP_CALLBACK_ACTION = "received_help_callback_action";
    public final static String RECEIVED_TYPED_RESET_ACTION = "received_typed_reset_action";
    public final static String RESET_TEXT_COMMAND = "/reset";
    public final static String RECEIVED_UNKNOWN_ACTION = "received_unknown_action";


    public static String getAction(Update update, Session session, boolean isNewSession) {
        if (isNewSession) {
            return RECEIVED_NEW_USER_ACTION;
        }
        if (update.hasMessage()) {
            return processMessage(update.getMessage());
        }
        if (update.hasCallbackQuery()) {
            return processCallback(update.getCallbackQuery(), session.sessionId);
        }
        return RECEIVED_UNKNOWN_ACTION;
    }

    private static String processMessage(Message message) {
        if (message.hasText()) {
            for (Language value : Language.values()) {
                String lan = value.name().toLowerCase();
                if (message.getText().toLowerCase().equals("/" + lan)) {
                    return RECEIVED_TYPED_LANGUAGE_ACTION;
                }
            }
            if (message.getText().equals(RESET_TEXT_COMMAND)) {
                return RECEIVED_TYPED_RESET_ACTION;
            } else if (message.getText().equals(HELP_TEXT_COMMAND)) {
                return RECEIVED_TYPED_HELP_ACTION;
            }
        } else if (message.hasLocation()) {
            return RECEIVED_LOCATION_ACTION;
        }

        return RECEIVED_UNKNOWN_ACTION;
    }

    private static String processCallback(CallbackQuery callbackQuery, long currentSessionId) {
        String call_data = callbackQuery.getData();
        CallbackBtn button = CallbackBtn.parseButton(call_data);
        if (currentSessionId != button.sessionId) return RECEIVED_OUTDATED_CALLBACK_ACTION;

        switch (button.name) {
            case LAN:
                return RECEIVED_LANGUAGE_CALLBACK_ACTION;
            case TOURISM:
                return RECEIVED_TOURISM_CALLBACK_ACTION;
            case HELP:
                return RECEIVED_HELP_CALLBACK_ACTION;
            case LOAD_MORE:
                return RECEIVED_LOAD_MORE_CALLBACK_ACTION;
        }

        return RECEIVED_UNKNOWN_ACTION;
    }

}
