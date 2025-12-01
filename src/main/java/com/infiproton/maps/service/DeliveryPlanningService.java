package com.infiproton.maps.service;

import com.infiproton.maps.dto.*;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.ServiceAreaStatus;
import com.infiproton.maps.model.Warehouse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class DeliveryPlanningService {

    private final GeocodingService geocodingService;
    private final ServiceAreaService serviceAreaService;
    private final NearestWarehouseService nearestWarehouseService;
    private final ReverseGeocodeService reverseGeocodingService;

    public PlanDeliveryResponse planDelivery(PlanDeliveryRequest request) {

        // 1. Forward Geocode: address → coordinates
        GeocodeResponse geocodeResult = geocodingService.geocode(request.customerAddress());
        GeoPoint customerLocation = new GeoPoint(
                geocodeResult.getLatitude(),
                geocodeResult.getLongitude()
        );

        // 2. Service Area Validation
        ServiceAreaStatus serviceAreaStatus = serviceAreaService.validateLocation(customerLocation);

        // 3. Nearest Warehouse Selection
        NearestWarehouseResponse nearestWarehouseResult = nearestWarehouseService.findNearestWarehouse(customerLocation);
        Warehouse nearestWarehouse = nearestWarehouseResult.warehouse();
        double distanceKm = nearestWarehouseResult.distanceKm();

        // 4. Reverse Geocode for display / logging
        ReverseGeocodingResponse reverseResult = reverseGeocodingService.reverseGeocode(
                customerLocation.lat(),
                customerLocation.lng()
        );
        String displayAddress = reverseResult.getFormattedAddress();

        return PlanDeliveryResponse.builder()
                .inputAddress(request.customerAddress())
                .lat(customerLocation.lat())
                .lng(customerLocation.lng())
                .serviceAreaStatus(serviceAreaStatus)
                .nearestWarehouse(nearestWarehouse)
                .warehouseDistanceKm(distanceKm)
                .displayAddress(displayAddress)
                .build();
    }

}
