package com.tourist_bot.bot.logic.language;


public interface LanguageMessages {



    public Language getLanguage();

    public String offerToChangeLanguage();

    public String getHelloMessage(int sessionTimeoutMin, int maxSearchDist);

    public String getLanguageWasChanged();

    public String getHelloMessageLocationBtn();

    public String getSendRequestTourismTypeMessage();

    public String attractionName();

    public String attractionDesc();

    public String tourismName();

    public String distanceFromYou();

    public String viewOnYandexMaps();

    public String incorrectRequest();

    public String nothingFound();

    public String noMorePlacesAvailable();

    public String sendAnotherLocation();

    public String getLoadMoreBtnName();

    public String availableAttractionsInRadius();
    public String maxRadiusIsReached();

    public String getMeters();




}
