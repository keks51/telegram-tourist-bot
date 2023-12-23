package com.tourist_bot.bot.storage.quad_storage;


import com.tourist_bot.bot.storage.GeoStorage;
import com.tourist_bot.bot.storage.TouristAttraction;
import com.tourist_bot.bot.storage.search.FoundTourismAttraction;
import com.tourist_bot.bot.storage.search.SearchRes;
import com.tourist_bot.quad.QuadPoint;
import com.tourist_bot.quad.QuadTree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class EmbeddedGeoQuadTreeGeoStorage implements GeoStorage {

    private static final Logger log = LoggerFactory.getLogger(EmbeddedGeoQuadTreeGeoStorage.class.getName());

    private final Map<Long, TouristAttraction> idToAttractionData;
    private final QuadTree<IdAndCoordinates> tree;
    private final int searchDistanceStep;
    private final int maxSearchDistance;

    public EmbeddedGeoQuadTreeGeoStorage(QuadTreeAttractions quadTreeAttractions,
                                         int searchDistanceStepMeters,
                                         int maxSearchDistanceMeters) {
        this.searchDistanceStep = searchDistanceStepMeters;
        this.maxSearchDistance = maxSearchDistanceMeters;


        this.idToAttractionData = new HashMap<>();
        double avgLat = (quadTreeAttractions.minLat + quadTreeAttractions.maxLat) / 2;

        // same for x and y
        double quadrantLenInAngel = LatLonConverter.getLatAngelStepForMeters(avgLat, searchDistanceStepMeters);


        // since longitude is two time greater than lat we cannot correct find the closest attractions by radius.
        // To handle this situation we have to store all lon coordinates divided by 2.
        double startX = quadTreeAttractions.minLon / 2;
        double endX = quadTreeAttractions.maxLon / 2;
        double startY = quadTreeAttractions.minLat;
        log.info("Building quadTree with params: " +
                "   \nStartX(Lon) '" + startX + "' EndX(Lon) '" + endX + "' " +
                "   \nStartY(Lat) '" + startY + "' " +
                "   \nQuadrantLen Angel='" + quadrantLenInAngel + "' " + "Meters: " + searchDistanceStepMeters +
                "   \nOneAngel in km='" + (searchDistanceStepMeters /quadrantLenInAngel/1000));
        this.tree = QuadTree.buildByMinMax(
                quadrantLenInAngel,
                quadrantLenInAngel,
                startX,
                endX,
                startY
        );
        log.info("Build QuadTree: " + tree.printTree());

        log.info("QuadTreeSideLen:  Angels='" + tree.quadTreeLenX + "' Km='" + (tree.quadrantsPerLineX * searchDistanceStepMeters) / 1000 + "'");

        for (QuadTreeAttraction quadTreeAttraction : quadTreeAttractions.quadTreeAttractions) {
            IdAndCoordinates idAndCoordinates = quadTreeAttraction.idAndCoordinates;
            tree.add(idAndCoordinates.lon / 2, idAndCoordinates.lat, idAndCoordinates);

            TouristAttraction touristAttraction = quadTreeAttraction.touristAttraction;
            idToAttractionData.put(touristAttraction.id, touristAttraction);
        }
        log.info("Inserted '" + quadTreeAttractions.numberOfAttractions + "' to tree");

    }

    @Override
    public SearchRes getGroupedAttractionsIdSorted(float lon,
                                                   float lat,
                                                   double startSearchDistanceMeters,
                                                   int minNumberOfAttractions,
                                                   HashSet<Long> skip) {
        if (lon / 2 >= tree.xMax || lat >= tree.yMax) {
            return new SearchRes(new HashMap<>(), -1, 0);
        }

        if (lon / 2 < tree.xMin || lat < tree.yMin) {
            return new SearchRes(new HashMap<>(), -1, 0);
        }


        try {
            double radius = startSearchDistanceMeters / this.searchDistanceStep * tree.quadrantLenX;
//            double radius = 20;
            Iterator<QuadPoint<IdAndCoordinates>> iter = tree.searchByRadius(lon / 2, lat, radius);
            Map<Integer, ArrayList<FoundTourismAttraction>> attractions = new HashMap<>();
            int cnt = 0;
            if (iter.hasNext()) {
                while (iter.hasNext()) {
                    IdAndCoordinates attraction = iter.next().value;
                    if (!skip.contains(attraction.id)) {
                        cnt++;
                        attractions.putIfAbsent(attraction.tourismTypeId, new ArrayList<>());
                        float distMeters = (float) LatLonConverter.distFromInMeters(
                                lon, lat,
                                attraction.lon, attraction.lat);
                        FoundTourismAttraction foundTourismAttraction = new FoundTourismAttraction(
                                attraction.id,
                                attraction.tourismTypeId,
                                attraction.lon,
                                attraction.lat,
                                distMeters
                        );
                        attractions.get(attraction.tourismTypeId).add(foundTourismAttraction);
                    }
                }

            }
            if (startSearchDistanceMeters >= maxSearchDistance) {
                return new SearchRes(attractions, (int) startSearchDistanceMeters, cnt);
            } else {
                if (attractions.isEmpty() || cnt < minNumberOfAttractions) {
                    return getGroupedAttractionsIdSorted(lon, lat, startSearchDistanceMeters + searchDistanceStep, minNumberOfAttractions, skip);
                } else {
                    return new SearchRes(attractions, (int) startSearchDistanceMeters, cnt);
                }
            }

        } catch (IndexOutOfBoundsException e) {
            return new SearchRes(new HashMap<>(), -1, 0);
        }
    }

    @Override
    public TouristAttraction getAttractionData(long attractiond) {
        return idToAttractionData.get(attractiond);
    }

}
