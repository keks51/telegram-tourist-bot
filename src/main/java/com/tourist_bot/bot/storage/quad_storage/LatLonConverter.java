package com.tourist_bot.bot.storage.quad_storage;


public class LatLonConverter {


    public static void main(String[] args) {


        // 59.852069, 30.292972
        // 59.852086, 30.302726
//        System.out.println(distFrom(
//                59.852061f, 30.292972f,
//                59.852061f, 30.302726f));
//
        // latitude - широта
//        System.out.println(distFrom(
//                60.0f, 1.0f,
//                60.0f, 2.0f)); // 96_297.33 meters
//        System.out.println(distFrom(
//                60.0f, 30.0f,
//                60.0f, 31.0f)); // 55_596.934 meters
//        System.out.println(distFrom(
//                60.0f, 60.0f,
//                60.0f, 61.0f)); // 55_596.934 meters

        float minLat = -90;
        float maxLat = 90;
//        float step = 0.0045f;
//        for (int lat = (int) minLat; lat < maxLat; lat++) {
//
//            float dist = distFromInMeters(
//                    lat, 1.0f,
//                    lat + step, 2.0f);
//            System.out.println("Lat: " + x + " dist: " + dist);
//
//        }


//        for (int i = -30; i < 30; i++) {
//
//            float angelStepForMeters = getAngelStepForMeters(i, 500);
//            float oneAngelMeters = distFromInMeters(
//                    i, 1.0f,
//                    i + angelStepForMeters, 2.0f);
//            System.out.println("Lat: " + i + " dist: " + angelStepForMeters + "  " + oneAngelMeters);
//        }

//        float oneAngelMeters = distFromInMeters(
//                0, 1.0f,
//                0 + 1, 2.0f);
//        float v = 500 / oneAngelMeters;
//
//        System.out.println(oneAngelMeters + "  " + v);


//        System.out.println(distFromInMeters(
//                0, 1.0f,
//                0 + 1f, 2.0f));
//        System.out.println(distFromInMeters(
//                0, 1.0f,
//                0 + 0.5f, 2.0f));
//        System.out.println(distFromInMeters(
//                0, 1.0f,
//                0 + 0.1f, 2.0f));
//        System.out.println(distFromInMeters(
//                0, 1.0f,
//                0 + 0.05f, 2.0f));
//        System.out.println(distFromInMeters(
//                0, 1.0f,
//                0 + 0.01f, 2.0f));
//        System.out.println(distFromInMeters(
//                0, 1.0f,
//                0 + 0.000001f, 2.0f));


        // 59.944211, 30.348824
        // 59.944176, 30.357807

        // for lat 60 lon 30  step 0.00449665     lat == 500.0046668962664 meters
        // for lat 60 lon 30  step 0.00449665 * 2 lon == 500.00466651130523 meters

//        for (int lat = -88; lat < 89; lat++) {
//            System.out.println("Lat: " + lat);
//            {
//
//            }
//            {
//                double lonMetersForOneAngel = distFromInMeters(
//                        lat, 30,
//                        lat, 30 + 1);
//                System.out.println(lonMetersForOneAngel + " -> 1 angel lon");
//                double lonAngelStepFor500 = (500 + 0.1) / lonMetersForOneAngel;
//                double d3 = distFromInMeters(
//                        lat, 30,
//                        lat, 30 + lonAngelStepFor500);
//                System.out.println(d3 + " -> " + lonAngelStepFor500 + " angel lon");
//            }
//            System.out.println();
//
//        }

        // 59.944211, 30.348824
        // 60.000000, 30.000000
        // 60.004497, 30.000000
        // 60.004487, 30.000003
        System.out.println(getLatAngelStepForMeters(60,500));
        System.out.println(getLonAngelStepForMeters(60,500));

    }

    public static double getLatAngelStepForMeters(double lat, int meters) {
        double latMetersForOneAngel = distFromInMeters(
                30, lat,
                 30, lat + 1);
        return (meters + 0.1) / latMetersForOneAngel;
    }

    public static double getLonAngelStepForMeters(double lat, int meters) {
        double lonMetersForOneAngel = distFromInMeters(
               30,  lat,
               30 + 1, lat);
        return (meters + 0.1) / lonMetersForOneAngel;
    }

    public static double distFromInMeters(double lng1, double lat1, double lng2, double lat2) {
        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLng = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c; // meters
    }

}
