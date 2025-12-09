package com.rental.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Document(collection = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {
    @Id
    private String id;
    
    private String title;
    private String description;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location; // [longitude, latitude]
    
    private PropertyType type; // APARTMENT, HOUSE, CONDO, TOWNHOUSE
    private BigDecimal price;
    private Integer bedrooms;
    private Integer bathrooms;
    private Double area; // square feet
    private List<String> amenities;
    private List<String> images;
    
    @Indexed
    private String ownerId; // User ID
    
    private PropertyStatus status = PropertyStatus.AVAILABLE; // AVAILABLE, RENTED, MAINTENANCE
    private Date createdAt;
    private Date updatedAt;
    
    public enum PropertyType {
        APARTMENT, HOUSE, CONDO, TOWNHOUSE
    }
    
    public enum PropertyStatus {
        AVAILABLE, RENTED, MAINTENANCE
    }
}

