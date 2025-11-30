package com.infiproton.maps.service;

import com.infiproton.maps.data.MockSpatialData;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.ServiceAreaStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceAreaService {

    public ServiceAreaStatus validateLocation(GeoPoint location) {
        boolean inside = isPointInsidePolygon(location, MockSpatialData.CITY_SERVICE_AREA);

        return inside
                ? ServiceAreaStatus.INSIDE_SERVICE_AREA
                : ServiceAreaStatus.OUTSIDE_SERVICE_AREA;
    }

    // Ray-casting point-in-polygon algorithm
    private boolean isPointInsidePolygon(GeoPoint point, List<GeoPoint> polygon) {
        double x = point.lng();
        double y = point.lat();

        boolean inside = false;
        int n = polygon.size();

        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon.get(i).lng();
            double yi = polygon.get(i).lat();

            double xj = polygon.get(j).lng();
            double yj = polygon.get(j).lat();

            boolean intersect = ((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (yj - yi + 0.0) + xi);

            if (intersect) {
                inside = !inside;
            }
        }
        return inside;
    }
}
