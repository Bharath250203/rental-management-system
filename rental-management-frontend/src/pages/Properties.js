import React, { useState, useEffect } from 'react';
import { useNavigate, useSearchParams, Link } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';
import './Properties.css';

const Properties = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();
  const [filters, setFilters] = useState({
    city: searchParams.get('city') || '',
    type: searchParams.get('type') || '',
    minPrice: searchParams.get('minPrice') || '',
    maxPrice: searchParams.get('maxPrice') || '',
    page: 0
  });

  const { data, isLoading, error } = useQuery({
    queryKey: ['properties', filters],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (filters.city) params.append('city', filters.city);
      if (filters.type) params.append('type', filters.type);
      if (filters.minPrice) params.append('minPrice', filters.minPrice);
      if (filters.maxPrice) params.append('maxPrice', filters.maxPrice);
      params.append('page', filters.page);
      params.append('size', '20');
      
      const response = await api.get(`/properties?${params.toString()}`);
      return response.data;
    }
  });

  if (isLoading) return <div className="loading">Loading properties...</div>;
  if (error) return <div className="error">Error loading properties: {error.message}</div>;

  return (
    <div className="container">
      <div className="properties-header">
        <h1>Properties</h1>
        <div className="filters">
          <input
            type="text"
            placeholder="Search by city..."
            value={filters.city}
            onChange={(e) => setFilters({...filters, city: e.target.value, page: 0})}
          />
          <select
            value={filters.type}
            onChange={(e) => setFilters({...filters, type: e.target.value, page: 0})}
          >
            <option value="">All Types</option>
            <option value="APARTMENT">Apartment</option>
            <option value="HOUSE">House</option>
            <option value="CONDO">Condo</option>
            <option value="TOWNHOUSE">Townhouse</option>
          </select>
        </div>
      </div>

      <div className="properties-grid">
        {data?.properties?.map(property => (
          <Link key={property.id} to={`/properties/${property.id}`} className="property-card">
            <div className="property-image">
              {property.images && property.images.length > 0 ? (
                <img src={property.images[0]} alt={property.title} />
              ) : (
                <span>🏠</span>
              )}
            </div>
            <div className="property-content">
              <h3 className="property-title">{property.title}</h3>
              <p className="property-location">
                {property.address}, {property.city}, {property.state}
              </p>
              <p className="property-price">${property.price?.toLocaleString()}/month</p>
              <div className="property-details">
                {property.bedrooms && <span>🛏️ {property.bedrooms} beds</span>}
                {property.bathrooms && <span>🚿 {property.bathrooms} baths</span>}
                {property.area && <span>📐 {property.area} sqft</span>}
              </div>
            </div>
          </Link>
        ))}
      </div>

      {data?.properties?.length === 0 && (
        <div className="no-results">No properties found</div>
      )}

      {data && data.totalPages > 1 && (
        <div className="pagination">
          <button
            onClick={() => setFilters({...filters, page: filters.page - 1})}
            disabled={filters.page === 0}
            className="btn btn-secondary"
          >
            Previous
          </button>
          <span>Page {filters.page + 1} of {data.totalPages}</span>
          <button
            onClick={() => setFilters({...filters, page: filters.page + 1})}
            disabled={filters.page >= data.totalPages - 1}
            className="btn btn-secondary"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};

export default Properties;

