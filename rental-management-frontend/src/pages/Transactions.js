import React, { useState } from 'react';
import { useQuery } from '@tanstack/react-query';
import api from '../services/api';
import './Transactions.css';

const Transactions = () => {
  const [page, setPage] = useState(0);
  const [view, setView] = useState('tenant'); // 'tenant' or 'owner'

  const { data, isLoading, error } = useQuery({
    queryKey: ['transactions', view, page],
    queryFn: async () => {
      const endpoint = view === 'owner' ? '/transactions/owner' : '/transactions';
      const response = await api.get(endpoint, {
        params: { page, size: 20 }
      });
      return response.data;
    }
  });

  if (isLoading) return <div className="loading">Loading transactions...</div>;
  if (error) return <div className="error">Error loading transactions: {error.message}</div>;

  return (
    <div className="container">
      <div className="transactions-header">
        <h1>Transactions</h1>
        <div className="view-toggle">
          <button
            className={view === 'tenant' ? 'btn btn-primary' : 'btn btn-secondary'}
            onClick={() => { setView('tenant'); setPage(0); }}
          >
            My Rentals
          </button>
          <button
            className={view === 'owner' ? 'btn btn-primary' : 'btn btn-secondary'}
            onClick={() => { setView('owner'); setPage(0); }}
          >
            My Properties
          </button>
        </div>
      </div>

      <div className="transactions-list">
        {data?.transactions?.length === 0 ? (
          <div className="no-results">No transactions found</div>
        ) : (
          data?.transactions?.map(transaction => (
            <div key={transaction.id} className="transaction-card">
              <div className="transaction-info">
                <h3>Transaction #{transaction.id.substring(0, 8)}</h3>
                <p><strong>Property ID:</strong> {transaction.propertyId}</p>
                <p><strong>Amount:</strong> ${transaction.amount?.toLocaleString()}</p>
                <p><strong>Start Date:</strong> {new Date(transaction.startDate).toLocaleDateString()}</p>
                <p><strong>End Date:</strong> {new Date(transaction.endDate).toLocaleDateString()}</p>
                <p><strong>Status:</strong> 
                  <span className={`status status-${transaction.status.toLowerCase()}`}>
                    {transaction.status}
                  </span>
                </p>
              </div>
              {view === 'owner' && transaction.status === 'PENDING' && (
                <button
                  onClick={async () => {
                    try {
                      await api.put(`/transactions/${transaction.id}/approve`);
                      window.location.reload();
                    } catch (error) {
                      alert('Failed to approve transaction');
                    }
                  }}
                  className="btn btn-primary"
                >
                  Approve
                </button>
              )}
            </div>
          ))
        )}
      </div>

      {data && data.totalPages > 1 && (
        <div className="pagination">
          <button
            onClick={() => setPage(page - 1)}
            disabled={page === 0}
            className="btn btn-secondary"
          >
            Previous
          </button>
          <span>Page {page + 1} of {data.totalPages}</span>
          <button
            onClick={() => setPage(page + 1)}
            disabled={page >= data.totalPages - 1}
            className="btn btn-secondary"
          >
            Next
          </button>
        </div>
      )}
    </div>
  );
};

export default Transactions;

