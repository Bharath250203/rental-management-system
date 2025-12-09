import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../services/api';
import './Home.css';

const Home = () => {
  const navigate = useNavigate();
  const [searchParams, setSearchParams] = useState({
    city: '',
    type: '',
    minPrice: '',
    maxPrice: ''
  });

  const handleSearch = (e) => {
    e.preventDefault();
    const params = new URLSearchParams();
    if (searchParams.city) params.append('city', searchParams.city);
    if (searchParams.type) params.append('type', searchParams.type);
    if (searchParams.minPrice) params.append('minPrice', searchParams.minPrice);
    if (searchParams.maxPrice) params.append('maxPrice', searchParams.maxPrice);
    
    navigate(`/properties?${params.toString()}`);
  };

  return (
    <div>
      <div className="hero">
        <h1>Find Your Perfect Rental</h1>
        <p>Discover amazing properties in your area</p>
      </div>
      
      <div className="container">
        <div className="search-section">
          <h2>Search Properties</h2>
          <form onSubmit={handleSearch} className="search-form">
            <div className="form-group">
              <label>City</label>
              <input
                type="text"
                value={searchParams.city}
                onChange={(e) => setSearchParams({...searchParams, city: e.target.value})}
                placeholder="Enter city"
              />
            </div>
            <div className="form-group">
              <label>Type</label>
              <select
                value={searchParams.type}
                onChange={(e) => setSearchParams({...searchParams, type: e.target.value})}
              >
                <option value="">All Types</option>
                <option value="APARTMENT">Apartment</option>
                <option value="HOUSE">House</option>
                <option value="CONDO">Condo</option>
                <option value="TOWNHOUSE">Townhouse</option>
              </select>
            </div>
            <div className="form-group">
              <label>Min Price</label>
              <input
                type="number"
                value={searchParams.minPrice}
                onChange={(e) => setSearchParams({...searchParams, minPrice: e.target.value})}
                placeholder="Min price"
              />
            </div>
            <div className="form-group">
              <label>Max Price</label>
              <input
                type="number"
                value={searchParams.maxPrice}
                onChange={(e) => setSearchParams({...searchParams, maxPrice: e.target.value})}
                placeholder="Max price"
              />
            </div>
          </form>
          <button type="submit" onClick={handleSearch} className="btn btn-primary">
            Search Properties
          </button>
        </div>
      </div>
    </div>
  );
};

export default Home;

