package com.infiproton.maps.util;

import com.infiproton.maps.model.GeoPoint;

public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public static double distanceInKm(GeoPoint p1, GeoPoint p2) {

        double lat1 = Math.toRadians(p1.lat());
        double lon1 = Math.toRadians(p1.lng());
        double lat2 = Math.toRadians(p2.lat());
        double lon2 = Math.toRadians(p2.lng());

        double deltaLat = lat2 - lat1;
        double deltaLon = lon2 - lon1;

        double a = Math.pow(Math.sin(deltaLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(deltaLon / 2), 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double rawDistance = EARTH_RADIUS_KM * c;

        return Math.round(rawDistance * 100.0) / 100.0;
    }
}
