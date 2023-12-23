package com.tourist_bot.bot.storage.quad_storage;


import com.tourist_bot.bot.storage.TouristAttraction;


public class QuadTreeAttraction {

    public final IdAndCoordinates idAndCoordinates;
    public final TouristAttraction touristAttraction;

    public QuadTreeAttraction(IdAndCoordinates idAndCoordinates, TouristAttraction touristAttraction) {

        this.idAndCoordinates = idAndCoordinates;
        this.touristAttraction = touristAttraction;
    }

}
