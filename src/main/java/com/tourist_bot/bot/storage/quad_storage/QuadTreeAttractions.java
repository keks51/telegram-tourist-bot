package com.tourist_bot.bot.storage.quad_storage;

import java.util.ArrayList;



public class QuadTreeAttractions {

    public final ArrayList<QuadTreeAttraction> quadTreeAttractions;
    public final double minLon;
    public final double minLat;
    public final double maxLon;
    public final double maxLat;
    public final int numberOfAttractions;

    public QuadTreeAttractions(ArrayList<QuadTreeAttraction> quadTreeAttractions,
                               double minLon,
                               double minLat,
                               double maxLon,
                               double maxLat) {
        this.quadTreeAttractions = quadTreeAttractions;
        this.minLon = minLon;
        this.minLat = minLat;
        this.maxLon = maxLon;
        this.maxLat = maxLat;
        this.numberOfAttractions = quadTreeAttractions.size();
    }


    @Override
    public String toString() {
        return "QuadTreeAttractions{" +
                "quadTreeAttractions=" + quadTreeAttractions +
                ", minLon=" + minLon +
                ", minLat=" + minLat +
                ", maxLon=" + maxLon +
                ", maxLat=" + maxLat +
                ", numberOfAttractions=" + numberOfAttractions +
                '}';
    }
}
