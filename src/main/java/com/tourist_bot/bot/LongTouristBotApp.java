package com.tourist_bot.bot;


import com.tourist_bot.bot.conf.BotConfYaml;
import com.tourist_bot.bot.conf.ConfLoader;
import com.tourist_bot.bot.logic.TouristBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.io.IOException;
import java.util.List;


public class LongTouristBotApp extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(LongTouristBotApp.class.getName());

    private final TouristBot touristBot;


    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            String envVar = "CONF_PATH";
            log.info("Reading env var: '" + envVar + "'");
            String confPath = System.getenv(envVar);
            log.info("conf yaml path is '" + confPath + "'");
            BotConfYaml confYaml = ConfLoader.load(confPath);
            log.info("Yaml conf: " + confYaml);

            telegramBotsApi.registerBot(new LongTouristBotApp(confYaml));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public LongTouristBotApp(BotConfYaml confYaml) throws IOException {
        super(confYaml.getApp().getToken());
        this.touristBot = new TouristBot(confYaml);
    }

    @Override
    public void onUpdateReceived(Update update) {
        try {
            List<BotApiMethodMessage> response = touristBot.handleUpdate(update);
            if (response != null) {
                for (BotApiMethodMessage botApiMethodMessage : response) {
                    execute(botApiMethodMessage);
                }
            }
        } catch (Throwable e) {
            log.error("Cannot process input: \n" + update, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "keks";
    }

}
