package com.tourist_bot.geo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tourist_bot.bot.logic.TourismType;
import com.tourist_bot.bot.logic.language.Language;
import com.tourist_bot.bot.storage.TouristAttraction;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;


public class TouristAttractionsDataPreparation {

    private static final Logger log = LoggerFactory.getLogger(TouristAttractionsDataPreparation.class.getName());

    private static final Map<String, String> descTranslator = new HashMap<>() {{
        put("sculpture", "скульптура");
        put("statue", "статуя");
        put("graffiti", "граффити");
        put("bust", "бюст");
        put("memorial", "мемориал");
        put("installation", "инсталляция");
        put("vase", "ваза");
        put("mural", "стенная роспись");
        put("building", "здание");
        put("ruins", "руины");
        put("painting", "живопись");
        put("palace", "дворец");
        put("castle", "замок");
        put("tomb", "гробница");
        put("vehicle", "транспорт");
        put("mosaic", "мозайка");
        put("ship", "корабль");
        put("manor", "поместье/усадьба");
        put("monument", "монумент");
        put("relief", "Рельеф (скульптура)");
        put("stone", "скульптура из камня");
        put("gate", "ворота");
        put("aircraft", "воздушное судно");
        put("arch", "арка");
        put("house", "здание");
        put("architecture", "архитектура");
        put("cannon", "пушка");
        put("locomotive", "локомотив");
        put("church", "церковь");
        put("boat", "лодка");
        put("fort", "форт");
        put("fountain", "фонтан");
        put("technical_monument", "монумент");
        put("city_gate", "городские ворота");
        put("stele", "стела");
        put("crane", "кран");
        put("printing_press", "Печатный станок");
        put("heritage", "");
        put("bridge", "мост");
        put("fresco", "фреска");
        put("Christopher", "");
        put("propeller", "пропеллер");
        put("streetart", "уличное искусство");
        put("monastery", "монастырь");
        put("anchor", "якорь");
    }};


    public static void main(String[] args) throws IOException, ParseException {
        String inputFilePath = "";
        String outputFilePath = "";

        String jsonStr = Files.readString(Path.of(inputFilePath)).replace("\u00a0", "");

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonStr);
        JsonNode jsonArrRaw = jsonNode.get("features");

        ArrayList<JsonNode> jsonArrayFiltered = filterTouristAttractions(jsonArrRaw.iterator());
        System.out.println(jsonArrayFiltered.size());

        ArrayList<TouristAttraction> touristAttractions = mapToTouristAttraction(jsonArrayFiltered);

        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new FileWriter(outputFilePath), touristAttractions);
    }

    public static ArrayList<TouristAttraction> mapToTouristAttraction(ArrayList<JsonNode> jsonArr) throws ParseException {
        ArrayList<TouristAttraction> res = new ArrayList<>(jsonArr.size());

        for (int id = 0; id < jsonArr.size(); id++) {
            JsonNode jsonNode = jsonArr.get(id);
            try {
                JsonNode geometryJsonObj = jsonNode.get("geometry");
                GeoJsonReader reader = new GeoJsonReader();
                Point centroid = reader.read(geometryJsonObj.toString()).getCentroid();
                double lat = centroid.getY();
                double lon = centroid.getX();

                JsonNode properties = jsonNode.get("properties");

                Map<Language, String> lanDescMap = getDesc(properties);
                Map<Language, String> lanNameMap = getLanNameMap(properties);

                String tourism = properties.get("tourism").textValue();
                TourismType tourismType = TourismType.valueOf(tourism.toUpperCase());


                if (lanDescMap.get(Language.EN).equals(tourismType.en)
                        || lanDescMap.get(Language.RU).equals(tourismType.ru)
                        || lanDescMap.get(Language.EN).equals(lanNameMap.get(Language.EN))
                        || lanDescMap.get(Language.RU).equals(lanNameMap.get(Language.RU))) {
                    lanDescMap.replaceAll((l, v) -> "");
                }
                res.add(new TouristAttraction(id, tourismType, lon, lat, lanNameMap, lanDescMap));
            } catch (Exception e) {
                log.warn("Cannot parse json: '" + jsonNode.toPrettyString() + "'", e);
            }

        }
        return res;
    }

    public static ArrayList<JsonNode> filterTouristAttractions(Iterator<JsonNode> jsonArray) throws ParseException {
        HashSet<String> filterOutSet = new HashSet<>(Arrays.asList("hotel",
                "hostel",
                "information",
                "motel",
                "guest_house",
                "trail_riding_station",
                "chalet",
                "lean_to",
                "caravan_site",
                "camp_site",
                "picnic_site",
                "theme_park",
                "apartment",
                "yes"));

        ArrayList<JsonNode> jsonNodes = new ArrayList<>();

        while (jsonArray.hasNext()) {
            JsonNode attractionJsonObj = jsonArray.next();
            JsonNode attPropertiesJsonObg = attractionJsonObj.get("properties");
            // take only tourist attractions
            if (!attPropertiesJsonObg.has("tourism")) continue;
            if (!attPropertiesJsonObg.has("name")) continue;
            if (filterOutSet.contains(attPropertiesJsonObg.get("tourism").textValue())) continue;


            JsonNode attractionGeometryJsonObj = attractionJsonObj.get("geometry");

            GeoJsonReader reader = new GeoJsonReader();
            Geometry geometry = reader.read(attractionGeometryJsonObj.toString());

            switch (geometry.getGeometryType()) {
                case Geometry.TYPENAME_MULTILINESTRING:
                    // multiline string means tourism route from one place to another
                    break;
                case Geometry.TYPENAME_GEOMETRYCOLLECTION:
                case Geometry.TYPENAME_MULTIPOINT:
                case Geometry.TYPENAME_LINEARRING:
                    throw new IllegalArgumentException("New geo type: " + geometry.getGeometryType());
                default:
                    jsonNodes.add(attractionJsonObj);
            }

        }
        return jsonNodes;
    }

    private static Map<Language, String> getDesc(JsonNode properties) {
        Map<Language, String> resMap = new HashMap<>();
        String descVal = "";
        String descValRu = "";
        if (properties.has("alt_name")) descVal = properties.get("alt_name").textValue();
        if (properties.has("description")) descVal = properties.get("description").textValue();
        if (properties.has("old_name")) descVal = properties.get("old_name").textValue();
        if (properties.has("inscription")) descVal = properties.get("inscription").textValue();
        if (properties.has("artwork_type")) descVal = properties.get("artwork_type").textValue();
        if (properties.has("note")) descVal = properties.get("note").textValue();
        if (properties.has("historic")) descVal = properties.get("historic").textValue();
        resMap.put(Language.EN, descVal);
        if (descTranslator.containsKey(descVal.trim().toLowerCase())) {
            descValRu = descTranslator.get(descVal.trim().toLowerCase());

        }
        resMap.put(Language.RU, descValRu);
        return resMap;
    }

    private static Map<Language, String> getLanNameMap(JsonNode properties) {
        Map<Language, String> resMap = new HashMap<>();
        if (properties.has("name")) {
            resMap.put(Language.RU, properties.get("name").textValue());
        } else if (properties.has("name:ru")) {
            resMap.put(Language.RU, properties.get("name:ru").textValue());
        } else {
            throw new IllegalArgumentException("Attraction doesn't contain 'name' property");
        }

        if (properties.has("name:en")) {
            resMap.put(Language.EN, properties.get("name:en").textValue());
        } else {
            resMap.put(Language.EN, resMap.get(Language.RU));
        }

        return resMap;
    }

}
