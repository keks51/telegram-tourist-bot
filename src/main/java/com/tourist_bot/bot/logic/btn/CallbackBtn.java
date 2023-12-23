package com.tourist_bot.bot.logic.btn;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;


public class CallbackBtn {

    public static final String KEY_VALUE_DELIMITER = ":";
    public static final String PROPERTIES_DELIMITER = ",";

    public static final String SESSION_ID_PROPERTY = "session_id";
    public static final String BTN_NAME_PROPERTY = "bnt_name";

    public final long sessionId;
    public final CallBackBtnName name;

    protected final Map<String, String> props;

    public CallbackBtn(long sessionId, String btnName) {
        this(sessionId, btnName, new HashMap<>());
    }

    public CallbackBtn(long sessionId, String btnName, Map<String, String> props) {
        this.sessionId = sessionId;
        this.name = CallBackBtnName.valueOf(btnName.toUpperCase());
        this.props = props;
    }

    public CallbackBtn(long sessionId, CallBackBtnName btnName) {
       this(sessionId, btnName, new HashMap<>());
    }

    public CallbackBtn(long sessionId, CallBackBtnName btnName, Map<String, String> props) {
        this.sessionId = sessionId;
        this.name = btnName;
        this.props = props;
    }

    public String toBtnStr() {
        StringBuilder sb = new StringBuilder();
        sb.append(SESSION_ID_PROPERTY).append(KEY_VALUE_DELIMITER).append(sessionId);
        sb.append(PROPERTIES_DELIMITER);
        sb.append(BTN_NAME_PROPERTY).append(KEY_VALUE_DELIMITER).append(name.name().toLowerCase());
        if (props.isEmpty()) {
            return sb.toString();
        } else {
            for (Map.Entry<String, String> stringStringEntry : props.entrySet()) {
                sb.append(PROPERTIES_DELIMITER);
                sb.append(stringStringEntry.getKey()).append(KEY_VALUE_DELIMITER).append(stringStringEntry.getValue());
            }
            return sb.toString();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CallbackBtn btn = (CallbackBtn) o;
        return sessionId == btn.sessionId && name == btn.name && Objects.equals(props, btn.props);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sessionId, name, props);
    }

    public static CallbackBtn parseButton(String btnText) {
        String[] splits = btnText.split(PROPERTIES_DELIMITER);
        Map<String, String> properties = new HashMap<>();
        long sessionId = 0;
        CallBackBtnName btn = null;
        for (String split : splits) {
            String[] kv = split.split(KEY_VALUE_DELIMITER);
            String key = kv[0];
            String value = kv[1];
            if (key.equals(SESSION_ID_PROPERTY)) {
                sessionId = Long.parseLong(value);
            } else if (key.equals(BTN_NAME_PROPERTY)) {
                btn = CallBackBtnName.valueOf(value.toUpperCase());
            } else {
                properties.put(key, value);
            }
        }

        return new CallbackBtn(sessionId, btn, properties);
    }

}
