package com.tourist_bot.bot;

import com.tourist_bot.bot.conf.BotConfYaml;
import com.tourist_bot.bot.conf.ConfLoader;
import com.tourist_bot.bot.logic.TouristBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.botapimethods.BotApiMethodMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultWebhook;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;


public class WebHookTouristBotApp extends TelegramWebhookBot {

    private static final Logger log = LoggerFactory.getLogger(WebHookTouristBotApp.class.getName());
    private final String botName;
    private final String path;
    private final TouristBot touristBot;

    public WebHookTouristBotApp(String botName, String path, BotConfYaml confYaml) throws IOException {
        super(new DefaultBotOptions(), confYaml.getApp().getToken());
        this.botName = botName;
        this.path = path;
        this.touristBot = new TouristBot(confYaml);
    }

    @Override
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {
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
        return null;
    }

    @Override
    public String getBotPath() {
        return path;
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    public static void main(String[] args) throws TelegramApiException {
        try {
            String envVar = "CONF_PATH";
            log.info("Reading env var: '" + envVar + "'");
            String confPath = System.getenv(envVar);
            log.info("conf yaml path is '" + confPath + "'");
            BotConfYaml confYaml = ConfLoader.load(confPath);
            log.info("Yaml conf: " + confYaml);

            DefaultWebhook defaultWebhook = new DefaultWebhook();
            log.info("WebHook local host is '" + confYaml.getWebHook().getLocalHostWebHookUrl() + "'");
            defaultWebhook.setInternalUrl(confYaml.getWebHook().getLocalHostWebHookUrl());
            if (confYaml.getWebHook().getLocalHostWebHookUrl().contains("https")) {
                log.info("Setting jsk cert");
                log.info("KeyStorePath: " + confYaml.getWebHook().getKeyStorePath());
                defaultWebhook.setKeyStore(
                        confYaml.getWebHook().getKeyStorePath(),
                        confYaml.getWebHook().getKeyStorePassword()
                );
            }
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class, defaultWebhook);

            log.info("WebHook public host is '" + confYaml.getWebHook().getPublicHostWebHookUrl() + "'");
            SetWebhook.SetWebhookBuilder builder = SetWebhook.builder()
                    .url(confYaml.getWebHook().getPublicHostWebHookUrl());

            if (confYaml.getWebHook().getLocalHostWebHookUrl().contains("https")) {
                log.info("Sending Pem cert to telegram");
                log.info("Pem cert path: " + confYaml.getWebHook().getPublicPemPath());
                InputFile inputFile = new InputFile(
                        new FileInputStream(confYaml.getWebHook().getPublicPemPath()),
                        "certificate");
                builder.certificate(inputFile);
            }

            SetWebhook publicWebHookHost = builder.build();

            String requestPath = "tourism_bot";
            log.info("All responses from  bot should be mapped to '/callback/" + requestPath + "'");
            WebHookTouristBotApp botHook = new WebHookTouristBotApp("keks", "tourism_bot", confYaml);
            telegramBotsApi.registerBot(botHook, publicWebHookHost);

        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
