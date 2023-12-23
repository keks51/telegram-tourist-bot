package com.tourist_bot.bot.session.redis;

import com.tourist_bot.bot.session.Session;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;

import java.time.Duration;


public class RedisSession extends Session {

    private transient RLock lock;
    public final Duration timeout;

    private transient RBucket<RedisSession> bucket;

    public RedisSession(String language, RLock lock, Duration timeout) {
        super(language);
        this.lock = lock;
        this.timeout = timeout;
    }

    @Override
    public void close() {
        bucket.set(this, timeout);
        lock.unlock();
    }

    public void setLock(RLock lock) {
        this.lock = lock;
    }

    public void setBucket(RBucket<RedisSession> bucket) {
        this.bucket = bucket;
    }
}
