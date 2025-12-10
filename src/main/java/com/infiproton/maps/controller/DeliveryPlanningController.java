package com.infiproton.maps.controller;

import com.infiproton.maps.dto.PlanDeliveryRequest;
import com.infiproton.maps.dto.PlanDeliveryResponse;
import com.infiproton.maps.dto.RestaurantOrderRequest;
import com.infiproton.maps.dto.RestaurantOrderResponse;
import com.infiproton.maps.service.DeliveryPlanningService;
import com.infiproton.maps.service.OrderPlanningService;
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
    private final OrderPlanningService orderPlanningService;

    @PostMapping("/delivery/plan")
    public PlanDeliveryResponse planDelivery(@RequestBody PlanDeliveryRequest request) {
        return deliveryPlanningService.planDelivery(request);
    }

    @PostMapping("/restaurant-order")
    public RestaurantOrderResponse planRestaurantOrder(@RequestBody RestaurantOrderRequest request) {
        return orderPlanningService.planRestaurantOrder(request);
    }
}
