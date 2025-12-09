package com.rental.dto;

import lombok.Data;

@Data
public class PropertySearchRequest {
    private Double latitude;
    private Double longitude;
    private Double radius; // in meters
    private String city;
    private String type;
    private Double minPrice;
    private Double maxPrice;
    private Integer page = 0;
    private Integer size = 20;
}

