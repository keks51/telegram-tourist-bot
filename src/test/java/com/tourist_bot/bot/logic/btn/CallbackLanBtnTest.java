package com.tourist_bot.bot.logic.btn;

import com.tourist_bot.bot.logic.language.Language;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


class CallbackLanBtnTest {

    @Test
    public void test1() {
        CallbackLanBtn callbackLanBtn = new CallbackLanBtn(1, Language.RU);
        String btnStr = callbackLanBtn.toBtnStr();
        assertEquals("session_id:1,bnt_name:lan,lan_prop:ru", btnStr);

        CallbackLanBtn parsedBtn = CallbackLanBtn.parseButton(btnStr);

        assertEquals(callbackLanBtn, parsedBtn);
    }

}