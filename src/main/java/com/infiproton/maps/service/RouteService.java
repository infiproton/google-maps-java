package com.infiproton.maps.service;

import com.infiproton.maps.dto.RouteRequest;
import com.infiproton.maps.dto.RouteResponse;
import com.infiproton.maps.model.routes.ComputeRoutesRequest;
import com.infiproton.maps.model.routes.ComputeRoutesResponse;
import com.infiproton.maps.model.routes.Waypoint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RouteService {

    private final String mapsApiKey;
    private final RestTemplate restTemplate;
    private final String COMPUTE_ROUTES_URL = "https://routes.googleapis.com/directions/v2:computeRoutes";

    RouteService(@Value("${maps.api.key}") String mapsApiKey, RestTemplate restTemplate) {
        this.mapsApiKey = mapsApiKey;
        this.restTemplate = restTemplate;
    }

    public RouteResponse getRoute(RouteRequest req) {

        // 1. prepare request
        ComputeRoutesRequest body = buildRequest(req);

        // 2. invoke google api
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Goog-Api-Key", mapsApiKey);
        headers.set("X-Goog-FieldMask", "routes.distanceMeters,routes.duration,routes.polyline.encodedPolyline," +
                        "routes.legs.steps.navigationInstruction," +
                        "routes.legs.steps.polyline.encodedPolyline," +
                        "routes.legs.steps.distanceMeters," +
                        "routes.legs.steps.staticDuration," +
                        "routes.legs.distanceMeters," +
                        "routes.legs.duration"
                );

        HttpEntity<ComputeRoutesRequest> entity = new HttpEntity<>(body, headers);
        ResponseEntity<ComputeRoutesResponse> respEntity;
        try {
            respEntity = restTemplate.exchange(COMPUTE_ROUTES_URL, HttpMethod.POST, entity, ComputeRoutesResponse.class);
        } catch (RestClientException ex) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Routes API call failed: " + ex.getMessage(), ex);
        }
        ComputeRoutesResponse resp = respEntity.getBody();

        // 3. parse the response
        return parsePrimaryRoute(resp);
    }

    private ComputeRoutesRequest buildRequest(RouteRequest req) {
        Waypoint.LatLng originLatLng = new Waypoint.LatLng(req.origin().lat(), req.origin().lng());
        Waypoint origin = new Waypoint(new Waypoint.Location(originLatLng));

        Waypoint.LatLng destLatLng = new Waypoint.LatLng(req.destination().lat(), req.destination().lng());
        Waypoint destination = new Waypoint(new Waypoint.Location(destLatLng));

        return new ComputeRoutesRequest(origin, destination, "DRIVE", "TRAFFIC_AWARE");
    }

    private RouteResponse parsePrimaryRoute(ComputeRoutesResponse resp) {
        ComputeRoutesResponse.Route r = resp.getRoutes().get(0);
        long distance = Optional.ofNullable(r.getDistanceMeters()).orElse(0L);
        long durationSeconds = parseDurationToSeconds(r.getDuration());
        String polyline = (r.getPolyline() != null) ? r.getPolyline().getEncodedPolyline() : "";

        RouteResponse out = RouteResponse.builder()
                .distanceMeters(distance)
                .durationSeconds(durationSeconds)
                .polyline(polyline)
                .build();

        // TODO: parse legs & steps
        if (r.getLegs() != null && !r.getLegs().isEmpty()) {
            List<RouteResponse.Leg> legs = new ArrayList<>();

            for (ComputeRoutesResponse.Leg leg : r.getLegs()) {
                long legDistance = Optional.ofNullable(leg.getDistanceMeters()).orElse(0L);
                long legDuration = parseDurationToSeconds(leg.getDuration());

                List<RouteResponse.Step> steps = new ArrayList<>();
                for (ComputeRoutesResponse.Step s : leg.getSteps()) {
                    String instr = s.getNavigationInstruction() != null ?
                            s.getNavigationInstruction().getInstructions() : "";
                    String maneuver = s.getNavigationInstruction() != null ?
                            s.getNavigationInstruction().getManeuver() : "";

                    long stepDist = Optional.ofNullable(s.getDistanceMeters()).orElse(0L);
                    long stepDur = parseDurationToSeconds(s.getStaticDuration());
                    String stepPoly = s.getPolyline() != null ? s.getPolyline().getEncodedPolyline() : "";

                    steps.add(new RouteResponse.Step(instr, maneuver, stepDist, stepDur, stepPoly));
                }
                legs.add(new RouteResponse.Leg(legDistance, legDuration, steps));
            }
            out.setLegs(legs);
        }

        return out;
    }

    /**
     * short duration parser:
     * 1) If null -> 0
     * 2) Try ISO-8601 (PTnHnMnS) with Duration.parse
     * 3) If ends with 's', parse the numeric prefix
     * 4) Fallback: extract first number
     */
    private long parseDurationToSeconds(String text) {
        if (text == null || text.isBlank()) return 0L;

        // Try ISO-8601 first (PT...), else simple "Ns" format, else fallback
        try {
            if (text.startsWith("PT")) {
                return java.time.Duration.parse(text).getSeconds();
            }
        } catch (Exception ignored) {}

        if (text.endsWith("s")) {
            try {
                return Long.parseLong(text.substring(0, text.length() - 1));
            } catch (NumberFormatException ignored) {}
        }

        // fallback - extract first number
        String digits = text.replaceAll("[^0-9]", " ").trim();
        if (digits.isEmpty()) return 0L;
        try {
            return Long.parseLong(digits.split("\\s+")[0]);
        } catch (Exception e) {
            return 0L;
        }
    }
}
