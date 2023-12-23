package com.tourist_bot.bot.logic.btn;

import com.tourist_bot.bot.logic.language.Language;


public class CallbackLanBtn extends CallbackBtn {

    public static final String LAN_PROP = "lan_prop";
    public final Language language;


    public CallbackLanBtn(long sessionId, Language language) {
        super(sessionId, CallBackBtnName.LAN);
        this.language = language;
        super.props.put(LAN_PROP, language.name().toLowerCase());
    }

    public static CallbackLanBtn parseButton(String btnText) {
        CallbackBtn callbackBtn = CallbackBtn.parseButton(btnText);
        return new CallbackLanBtn(callbackBtn.sessionId, Language.valueOf(callbackBtn.props.get(LAN_PROP).toUpperCase()));
    }

}
