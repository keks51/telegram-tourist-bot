package com.tourist_bot.bot.logic;

import com.tourist_bot.bot.logic.btn.CallBackBtnName;
import com.tourist_bot.bot.logic.btn.CallbackBtn;
import com.tourist_bot.bot.logic.language.Language;
import com.tourist_bot.bot.session.Session;
import com.tourist_bot.bot.session.embedded.EmbeddedSession;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static com.tourist_bot.bot.logic.ClientActions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


class ClientActionsTest {

    @Test
    public void testNewUser1() {
        Update update = new Update();
        Session session = new EmbeddedSession("RU");
        String action = ClientActions.getAction(update, session, true);

        assertEquals(RECEIVED_NEW_USER_ACTION, action);
    }

    @Test
    public void testNewUser2() {
        Update update = new Update();
        Session session = new EmbeddedSession("RU");
        String action = ClientActions.getAction(update, session, false);

        assertEquals(RECEIVED_UNKNOWN_ACTION, action);
    }

    @Test
    public void testTypedLanguage1() {
        Update update = new Update();
        Message message = new Message();
        message.setText("/" + Language.RU.name().toLowerCase());
        update.setMessage(message);
        Session session = new EmbeddedSession("RU");
        String action = ClientActions.getAction(update, session, false);

        assertEquals(RECEIVED_TYPED_LANGUAGE_ACTION, action);
    }

    @Test
    public void testTypedLanguage2() {
        Update update = new Update();
        Message message = new Message();
        message.setText("/" + Language.EN.name().toLowerCase());
        update.setMessage(message);
        Session session = new EmbeddedSession("RU");
        String action = ClientActions.getAction(update, session, false);

        assertEquals(RECEIVED_TYPED_LANGUAGE_ACTION, action);
    }

    @Test
    public void testTypedLanguage3() {
        Update update = new Update();
        Message message = new Message();
        message.setText("/" + "haha");
        update.setMessage(message);
        Session session = new EmbeddedSession("RU");
        String action = ClientActions.getAction(update, session, false);

        assertEquals(RECEIVED_UNKNOWN_ACTION, action);
    }

    @Test
    public void testTypedReset1() {
        Update update = new Update();
        Message message = new Message();
        message.setText(RESET_TEXT_COMMAND);
        update.setMessage(message);
        Session session = new EmbeddedSession("RU");
        String action = ClientActions.getAction(update, session, false);

        assertEquals(RECEIVED_TYPED_RESET_ACTION, action);
    }

    @Test
    public void testTypedHelp1() {
        Update update = new Update();
        Message message = new Message();
        message.setText(HELP_TEXT_COMMAND);
        update.setMessage(message);
        Session session = new EmbeddedSession("RU");
        String action = ClientActions.getAction(update, session, false);

        assertEquals(RECEIVED_TYPED_HELP_ACTION, action);
    }

    @Test
    public void testTypedLocation1() {
        Update update = new Update();
        Message message = new Message();
        Location location = new Location();
        message.setLocation(location);
        update.setMessage(message);
        Session session = new EmbeddedSession("RU");
        String action = ClientActions.getAction(update, session, false);

        assertEquals(RECEIVED_LOCATION_ACTION, action);
    }

    @Test
    public void testUnknownMessage1() {
        Update update = new Update();
        Message message = new Message();
        update.setMessage(message);
        Session session = new EmbeddedSession("RU");
        String action = ClientActions.getAction(update, session, false);

        assertEquals(RECEIVED_UNKNOWN_ACTION, action);
    }

    @Test
    public void testOutdatedBtn1() {
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        CallbackBtn btn = new CallbackBtn(1, "help");
        callbackQuery.setData(btn.toBtnStr());
        update.setCallbackQuery(callbackQuery);
        Session session = new EmbeddedSession("RU");

        String action = ClientActions.getAction(update, session, false);
        assertEquals(RECEIVED_OUTDATED_CALLBACK_ACTION, action);
    }

    @Test
    public void testHelpBtn1() {
        Session session = new EmbeddedSession("RU");
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        CallbackBtn btn = new CallbackBtn(session.sessionId, CallBackBtnName.HELP.name());
        callbackQuery.setData(btn.toBtnStr());
        update.setCallbackQuery(callbackQuery);

        String action = ClientActions.getAction(update, session, false);
        assertEquals(RECEIVED_HELP_CALLBACK_ACTION, action);
    }

    @Test
    public void testLanBtn1() {
        Session session = new EmbeddedSession("RU");
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        CallbackBtn btn = new CallbackBtn(session.sessionId, CallBackBtnName.LAN);
        callbackQuery.setData(btn.toBtnStr());
        update.setCallbackQuery(callbackQuery);

        String action = ClientActions.getAction(update, session, false);
        assertEquals(RECEIVED_LANGUAGE_CALLBACK_ACTION, action);
    }

    @Test
    public void testLoadMoreBtn1() {
        Session session = new EmbeddedSession("RU");
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        CallbackBtn btn = new CallbackBtn(session.sessionId, CallBackBtnName.LOAD_MORE);
        callbackQuery.setData(btn.toBtnStr());
        update.setCallbackQuery(callbackQuery);

        String action = ClientActions.getAction(update, session, false);
        assertEquals(RECEIVED_LOAD_MORE_CALLBACK_ACTION, action);
    }

    @Test
    public void testTourismBtn1() {
        Session session = new EmbeddedSession("RU");
        Update update = new Update();
        CallbackQuery callbackQuery = new CallbackQuery();
        CallbackBtn btn = new CallbackBtn(session.sessionId, CallBackBtnName.TOURISM);
        callbackQuery.setData(btn.toBtnStr());
        update.setCallbackQuery(callbackQuery);

        String action = ClientActions.getAction(update, session, false);
        assertEquals(RECEIVED_TOURISM_CALLBACK_ACTION, action);
    }

}