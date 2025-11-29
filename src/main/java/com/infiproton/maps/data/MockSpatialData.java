package com.infiproton.maps.data;

import com.infiproton.maps.model.Driver;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.Warehouse;

import java.util.List;

public class MockSpatialData {

    public static final GeoPoint CUSTOMER = new GeoPoint(12.9716, 77.5946);

    public static final List<Warehouse> WAREHOUSES = List.of(
            new Warehouse("W1", "North Hub", new GeoPoint(13.0358, 77.5970)),
            new Warehouse("W2", "Central Hub", new GeoPoint(12.9758, 77.6055)),
            new Warehouse("W3", "South Hub", new GeoPoint(12.9050, 77.5850))
    );

    public static final List<Driver> DRIVERS = List.of(
            new Driver("D1", "Ravi", new GeoPoint(12.9721, 77.5933)),
            new Driver("D2", "Amit", new GeoPoint(12.9615, 77.6100)),
            new Driver("D3", "Suresh", new GeoPoint(13.0200, 77.5500))
    );
}
