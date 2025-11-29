package com.infiproton.maps.util;

import com.infiproton.maps.data.MockSpatialData;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.Warehouse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class DistanceCalculatorTest {

    @Test
    void testCustomerToWarehouseDistances() {
        GeoPoint customer = MockSpatialData.CUSTOMER;

        for (Warehouse warehouse : MockSpatialData.WAREHOUSES) {

            double distance = DistanceCalculator.distanceInKm(customer, warehouse.location());
            System.out.println("Distance to " + warehouse.name() + " = " + distance + " km");

            assertTrue(distance > 0);
        }
    }

}
