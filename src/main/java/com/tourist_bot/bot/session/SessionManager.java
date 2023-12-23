package com.tourist_bot.bot.session;

import com.tourist_bot.bot.logic.ClientInfo;

import java.time.Duration;


public abstract class SessionManager implements AutoCloseable {


    public final Duration sessionTimeout;

    public SessionManager(Duration sessionTimeout) {
        this.sessionTimeout = sessionTimeout;
    }

    public abstract Session getSession(ClientInfo clientInfo);

    public abstract int cleanOldSessions();

}
