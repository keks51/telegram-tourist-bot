package com.tourist_bot.bot.session.embedded;


import com.tourist_bot.bot.session.Session;

import java.util.concurrent.locks.ReentrantLock;


public class EmbeddedSession extends Session {

    public final ReentrantLock lock = new ReentrantLock();

    public EmbeddedSession(String language) {
        super(language);
    }


    @Override
    public void close() {
        lock.unlock();
    }

}
