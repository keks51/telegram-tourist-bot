package com.tourist_bot.bot.storage.quad_storage.quad_storage;

import com.tourist_bot.bot.logic.TourismType;
import com.tourist_bot.bot.storage.TouristAttraction;
import com.tourist_bot.bot.storage.quad_storage.*;
import com.tourist_bot.bot.storage.search.SearchRes;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class EmbeddedGeoQuadTreeGeoStorageTest {

    @Test
    public void test1() {
        ArrayList<QuadTreeAttraction> attractions = new ArrayList<>() {{
            add(generateAttraction(1, 1, 60, 60));
            add(generateAttraction(2, 1, 60, 61));
            add(generateAttraction(3, 1, 61, 60));
        }};
        QuadTreeAttractions quadTreeAttractions = new QuadTreeAttractions(attractions, 0, 30, 360, 90);


        {
            EmbeddedGeoQuadTreeGeoStorage geoStorage = new EmbeddedGeoQuadTreeGeoStorage(
                    quadTreeAttractions,
                    10_000,
                    112_000);

            SearchRes res = geoStorage.getGroupedAttractionsIdSorted(
                    60f, 60f, 1000, 5, new HashSet<>());
            Set<Long> foundIds = res.groupedAttractions.values().stream().flatMap(Collection::stream).map(e -> e.id).collect(Collectors.toSet());
            Set<Long> expected = new HashSet<>(Arrays.asList(1L, 2L, 3L));
            assertEquals(expected, foundIds);
        }

        {
            EmbeddedGeoQuadTreeGeoStorage geoStorage = new EmbeddedGeoQuadTreeGeoStorage(
                    quadTreeAttractions,
                    10_000,
                    100_000);

            SearchRes res = geoStorage.getGroupedAttractionsIdSorted(
                    60f, 60f, 1000, 5, new HashSet<>());
            Set<Long> foundIds = res.groupedAttractions.values().stream().flatMap(Collection::stream).map(e -> e.id).collect(Collectors.toSet());
            Set<Long> expected = new HashSet<>(Arrays.asList(1L, 3L));
            assertEquals(expected, foundIds);
        }


    }

    @Test
    public void testSpb() {
        ArrayList<QuadTreeAttraction> attractions = new ArrayList<>() {{
            add(generateAttraction(1, 1, 30.313752f, 59.940435f)); // winter palace
            add(generateAttraction(2, 1, 30.335840f, 59.931772f)); // alexander theater ,
        }};
        QuadTreeAttractions quadTreeAttractions = new QuadTreeAttractions(attractions, 0, 30, 360, 90);


        // user position ,
        float userLat = 59.932036f;
        float userLon = 30.317492f;
        {
            EmbeddedGeoQuadTreeGeoStorage geoStorage = new EmbeddedGeoQuadTreeGeoStorage(
                    quadTreeAttractions,
                    100,
                    1100);

            SearchRes res = geoStorage.getGroupedAttractionsIdSorted(
                    userLon, userLat, 100, 5, new HashSet<>());
            Set<Long> foundIds = res.groupedAttractions.values().stream().flatMap(Collection::stream).map(e -> e.id).collect(Collectors.toSet());
            Set<Long> expected = new HashSet<>(Arrays.asList(1L, 2L));
            assertEquals(expected, foundIds);
        }

        {
            EmbeddedGeoQuadTreeGeoStorage geoStorage = new EmbeddedGeoQuadTreeGeoStorage(
                    quadTreeAttractions,
                    100,
                    1000);

            SearchRes res = geoStorage.getGroupedAttractionsIdSorted(
                    userLon, userLat, 100, 5, new HashSet<>());
            Set<Long> foundIds = res.groupedAttractions.values().stream().flatMap(Collection::stream).map(e -> e.id).collect(Collectors.toSet());
            Set<Long> expected = new HashSet<>(Arrays.asList(1L));
            assertEquals(expected, foundIds);
        }

        {
            EmbeddedGeoQuadTreeGeoStorage geoStorage = new EmbeddedGeoQuadTreeGeoStorage(
                    quadTreeAttractions,
                    100,
                    1100);

            SearchRes res = geoStorage.getGroupedAttractionsIdSorted(
                    userLon, userLat, 100, 5, new HashSet<>(Arrays.asList(1L, 2L)));
            Set<Long> foundIds = res.groupedAttractions.values().stream().flatMap(Collection::stream).map(e -> e.id).collect(Collectors.toSet());
            assertTrue(foundIds.isEmpty());
        }


    }

    @Test
    public void testSpbFromJson() throws IOException {
        String jsonPath = Objects.requireNonNull(getClass().getClassLoader().getResource("com/tourist_bot/bot/storage/quad_storage/spb.geojson")).getPath();
        EmbeddedGeoQuadTreeGeoStorage geoStorage = new EmbeddedGeoQuadTreeGeoStorage(
                JsonAttractionsParser.parse(jsonPath),
                100,
                1500);

        // user position ,
        float userLat = 59.932036f;
        float userLon = 30.317492f;
        SearchRes res = geoStorage.getGroupedAttractionsIdSorted(userLon, userLat, 10, 100, new HashSet<>());

        Set<Long> foundIds = res.groupedAttractions.values().stream().flatMap(Collection::stream).map(e -> e.id).collect(Collectors.toSet());
        assertTrue(foundIds.size() >= 100);

    }

    private static QuadTreeAttraction generateAttraction(int id, int attractionTypeId, float lon, float lat) {
        return new QuadTreeAttraction(
                new IdAndCoordinates(id, attractionTypeId, lon, lat),
                new TouristAttraction(id, TourismType.getByTypeId(attractionTypeId), 0,0, new HashMap<>(), new HashMap<>())
        );
    }

}