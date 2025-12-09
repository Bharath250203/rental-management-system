package com.rental.controller;

import com.rental.dto.PropertyRequest;
import com.rental.dto.PropertySearchRequest;
import com.rental.model.Property;
import com.rental.service.PropertyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/properties")
@CrossOrigin(origins = "http://localhost:3000")
public class PropertyController {
    
    @Autowired
    private PropertyService propertyService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProperties(
            @RequestParam(required = false) Double latitude,
            @RequestParam(required = false) Double longitude,
            @RequestParam(required = false) Double radius,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PropertySearchRequest searchRequest = new PropertySearchRequest();
        searchRequest.setLatitude(latitude);
        searchRequest.setLongitude(longitude);
        searchRequest.setRadius(radius);
        searchRequest.setCity(city);
        searchRequest.setType(type);
        searchRequest.setMinPrice(minPrice);
        searchRequest.setMaxPrice(maxPrice);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        
        Page<Property> properties = propertyService.searchProperties(searchRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("properties", properties.getContent());
        response.put("currentPage", properties.getNumber());
        response.put("totalItems", properties.getTotalElements());
        response.put("totalPages", properties.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Property> getPropertyById(@PathVariable String id) {
        Property property = propertyService.getPropertyById(id);
        return ResponseEntity.ok(property);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchProperties(
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double radius,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        PropertySearchRequest searchRequest = new PropertySearchRequest();
        searchRequest.setLatitude(lat);
        searchRequest.setLongitude(lng);
        searchRequest.setRadius(radius != null ? radius : 5000);
        searchRequest.setPage(page);
        searchRequest.setSize(size);
        
        Page<Property> properties = propertyService.searchProperties(searchRequest);
        
        Map<String, Object> response = new HashMap<>();
        response.put("properties", properties.getContent());
        response.put("currentPage", properties.getNumber());
        response.put("totalItems", properties.getTotalElements());
        response.put("totalPages", properties.getTotalPages());
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Property> createProperty(@Valid @RequestBody PropertyRequest request) {
        Property property = propertyService.createProperty(request);
        return ResponseEntity.ok(property);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Property> updateProperty(@PathVariable String id, 
                                                   @Valid @RequestBody PropertyRequest request) {
        Property property = propertyService.updateProperty(id, request);
        return ResponseEntity.ok(property);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Void> deleteProperty(@PathVariable String id) {
        propertyService.deleteProperty(id);
        return ResponseEntity.noContent().build();
    }
}

