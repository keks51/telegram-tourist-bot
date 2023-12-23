package com.tourist_bot.bot.logic.language;


public class RuLanguage implements LanguageMessages {


    @Override
    public Language getLanguage() {
        return Language.RU;
    }


    @Override
    public String offerToChangeLanguage() {
        return "Хотите изменить язык?";
    }

    @Override
    public String getHelloMessage(int sessionTimeoutMin, int maxSearchDist) {
        return "Туристический бот\n"
                + "To change language to English type '/en'\n"
                + "Чтобы изменить язык на Русский введите '/ru'\n"
                + "Поиск туристических мест рядом с вами\n"
                + "Инструкция:\n"
                + "  \\-Прикрепите местоположение \n"
                + "  \\-Нажмите на предложенную туристическую кнопку или запросите еще\n"
                + "  \\-Чтобы начать поиск заново, необходимо отправить новое местоположение\n"
                + "  \\-По истчению "+ sessionTimeoutMin + " мин вся найденная информация становится неактуальной и \n"
                + "  \\необходимо заново отправить местоположение\n"
                + "  \\-Максимальный радиус поиска " + maxSearchDist + " метров\n"
                + "Если вы столкнулись с некорректной работой бота то отправьте команду '/reset'";
    }

    @Override
    public String getLanguageWasChanged() {
        return "Язык был изменен на Русский";
    }

    @Override
    public String getHelloMessageLocationBtn() {
        return "Отправить мое местоположение";
    }


    @Override
    public String getSendRequestTourismTypeMessage() {
        return "Выберите вариант туризма";
    }

    @Override
    public String attractionName() {
        return "Название";
    }

    @Override
    public String attractionDesc() {
        return "Описание";
    }

    @Override
    public String tourismName() {
        return "Туризм";
    }

    @Override
    public String distanceFromYou() {
        return "Расстояние по прямой";
    }

    @Override
    public String viewOnYandexMaps() {
        return "посмотреть в Яндекс Картах";
    }

    @Override
    public String incorrectRequest() {
        return "Некорректный запрос";
    }

    @Override
    public String nothingFound() {
        return "Ничего не нашли.";
    }

    @Override
    public String noMorePlacesAvailable() {
        return "Больше нет туристических мест рядом";
    }

    @Override
    public String sendAnotherLocation() {
        return "Отправьте другую локацию.";
    }

    @Override
    public String getLoadMoreBtnName() {
        return "Показать еще?";
    }

    @Override
    public String availableAttractionsInRadius() {
        return "Доступные места в радиусе";
    }

    @Override
    public String maxRadiusIsReached() {
        return "Достигнут максимальный радиус поиска";
    }

    @Override
    public String getMeters() {
        return "метров";
    }

}
