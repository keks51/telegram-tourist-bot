package com.tourist_bot.bot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class.getName());

    public static void main(String[] args) throws TelegramApiException {
        try {
            String envVar = "SERVER";
            log.info("Reading env var: '" + envVar + "'");
            String server = System.getenv(envVar);
            log.info("server is '" + server + "'");
            if (server.equals("LONG")) {
                LongTouristBotApp.main(args);
            } else if (server.equals("HOOK")) {
                WebHookTouristBotApp.main(args);
            } else {
                throw new IllegalArgumentException("Unknown server " + server);
            }
        } catch (Throwable e) {
            log.error("Critical error", e);
            throw e;
        }

    }

}
