package com.infiproton.maps.dto;

import com.infiproton.maps.model.ServiceAreaStatus;
import com.infiproton.maps.model.Warehouse;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanDeliveryResponse {
    private String inputAddress;

    private double lat;
    private double lng;

    private ServiceAreaStatus serviceAreaStatus;

    private Warehouse nearestWarehouse;
    private double warehouseDistanceKm;

    private String displayAddress;
}
