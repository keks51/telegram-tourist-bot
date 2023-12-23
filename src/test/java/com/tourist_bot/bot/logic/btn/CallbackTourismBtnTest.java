package com.tourist_bot.bot.logic.btn;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CallbackTourismBtnTest {

    @Test
    public void test1() {
        CallbackTourismBtn callbackLanBtn = new CallbackTourismBtn(1, 2);
        String btnStr = callbackLanBtn.toBtnStr();
        assertEquals("session_id:1,bnt_name:tourism,tourism_type:2", btnStr);

        CallbackTourismBtn parsedBtn = CallbackTourismBtn.parseButton(btnStr);

        assertEquals(callbackLanBtn, parsedBtn);
    }


}