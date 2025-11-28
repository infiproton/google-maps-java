package com.infiproton.maps.service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.errors.InvalidRequestException;
import com.google.maps.errors.OverQueryLimitException;
import com.google.maps.errors.RequestDeniedException;
import com.google.maps.model.AddressType;
import com.google.maps.model.GeocodingResult;
import com.infiproton.maps.cache.CachedGeocodeEntry;
import com.infiproton.maps.cache.GeocodeCacheStore;
import com.infiproton.maps.dto.GeocodeResponse;
import com.infiproton.maps.model.GeocodeStatus;
import lombok.AllArgsConstructor;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class GeocodingService {
    private final GeoApiContext geoApiContext;
    private final GeocodeCacheStore geocodeCacheStore;

    public byte[] bulkGeocode(InputStream inputStream) throws IOException {
        List<CSVRecord> records;
        try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVParser parser = CSVFormat.DEFAULT
                     .withFirstRecordAsHeader()
                     .parse(reader)) {

            records = parser.getRecords();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (OutputStreamWriter writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        CSVPrinter printer = new CSVPrinter(writer, CSVFormat.DEFAULT.withHeader(
                "input_address",
                "formatted_address",
                "latitude",
                "longitude",
                "status",
                "partial_match",
                "ambiguous"
        ))) {
            for (CSVRecord record : records) {
                String rawAddress = record.get("address");
                GeocodeResponse geo = geocode(rawAddress);

                printer.printRecord(rawAddress,
                        geo.getFormattedAddress(),
                        geo.getLatitude(),
                        geo.getLongitude(),
                        geo.getStatus(),
                        geo.isPartialMatch(), geo.isAmbiguous());
            }
        }

        return baos.toByteArray();
    }

    public GeocodeResponse geocode(String address) {

        String cacheKey = normalizeKey(address);

        // 1. Check cache
        CachedGeocodeEntry cached = geocodeCacheStore.get(cacheKey);
        if(cached != null && !cached.isExpired()) {
            return (GeocodeResponse) cached.getResponse();
        }

        // 2. Invoke google
        GeocodeResponse response = invokeGoogle(address);

        // 3. Store response in the cache
        if (response.getStatus() == GeocodeStatus.OK ||
                response.getStatus() == GeocodeStatus.ZERO_RESULTS) {
            CachedGeocodeEntry entry = new CachedGeocodeEntry();
            entry.setResponse(response);
            entry.setExpiresAt(Instant.now().plusSeconds(3600)); // 1 hour
            geocodeCacheStore.put(cacheKey, entry);
        }

        return response;
    }

    public GeocodeResponse invokeGoogle(String address) {
        try {
            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, address).await();
            GeocodeResponse response = new GeocodeResponse();
            if (results == null || results.length == 0) {
                response.setStatus(GeocodeStatus.ZERO_RESULTS);
                return response;
            }

            GeocodingResult result = results[0];
            int resultCount = results.length;

            response.setStatus(GeocodeStatus.OK);
            response.setFormattedAddress(result.formattedAddress);
            response.setLatitude(result.geometry.location.lat);
            response.setLongitude(result.geometry.location.lng);
            response.setPlaceId(result.placeId);
            response.setPartialMatch(result.partialMatch);

            if(result.types != null) {
                List<String> types = Arrays.stream(result.types)
                        .map(AddressType::toCanonicalLiteral)
                        .toList();
                response.setTypes(types);
            }

            response.setResultCount(resultCount);
            response.setAmbiguous(resultCount > 1);
            if (resultCount > 1) {
                List<String> candidates = Arrays.stream(results)
                        .map(r -> r.formattedAddress)
                        .toList();
                response.setCandidateAddresses(candidates);
            }

            return response;
        } catch (InvalidRequestException e) {
            return errorResponse(GeocodeStatus.INVALID_REQUEST);
        } catch (RequestDeniedException e) {
            return errorResponse(GeocodeStatus.REQUEST_DENIED);
        } catch (OverQueryLimitException e) {
            return errorResponse(GeocodeStatus.OVER_QUERY_LIMIT);
        } catch (ApiException e) {
            return errorResponse(GeocodeStatus.UNKNOWN_ERROR);
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException("Failed to call Geocoding API", e);
        }
    }

    private GeocodeResponse errorResponse(GeocodeStatus status) {
        GeocodeResponse dto = new GeocodeResponse();
        dto.setStatus(status);
        return dto;
    }

    private String normalizeKey(String address) {
        return address.trim().toLowerCase();
    }

}
