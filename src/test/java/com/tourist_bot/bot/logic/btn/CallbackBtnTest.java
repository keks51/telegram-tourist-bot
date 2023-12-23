package com.tourist_bot.bot.logic.btn;

import org.junit.jupiter.api.Test;

import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CallbackBtnTest {

    @Test
    public void test1() {
        CallbackBtn button = new CallbackBtn(1, CallBackBtnName.HELP);
        String btnAsStr = button.toBtnStr();
        assertEquals("session_id:1,bnt_name:help", btnAsStr);

        CallbackBtn parsedButton = CallbackBtn.parseButton(btnAsStr);

        assertEquals(button, parsedButton);
    }

    @Test
    public void test2() {
        CallbackBtn button = new CallbackBtn(1, CallBackBtnName.HELP, new TreeMap<>() {{
            put("lan", "ru");
            put("type", "2");
        }});
        String btnAsStr = button.toBtnStr();
        assertEquals("session_id:1,bnt_name:help,lan:ru,type:2", btnAsStr);

        CallbackBtn parsedButton = CallbackBtn.parseButton(btnAsStr);

        assertEquals(button, parsedButton);
    }

    @Test
    public void test3() {
        CallbackBtn button = new CallbackBtn(1, CallBackBtnName.HELP.name());
        String btnAsStr = button.toBtnStr();
        assertEquals("session_id:1,bnt_name:help", btnAsStr);

        CallbackBtn parsedButton = CallbackBtn.parseButton(btnAsStr);

        assertEquals(button, parsedButton);
    }

    @Test
    public void test4() {
        CallbackBtn button = new CallbackBtn(1, CallBackBtnName.HELP.name(), new TreeMap<>() {{
            put("lan", "ru");
            put("type", "2");
        }});
        String btnAsStr = button.toBtnStr();
        assertEquals("session_id:1,bnt_name:help,lan:ru,type:2", btnAsStr);

        CallbackBtn parsedButton = CallbackBtn.parseButton(btnAsStr);

        assertEquals(button, parsedButton);
    }

}
