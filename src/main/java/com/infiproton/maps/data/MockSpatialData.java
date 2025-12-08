package com.infiproton.maps.data;

import com.infiproton.maps.model.Driver;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.Warehouse;

import java.util.List;

public class MockSpatialData {

    public static final GeoPoint CUSTOMER = new GeoPoint(12.9716, 77.5946);

    // Simple polygon approximating a city service zone
    public static final List<GeoPoint> CITY_SERVICE_AREA = List.of(
            new GeoPoint(12.99, 77.55),
            new GeoPoint(13.05, 77.60),
            new GeoPoint(12.98, 77.65),
            new GeoPoint(12.90, 77.64),
            new GeoPoint(12.88, 77.57)
    );

    public static final List<Warehouse> WAREHOUSES = List.of(
            new Warehouse("W1", "North Hub", new GeoPoint(13.0358, 77.5970)),
            new Warehouse("W2", "Central Hub", new GeoPoint(12.9758, 77.6055)),
            new Warehouse("W3", "South Hub", new GeoPoint(12.9050, 77.5850))
    );

    public static final List<Driver> DRIVERS = List.of(
            new Driver("D1", "Ravi", new GeoPoint(12.9721, 77.5933)),
            new Driver("D2", "Amit", new GeoPoint(12.9615, 77.6100)),
            new Driver("D3", "Suresh", new GeoPoint(13.0200, 77.5500)),
            new Driver("D4", "Meena",  new GeoPoint(12.9800, 77.6200)),
            new Driver("D5", "Kiran",  new GeoPoint(12.9500, 77.5800))
    );
}
