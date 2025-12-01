package com.infiproton.maps.controller;

import com.infiproton.maps.dto.PlanDeliveryRequest;
import com.infiproton.maps.dto.PlanDeliveryResponse;
import com.infiproton.maps.service.DeliveryPlanningService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
@AllArgsConstructor
class DeliveryPlanningController {
    private final DeliveryPlanningService deliveryPlanningService;

    @PostMapping("/delivery/plan")
    public PlanDeliveryResponse planDelivery(@RequestBody PlanDeliveryRequest request) {
        return deliveryPlanningService.planDelivery(request);
    }
}
