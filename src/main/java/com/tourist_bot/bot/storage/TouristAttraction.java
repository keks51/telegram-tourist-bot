package com.tourist_bot.bot.storage;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tourist_bot.bot.logic.TourismType;
import com.tourist_bot.bot.logic.language.Language;

import java.util.Map;


public class TouristAttraction {

    public final long id;
    public final TourismType tourismType;
    public final double lon;
    public final double lat;
    public final Map<Language, String> lanNameMap;
    public final Map<Language, String> lanDescMap;


    public TouristAttraction(long id,
                             TourismType tourismType,
                             double lon,
                             double lat,
                             Map<Language, String> lanNameMap,
                             Map<Language, String> lanDescMap) {
        this.id = id;
        this.tourismType = tourismType;
        this.lon = lon;
        this.lat = lat;
        this.lanNameMap = lanNameMap;
        this.lanDescMap = lanDescMap;
    }

    // json deserialization
    @JsonCreator
    public static TouristAttraction apply(
            @JsonProperty("id") long id,
            @JsonProperty("tourismType") TourismType tourismType,
            @JsonProperty("lon") double lon,
            @JsonProperty("lat") double lat,
            @JsonProperty("lanNameMap") Map<Language, String> lanNameMap,
            @JsonProperty("lanDescMap") Map<Language, String> lanDescMap) {
        return new TouristAttraction(id, tourismType, lon, lat, lanNameMap, lanDescMap);
    }

}
