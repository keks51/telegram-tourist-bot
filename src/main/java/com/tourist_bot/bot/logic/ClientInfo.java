package com.tourist_bot.bot.logic;

import com.tourist_bot.bot.logic.language.Language;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class ClientInfo {
    public final long chatId;
    public final long userId;
    private final String userName;
    private final String firstName;
    private final String lastName;
    public final String  languageCode;

    public ClientInfo(long chatId, long userId, String userName, String firstName, String lastName, String languageCode) {
        this.chatId = chatId;
        this.userId = userId;
        this.userName = userName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.languageCode = languageCode;
    }

    @Override
    public String toString() {
        return "ClientInfo{" +
                "chatId=" + chatId +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                '}';
    }

    public static ClientInfo getClientInfo(Update update) {
        if (update.hasMessage()) {
            Message message = update.getMessage();
            User from = message.getFrom();
            String languageCode = from.getLanguageCode();
            return new ClientInfo(message.getChatId(), from.getId(), from.getUserName(), from.getFirstName(), from.getLastName(), languageCode);
        } else if (update.hasCallbackQuery()) {
            Message message = update.getCallbackQuery().getMessage();
            User from = update.getCallbackQuery().getFrom();
            String languageCode = from.getLanguageCode();
            return new ClientInfo(message.getChatId(), from.getId(), from.getUserName(), from.getFirstName(), from.getLastName(), languageCode);
        } else {
            return new ClientInfo(-1, -1, "", "", "", "EN");
        }
    }


}
