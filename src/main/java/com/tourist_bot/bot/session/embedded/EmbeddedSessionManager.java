package com.tourist_bot.bot.session.embedded;

import com.tourist_bot.bot.logic.ClientInfo;
import com.tourist_bot.bot.logic.language.Language;
import com.tourist_bot.bot.session.Session;
import com.tourist_bot.bot.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;


public class EmbeddedSessionManager extends SessionManager {
    private static final Logger log = LoggerFactory.getLogger(EmbeddedSessionManager.class.getName());

    ConcurrentSkipListMap<Long, EmbeddedSession> sessions = new ConcurrentSkipListMap<>();

    public EmbeddedSessionManager(Duration sessionTimeout) {
        super(sessionTimeout);
    }

    @Override
    public Session getSession(ClientInfo clientInfo) {
        long userId = clientInfo.userId;
        while (true) {
            if (!sessions.containsKey(userId)) {
                EmbeddedSession userSession = new EmbeddedSession(clientInfo.languageCode);
                userSession.setLanguageMessages(Language.valueOfDefaultEn(userSession.getLanguage()));
                sessions.putIfAbsent(userId, userSession);
            }
            EmbeddedSession embeddedSession = sessions.get(userId);
            embeddedSession.lock.lock();
            if (!embeddedSession.isRemoved()) {
                embeddedSession.setClientInfo(clientInfo);
                return embeddedSession;
            }
            embeddedSession.lock.unlock();
        }
    }

    @Override
    public int cleanOldSessions() {
        int cleanedCount = 0;

        for (Map.Entry<Long, EmbeddedSession> entry : sessions.entrySet()) {
            EmbeddedSession session = entry.getValue();
            try {
                session.lock.lock();
                if (System.currentTimeMillis() / 1000 - session.getLastUsedTimeSec() > sessionTimeout.getSeconds()) {
                    sessions.remove(entry.getKey());
                    session.setWasRemoved();
                    log.info("Session for user " + entry.getKey() + " is removed");
                    cleanedCount++;
                }
            } finally {
                session.lock.unlock();
            }
        }
        return cleanedCount;
    }

    @Override
    public void close() throws Exception {

    }
}
