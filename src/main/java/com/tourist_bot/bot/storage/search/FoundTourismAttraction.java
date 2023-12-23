package com.tourist_bot.bot.storage.search;


import com.tourist_bot.bot.logic.TourismType;


public class FoundTourismAttraction {
    public final double dist;
    public final long id;
    public final double lon;
    public final double lat;
    public final TourismType tourismType;

    public FoundTourismAttraction(long id, int attractionTypeId, double lon, double lat, float distMeters) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        this.tourismType = TourismType.getByTypeId(attractionTypeId);
        this.dist = distMeters;
    }

    @Override
    public String toString() {
        return "FoundTourismAttraction{" +
                "dist=" + dist +
                ", id=" + id +
                ", lon=" + lon +
                ", lat=" + lat +
                ", tourismType=" + tourismType +
                '}';
    }
}
