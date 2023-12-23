package com.tourist_bot.bot.logic;

import com.tourist_bot.bot.conf.BotConfYaml;
import com.tourist_bot.bot.session.SessionManager;
import com.tourist_bot.bot.session.embedded.EmbeddedSessionManager;
import com.tourist_bot.bot.session.redis.RedisSessionManager;
import com.tourist_bot.bot.storage.quad_storage.EmbeddedGeoQuadTreeGeoStorage;
import com.tourist_bot.bot.storage.quad_storage.JsonAttractionsParser;
import com.tourist_bot.bot.storage.quad_storage.QuadTreeAttractions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;


public class TouristBot {

    private static final Logger log = LoggerFactory.getLogger(TouristBot.class.getName());

    private final RequestHandler requestHandler;

    public TouristBot(BotConfYaml conf) throws IOException {

        BotConf botConf = new BotConf(
                conf.getApp().getMaxSearchDistMeters(),
                conf.getApp().getDefaultSearchDistStepMeters(),
                conf.getApp().getSessionTimeoutSec(),
                conf.getApp().getSessionsCleanupPeriodSec());


        log.info("Reading geo json from path: '" + conf.getApp().getGeoJsonPath() + "'");
        QuadTreeAttractions parsedAttractions = JsonAttractionsParser.parse(conf.getApp().getGeoJsonPath());
        EmbeddedGeoQuadTreeGeoStorage storage = new EmbeddedGeoQuadTreeGeoStorage(
                parsedAttractions,
                botConf.searchDistanceStep,
                botConf.maxSearchDistanceMeters);

        SessionManager sessionManager;
        if (conf.getApp().getSessionManager().equals("REDIS")) {
            sessionManager = new RedisSessionManager(
                    conf.getRedis().getHost(),
                    conf.getRedis().getPort(),
                    conf.getRedis().getPass(),
                    Duration.of(botConf.sessionTimeOutSec, ChronoUnit.SECONDS)
            );
        } else if (conf.getApp().getSessionManager().equals("EMBEDDED")) {
            sessionManager = new EmbeddedSessionManager(Duration.of(botConf.sessionTimeOutSec, ChronoUnit.SECONDS));
        } else {
            throw new IllegalArgumentException("Unknown session manager '" + conf.getApp().getSessionManager() + "'");
        }

        this.requestHandler = new RequestHandler(
                storage,
                sessionManager,
                botConf
        );
        log.info("Starting bot");
    }

    public List<BotApiMethodMessage> handleUpdate(Update update) throws Exception {
        return requestHandler.process(update);
    }

}
