package com.infiproton.maps.service;

import com.infiproton.maps.model.Driver;
import com.infiproton.maps.model.GeoPoint;
import com.infiproton.maps.model.Warehouse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StaticMapService {

    private final RestTemplate restTemplate;
    @Value("${maps.api.key}")
    private String mapsApiKey;

    private static final String STATIC_BASE = "https://maps.googleapis.com/maps/api/staticmap";
    private static final String DEFAULT_SIZE = "1024x1024";

    public byte[] generateDeliveryRouteMapImage(GeoPoint customer, Warehouse nearestWarehouse,
                                                String customerColor, String warehouseColor,
                                                String polyline) {
        StringBuilder sb = new StringBuilder(STATIC_BASE).append("?");
        sb.append("size=").append(encode(DEFAULT_SIZE)).append("&");
        sb.append("markers=color:")
                .append(encode(customerColor))
                .append("|")
                .append(customer.lat())
                .append(",")
                .append(customer.lng())
                .append("&");
        sb.append("markers=color:")
                .append(encode(warehouseColor))
                .append("|").append("label:W|")
                .append(nearestWarehouse.location().lat())
                .append(",")
                .append(nearestWarehouse.location().lng())
                .append("&");

        sb.append("path=enc:").append(polyline).append("&");
        sb.append("key=").append(encode(mapsApiKey));

        String url = sb.toString();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.IMAGE_PNG, MediaType.IMAGE_JPEG));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        return resp.getBody();
    }

    public byte[] generateCustomerAndDriversMapImage(GeoPoint customer,
                                                     List<Driver> drivers,
                                                     String customerColor,
                                                     String driverColor) {
        StringBuilder sb = new StringBuilder(STATIC_BASE).append("?");
        sb.append("size=").append(encode(DEFAULT_SIZE)).append("&");
        sb.append("markers=color:")
                .append(encode(customerColor))
                .append("|")
                .append(customer.lat())
                .append(",")
                .append(customer.lng())
                .append("&");

        for (Driver d : drivers) {
            GeoPoint dl = d.location();
            sb.append("markers=color:")
                    .append(encode(driverColor))
                    .append("|")
                    .append("label:" + d.name().charAt(0))
                    .append("|")
                    .append(dl.lat())
                    .append(",")
                    .append(dl.lng())
                    .append("&");
        }
        sb.append("key=").append(encode(mapsApiKey));
        String url = sb.toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.IMAGE_PNG, MediaType.IMAGE_JPEG));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        return resp.getBody();
    }

    public byte[] generateCustomerMapImage(GeoPoint customerLocation, String color) {
        String url = STATIC_BASE +
                "?size=" + encode(DEFAULT_SIZE) +
                "&markers=color:" + encode(color) + "|" + customerLocation.lat() + "," + customerLocation.lng() +
                "&key=" + encode(mapsApiKey);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(java.util.List.of(MediaType.IMAGE_PNG, MediaType.IMAGE_JPEG));
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> resp = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
        return resp.getBody();
    }

    private String encode(String s) {
        return URLEncoder.encode(s, StandardCharsets.UTF_8);
    }

}
