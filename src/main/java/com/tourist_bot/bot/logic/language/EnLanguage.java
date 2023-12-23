package com.tourist_bot.bot.logic.language;


public class EnLanguage implements LanguageMessages {

    @Override
    public String getSendRequestTourismTypeMessage() {
        return "Choose tourism type";
    }

    @Override
    public Language getLanguage() {
        return Language.EN;
    }

    @Override
    public String offerToChangeLanguage() {
        return "Would you like to change the language?";
    }

    @Override
    public String getHelloMessage(int sessionTimeoutMin, int maxSearchDist) {
        return "TourismBot\n"
                + "To change language to English type '/en'\n"
                + "Чтобы изменить язык на Русский введите '/ru'\n"
                + "Search for tourist places nearby\n"
                + "Usage:\n"
                + "  \\-Send your location\n"
                + "  \\-Press on tourism type button or request more\n"
                + "  \\-To start a new search send a new location\n"
                + "  \\-After " + sessionTimeoutMin + " mins all found locations are outdated \n"
                + "  \\and new location is required \n"
                + "  \\-Max search is across a radius of " + maxSearchDist + " meters \n"
                + "If you encounter incorrect behavior type '/reset'";
    }

    @Override
    public String getLanguageWasChanged() {
        return "Language is changed to English";
    }

    @Override
    public String getHelloMessageLocationBtn() {
        return "Send my location";
    }

    @Override
    public String attractionName() {
        return "name";
    }

    @Override
    public String attractionDesc() {
        return "desc";
    }

    @Override
    public String tourismName() {
        return "type";
    }

    @Override
    public String distanceFromYou() {
        return "Straight line distance in meters";
    }

    @Override
    public String viewOnYandexMaps() {
        return "view on  yandex map";
    }
    @Override
    public String incorrectRequest() {
        return "INCORRECT REQUEST\\. PLEASE TRY AGAIN FROM THE BEGINNING\\.";
    }

    @Override
    public String nothingFound() {
        return "No palces found in this location.";
    }
    @Override
    public String noMorePlacesAvailable() {
        return "No more places available.";
    }

    @Override
    public String sendAnotherLocation() {
        return "Send new location.";
    }

    @Override
    public String getLoadMoreBtnName() {
        return "load more?";
    }

    @Override
    public String availableAttractionsInRadius() {
        return "Available places in radius";
    }

    @Override
    public String maxRadiusIsReached() {
        return "Max search radius is reached";
    }

    @Override
    public String getMeters() {
        return "meters";
    }

}
