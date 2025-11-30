package com.infiproton.maps.service;

import com.infiproton.maps.data.MockSpatialData;
import com.infiproton.maps.model.Driver;
import com.infiproton.maps.model.DriverDistance;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.util.DistanceCalculator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DriverRadiusService {

    public List<DriverDistance> findDriversWithinRadius(GeoPoint pickup, double radiusKm) {
        List<DriverDistance> result = new ArrayList<>();

        List<Driver> drivers = MockSpatialData.DRIVERS;

        for (Driver driver : drivers) {
            double distance = DistanceCalculator.distanceInKm(
                    pickup,
                    driver.location()
            );

            if (distance <= radiusKm) {
                result.add(new DriverDistance(driver, distance));
            }
        }
        result.sort(Comparator.comparingDouble(DriverDistance::distanceKm));

        return result;
    }
}
