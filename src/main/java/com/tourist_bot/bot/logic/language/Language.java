package com.tourist_bot.bot.logic.language;


public enum Language {

    RU(new RuLanguage()),
    EN(new EnLanguage());

    public final LanguageMessages languageMessages;

    Language(LanguageMessages languageMessages) {
        this.languageMessages = languageMessages;
    };

    public static Language valueOfDefaultEn(String lanStr){
        try {
            return Language.valueOf(lanStr.toUpperCase());
        } catch (Throwable e) {
            return Language.EN;
        }
    }

}
