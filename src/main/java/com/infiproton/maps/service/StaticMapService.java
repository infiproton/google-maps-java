package com.infiproton.maps.service;

import com.infiproton.maps.model.GeoPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class StaticMapService {

    private final RestTemplate restTemplate;
    @Value("${maps.api.key}")
    private String mapsApiKey;

    private static final String STATIC_BASE = "https://maps.googleapis.com/maps/api/staticmap";
    private static final String DEFAULT_SIZE = "1024x1024";

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
