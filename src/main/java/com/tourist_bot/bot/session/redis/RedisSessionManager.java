package com.tourist_bot.bot.session.redis;

import com.tourist_bot.bot.logic.ClientInfo;
import com.tourist_bot.bot.logic.language.Language;
import com.tourist_bot.bot.session.Session;
import com.tourist_bot.bot.session.SessionManager;
import org.redisson.Redisson;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;


public class RedisSessionManager extends SessionManager {

    private static final Logger log = LoggerFactory.getLogger(RedisSessionManager.class.getName());

    private final RedissonClient client;
    public RedisSessionManager(String host, int port, String password, Duration sessionTimeout) {
       super(sessionTimeout);
        Config config = new Config();
        String redisUrl = "redis://" + host + ":" + port;
        config.useSingleServer()
                .setPassword(password)
                .setAddress(redisUrl);

        this.client = Redisson.create(config);
        log.info("Connected to Redis: '" + redisUrl + "'");
    }

    @Override
    public Session getSession(ClientInfo clientInfo) {
        long userId = clientInfo.userId;
        RLock lock = client.getLock("lock" + userId);
        lock.lock();
        RBucket<RedisSession> bucket = client.getBucket("session" + userId);
        bucket.setIfAbsent(new RedisSession(clientInfo.languageCode, lock, sessionTimeout));
        RedisSession redisSession = bucket.get();
        redisSession.setLanguageMessages(Language.valueOfDefaultEn(redisSession.getLanguage()));
        redisSession.setLock(lock);
        redisSession.setBucket(bucket);
        redisSession.setClientInfo(clientInfo);
        return redisSession;
    }

    @Override
    public int cleanOldSessions() {
        return 0;
    }

    @Override
    public void close() throws Exception {
        client.shutdown();
    }

}
