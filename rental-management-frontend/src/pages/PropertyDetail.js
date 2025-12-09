import React, { useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import './PropertyDetail.css';

const PropertyDetail = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const { isAuthenticated } = useAuth();
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [message, setMessage] = useState('');

  const { data: property, isLoading, error } = useQuery({
    queryKey: ['property', id],
    queryFn: async () => {
      const response = await api.get(`/properties/${id}`);
      return response.data;
    }
  });

  const handleRent = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }

    if (!startDate || !endDate) {
      setMessage('Please select start and end dates');
      return;
    }

    try {
      await api.post('/transactions', null, {
        params: {
          propertyId: id,
          startDate: new Date(startDate).getTime(),
          endDate: new Date(endDate).getTime()
        }
      });
      setMessage('Rental request submitted successfully!');
    } catch (error) {
      setMessage(error.response?.data?.message || 'Failed to submit rental request');
    }
  };

  if (isLoading) return <div className="loading">Loading property...</div>;
  if (error) return <div className="error">Error loading property: {error.message}</div>;
  if (!property) return <div className="error">Property not found</div>;

  return (
    <div className="container">
      <div className="property-detail">
        <div className="property-detail-image">
          {property.images && property.images.length > 0 ? (
            <img src={property.images[0]} alt={property.title} />
          ) : (
            <div className="placeholder-image">🏠</div>
          )}
        </div>
        <div className="property-detail-content">
          <h1>{property.title}</h1>
          <p className="property-location">
            {property.address}, {property.city}, {property.state} {property.zipCode}
          </p>
          <p className="property-price">${property.price?.toLocaleString()}/month</p>
          
          <div className="property-info">
            <div className="info-item">
              <strong>Type:</strong> {property.type}
            </div>
            {property.bedrooms && (
              <div className="info-item">
                <strong>Bedrooms:</strong> {property.bedrooms}
              </div>
            )}
            {property.bathrooms && (
              <div className="info-item">
                <strong>Bathrooms:</strong> {property.bathrooms}
              </div>
            )}
            {property.area && (
              <div className="info-item">
                <strong>Area:</strong> {property.area} sqft
              </div>
            )}
            <div className="info-item">
              <strong>Status:</strong> {property.status}
            </div>
          </div>

          {property.description && (
            <div className="property-description">
              <h3>Description</h3>
              <p>{property.description}</p>
            </div>
          )}

          {property.amenities && property.amenities.length > 0 && (
            <div className="property-amenities">
              <h3>Amenities</h3>
              <ul>
                {property.amenities.map((amenity, index) => (
                  <li key={index}>{amenity}</li>
                ))}
              </ul>
            </div>
          )}

          {property.status === 'AVAILABLE' && (
            <div className="rent-section">
              <h3>Rent This Property</h3>
              {message && (
                <div className={message.includes('success') ? 'success' : 'error'}>
                  {message}
                </div>
              )}
              <div className="form-group">
                <label>Start Date</label>
                <input
                  type="date"
                  value={startDate}
                  onChange={(e) => setStartDate(e.target.value)}
                  min={new Date().toISOString().split('T')[0]}
                />
              </div>
              <div className="form-group">
                <label>End Date</label>
                <input
                  type="date"
                  value={endDate}
                  onChange={(e) => setEndDate(e.target.value)}
                  min={startDate || new Date().toISOString().split('T')[0]}
                />
              </div>
              <button onClick={handleRent} className="btn btn-primary">
                Request Rental
              </button>
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default PropertyDetail;

