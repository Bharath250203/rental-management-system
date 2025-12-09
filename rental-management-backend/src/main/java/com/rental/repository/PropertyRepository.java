package com.rental.repository;

import com.rental.model.Property;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Point;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PropertyRepository extends MongoRepository<Property, String> {
    Page<Property> findByStatus(Property.PropertyStatus status, Pageable pageable);
    
    Page<Property> findByCityAndStatus(String city, Property.PropertyStatus status, Pageable pageable);
    
    Page<Property> findByTypeAndStatus(Property.PropertyType type, Property.PropertyStatus status, Pageable pageable);
    
    Page<Property> findByPriceBetweenAndStatus(BigDecimal minPrice, BigDecimal maxPrice, Property.PropertyStatus status, Pageable pageable);
    
    List<Property> findByLocationNear(Point point, Distance distance);
    
    @Query("{ 'location': { $near: { $geometry: { type: 'Point', coordinates: [?1, ?0] }, $maxDistance: ?2 } } }")
    List<Property> findNearbyProperties(double latitude, double longitude, double maxDistance);
    
    Page<Property> findByOwnerId(String ownerId, Pageable pageable);
}

