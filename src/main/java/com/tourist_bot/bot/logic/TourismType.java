package com.tourist_bot.bot.logic;

import com.tourist_bot.bot.logic.language.Language;

import java.util.HashMap;
import java.util.Map;


public enum TourismType {

    ATTRACTION(1,
            "attraction",
            "развлечение"),
    ARTWORK(2,
            "artwork",
            "произведение искусства"),
    THEME_PARK(3,
            "park",
            "парк"),
    AQUARIUM(4,
            "aquarium",
            "океанариум"),
    GALLERY(5,
            "gallery",
            "галерея"),
    MUSEUM(6,
            "museum",
            "музей"),
    VIEWPOINT(7,
            "viewpoint",
            "красивое место"),
    ZOO(8,
            "zoo",
            "зоопарк");

    public final int id;
    public final String en;
    public final String ru;

    TourismType(int id,
                String en,
                String ru) {
        this.id = id;
        this.en = en;
        this.ru = ru;
    }

    private static final Map<Integer, TourismType> idToType = new HashMap<>();

    static {
        for (TourismType value : TourismType.values()) {
            idToType.put(value.id, value);
        }
    }

    public String getByLan(Language lan) {
        switch (lan) {
            case EN:
                return en;
            case RU:
                return ru;
            default:
                return "";
        }

    }

    public static TourismType getByTypeId(int id) {
        return idToType.get(id);
    }

}
