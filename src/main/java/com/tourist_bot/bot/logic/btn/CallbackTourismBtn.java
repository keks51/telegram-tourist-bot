package com.tourist_bot.bot.logic.btn;


public class CallbackTourismBtn extends CallbackBtn {

    public static final String TOURISM_TYPE_PROP = "tourism_type";
    public final int tourismType;


    public CallbackTourismBtn(long sessionId, int tourismType) {
        super(sessionId, CallBackBtnName.TOURISM);
        this.tourismType = tourismType;
        super.props.put(TOURISM_TYPE_PROP, tourismType + "");
    }

    public static CallbackTourismBtn parseButton(String btnText) {
        CallbackBtn callbackBtn = CallbackBtn.parseButton(btnText);
        return new CallbackTourismBtn(callbackBtn.sessionId, Integer.parseInt(callbackBtn.props.get(TOURISM_TYPE_PROP)));
    }

}
