package com.tourist_bot.bot.storage;

import com.tourist_bot.bot.storage.search.SearchRes;

import java.util.HashSet;


public interface GeoStorage {

    SearchRes getGroupedAttractionsIdSorted(float lon,
                                            float lat,
                                            double searchDistance,
                                            int minNumberOfAttractions,
                                            HashSet<Long> visited);

    TouristAttraction getAttractionData(long attractiond);

}
