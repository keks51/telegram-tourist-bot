package com.tourist_bot.bot.storage.quad_storage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourist_bot.bot.storage.TouristAttraction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class JsonAttractionsParser {

    public static QuadTreeAttractions parse(String jsonPath) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<TouristAttraction> touristAttractions = objectMapper.readValue(
                new File(jsonPath),
                new TypeReference<>() {
                });


        ArrayList<QuadTreeAttraction> quadTreeAttractions = new ArrayList<>();

        double minLat = Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE;
        double maxLat = Double.MIN_VALUE;
        double maxLon = Double.MIN_VALUE;
        for (TouristAttraction touristAttraction : touristAttractions) {
            minLat = Math.min(minLat, touristAttraction.lat);
            minLon = Math.min(minLon, touristAttraction.lon);
            maxLat = Math.max(maxLat, touristAttraction.lat);
            maxLon = Math.max(maxLon, touristAttraction.lon);



            IdAndCoordinates idAndCoordinates = new IdAndCoordinates(
                    touristAttraction.id,
                    touristAttraction.tourismType.id,
                    touristAttraction.lon,
                    touristAttraction.lat);

            quadTreeAttractions.add(new QuadTreeAttraction(idAndCoordinates, touristAttraction));
        }


        return new QuadTreeAttractions(quadTreeAttractions, minLon, minLat, maxLon, maxLat);
    }

}
