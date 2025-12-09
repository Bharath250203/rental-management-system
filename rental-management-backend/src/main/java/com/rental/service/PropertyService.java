package com.rental.service;

import com.rental.dto.PropertyRequest;
import com.rental.dto.PropertySearchRequest;
import com.rental.model.Property;
import com.rental.model.User;
import com.rental.repository.PropertyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class PropertyService {
    
    @Autowired
    private PropertyRepository propertyRepository;
    
    @Autowired
    private UserService userService;
    
    @Cacheable(value = "properties", key = "#id")
    public Property getPropertyById(String id) {
        return propertyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Property not found"));
    }
    
    @Cacheable(value = "properties", key = "#searchRequest.toString()")
    public Page<Property> searchProperties(PropertySearchRequest searchRequest) {
        Pageable pageable = PageRequest.of(searchRequest.getPage(), searchRequest.getSize());
        
        // Geospatial search
        if (searchRequest.getLatitude() != null && searchRequest.getLongitude() != null) {
            double radius = searchRequest.getRadius() != null ? searchRequest.getRadius() : 5000; // default 5km
            List<Property> nearbyProperties = propertyRepository.findNearbyProperties(
                    searchRequest.getLatitude(),
                    searchRequest.getLongitude(),
                    radius
            );
            
            // Apply additional filters
            return filterProperties(nearbyProperties, searchRequest, pageable);
        }
        
        // Regular search with filters
        if (searchRequest.getCity() != null && !searchRequest.getCity().isEmpty()) {
            return propertyRepository.findByCityAndStatus(
                    searchRequest.getCity(),
                    Property.PropertyStatus.AVAILABLE,
                    pageable
            );
        }
        
        if (searchRequest.getType() != null && !searchRequest.getType().isEmpty()) {
            Property.PropertyType type = Property.PropertyType.valueOf(searchRequest.getType().toUpperCase());
            return propertyRepository.findByTypeAndStatus(type, Property.PropertyStatus.AVAILABLE, pageable);
        }
        
        if (searchRequest.getMinPrice() != null || searchRequest.getMaxPrice() != null) {
            BigDecimal minPrice = searchRequest.getMinPrice() != null ? 
                    BigDecimal.valueOf(searchRequest.getMinPrice()) : BigDecimal.ZERO;
            BigDecimal maxPrice = searchRequest.getMaxPrice() != null ? 
                    BigDecimal.valueOf(searchRequest.getMaxPrice()) : BigDecimal.valueOf(Double.MAX_VALUE);
            return propertyRepository.findByPriceBetweenAndStatus(
                    minPrice, maxPrice, Property.PropertyStatus.AVAILABLE, pageable);
        }
        
        return propertyRepository.findByStatus(Property.PropertyStatus.AVAILABLE, pageable);
    }
    
    private Page<Property> filterProperties(List<Property> properties, PropertySearchRequest searchRequest, Pageable pageable) {
        // Simple filtering - in production, use more sophisticated approach
        List<Property> filtered = properties.stream()
                .filter(p -> searchRequest.getType() == null || 
                        p.getType().name().equalsIgnoreCase(searchRequest.getType()))
                .filter(p -> searchRequest.getMinPrice() == null || 
                        p.getPrice().compareTo(BigDecimal.valueOf(searchRequest.getMinPrice())) >= 0)
                .filter(p -> searchRequest.getMaxPrice() == null || 
                        p.getPrice().compareTo(BigDecimal.valueOf(searchRequest.getMaxPrice())) <= 0)
                .toList();
        
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), filtered.size());
        List<Property> paged = filtered.subList(start, end);
        
        return new PageImpl<>(paged, pageable, filtered.size());
    }
    
    @CacheEvict(value = "properties", allEntries = true)
    public Property createProperty(PropertyRequest request) {
        User currentUser = userService.getCurrentUser();
        
        Property property = new Property();
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setZipCode(request.getZipCode());
        property.setCountry(request.getCountry());
        property.setLocation(new GeoJsonPoint(request.getLongitude(), request.getLatitude()));
        property.setType(request.getType());
        property.setPrice(request.getPrice());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setArea(request.getArea());
        property.setAmenities(request.getAmenities());
        property.setImages(request.getImages());
        property.setOwnerId(currentUser.getId());
        property.setStatus(Property.PropertyStatus.AVAILABLE);
        property.setCreatedAt(new Date());
        property.setUpdatedAt(new Date());
        
        return propertyRepository.save(property);
    }
    
    @CacheEvict(value = "properties", allEntries = true)
    public Property updateProperty(String id, PropertyRequest request) {
        Property property = getPropertyById(id);
        User currentUser = userService.getCurrentUser();
        
        if (!property.getOwnerId().equals(currentUser.getId()) && !currentUser.getRole().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized to update this property");
        }
        
        property.setTitle(request.getTitle());
        property.setDescription(request.getDescription());
        property.setAddress(request.getAddress());
        property.setCity(request.getCity());
        property.setState(request.getState());
        property.setZipCode(request.getZipCode());
        property.setCountry(request.getCountry());
        property.setLocation(new GeoJsonPoint(request.getLongitude(), request.getLatitude()));
        property.setType(request.getType());
        property.setPrice(request.getPrice());
        property.setBedrooms(request.getBedrooms());
        property.setBathrooms(request.getBathrooms());
        property.setArea(request.getArea());
        property.setAmenities(request.getAmenities());
        property.setImages(request.getImages());
        property.setUpdatedAt(new Date());
        
        return propertyRepository.save(property);
    }
    
    @CacheEvict(value = "properties", allEntries = true)
    public void deleteProperty(String id) {
        Property property = getPropertyById(id);
        User currentUser = userService.getCurrentUser();
        
        if (!property.getOwnerId().equals(currentUser.getId()) && !currentUser.getRole().equals("ADMIN")) {
            throw new RuntimeException("Unauthorized to delete this property");
        }
        
        propertyRepository.deleteById(id);
    }
    
    public Page<Property> getPropertiesByOwner(String ownerId, Pageable pageable) {
        return propertyRepository.findByOwnerId(ownerId, pageable);
    }
    
    @CacheEvict(value = "properties", allEntries = true)
    public Property updatePropertyStatus(String id, Property.PropertyStatus status) {
        Property property = getPropertyById(id);
        property.setStatus(status);
        property.setUpdatedAt(new Date());
        return propertyRepository.save(property);
    }
}

