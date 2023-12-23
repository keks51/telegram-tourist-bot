package com.tourist_bot.bot.storage.quad_storage;


// stored inside quads
public class IdAndCoordinates {

    public final long id;
    public final int tourismTypeId;
    public final double lon;
    public final double lat;


    public IdAndCoordinates(long id, int tourismTypeId, double lon, double lat) {
        this.id = id;
        this.tourismTypeId = tourismTypeId;
        this.lon = lon;
        this.lat = lat;
    }

}
