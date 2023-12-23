package com.tourist_bot.bot.session;


import com.tourist_bot.bot.logic.ClientInfo;
import com.tourist_bot.bot.logic.language.Language;
import com.tourist_bot.bot.logic.language.LanguageMessages;
import com.tourist_bot.bot.storage.search.SearchRes;
import org.telegram.telegrambots.meta.api.objects.Location;

import java.util.HashMap;
import java.util.HashSet;


public abstract class Session implements AutoCloseable {

    public final long sessionId = System.currentTimeMillis();
    private boolean isRemoved = false;
    private String language;
    private LanguageMessages languageMessages;
    private Location location;
    private SearchRes lastSearch;
    private long lastUsedTimeSec;
    private ClientInfo clientInfo;
    private final HashSet<Long> viewedAttractions = new HashSet<>();

    public Session(String language) {
        this.language = language;
    }

    public void setLastSearch(SearchRes lastSearch) {
        this.lastSearch = lastSearch;
    }

    public int getLastSearchDistance() {
        return lastSearch.distance;
    }

    public SearchRes getLastSearch() {
        return lastSearch;
    }

    public long getLastUsedTimeSec() {
        return lastUsedTimeSec;
    }

    public void setLastUsedTimeSec(long lastUsedTimeSec) {
        this.lastUsedTimeSec = lastUsedTimeSec;
    }

    public void setLanguageMessages(Language language) {
        this.language = language.name();
        this.languageMessages = language.languageMessages;
    }

    public LanguageMessages getLanguageMessages() {
        return languageMessages;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void resetSession() {
        viewedAttractions.clear();
        location = null;
        lastSearch = new SearchRes(new HashMap<>(), 0, 0);
    }

    public HashSet<Long> getViewedAttractions() {
        return viewedAttractions;
    }

    public void setClientInfo(ClientInfo clientInfo) {
        this.clientInfo = clientInfo;
    }

    public ClientInfo getClientInfo() {
        return this.clientInfo;
    }

    @Override
    public String toString() {
        return "Session{" +
                "sessionId=" + sessionId +
                ", isRemoved=" + isRemoved +
                ", language='" + language + '\'' +
                ", location=" + location +
                ", lastSearch=" + lastSearch +
                ", lastUsedTimeSec=" + lastUsedTimeSec +
                ", viewedAttractions=" + viewedAttractions +
                '}';
    }

    public String getLanguage() {
        return language;
    }

    public boolean isRemoved() {
        return isRemoved;
    }

    public void setWasRemoved() {
        isRemoved = true;
    }


}
