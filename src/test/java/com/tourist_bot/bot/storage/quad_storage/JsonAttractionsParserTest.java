package com.tourist_bot.bot.storage.quad_storage;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


class JsonAttractionsParserTest {

    @Test
    public void test1() throws IOException {
        String jsonPath = getClass().getClassLoader().getResource("com/tourist_bot/bot/storage/quad_storage/test1.json").getPath();
        QuadTreeAttractions quadTreeAttractions = JsonAttractionsParser.parse(jsonPath);

        assertEquals(quadTreeAttractions.minLon, 15);
        assertEquals(quadTreeAttractions.minLat, 15);

        assertEquals(quadTreeAttractions.maxLat, 30);
        assertEquals(quadTreeAttractions.maxLat, 30);
    }

}