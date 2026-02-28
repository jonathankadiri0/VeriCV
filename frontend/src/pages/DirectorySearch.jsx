import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { directoryAPI } from '../services/api';

function DirectorySearch() {
  const [professionals, setProfessionals] = useState([]);
  const [searchQuery, setSearchQuery] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    searchDirectory('');
  }, []);

  const searchDirectory = async (query) => {
    setLoading(true);
    try {
      const response = await directoryAPI.search(query);
      // Backend returns { count, results } format
      const data = response.data.results || response.data;
      setProfessionals(Array.isArray(data) ? data : []);
    } catch (error) {
      console.error('Error searching directory:', error);
      setProfessionals([]);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    searchDirectory(searchQuery);
  };

  const getBadgeColor = (badge) => {
    const colors = {
      NONE: '#gray',
      BRONZE: '#cd7f32',
      SILVER: '#c0c0c0',
      GOLD: '#ffd700',
      PLATINUM: '#e5e4e2'
    };
    return colors[badge] || '#gray';
  };

  return (
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '30px 20px' }}>
      <div style={{ textAlign: 'center', marginBottom: '40px' }}>
        <h1 style={{ fontSize: '36px', marginBottom: '10px' }}>📖 VeriCV Professional Directory</h1>
        <p style={{ color: '#666', fontSize: '18px' }}>Find verified professionals in our phone book</p>
      </div>

      <form onSubmit={handleSearch} style={{ marginBottom: '30px' }}>
        <div style={{ display: 'flex', gap: '10px', maxWidth: '600px', margin: '0 auto' }}>
          <input
            type="text"
            placeholder="Search by name, company, university..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            style={{
              flex: 1,
              padding: '12px 20px',
              fontSize: '16px',
              border: '2px solid #ddd',
              borderRadius: '4px'
            }}
          />
          <button
            type="submit"
            style={{
              padding: '12px 30px',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              fontSize: '16px',
              cursor: 'pointer',
              fontWeight: 'bold'
            }}
          >
            Search
          </button>
        </div>
      </form>

      {loading ? (
        <div style={{ textAlign: 'center', padding: '50px' }}>
          <div style={{
            width: '50px',
            height: '50px',
            border: '5px solid #f3f3f3',
            borderTop: '5px solid #007bff',
            borderRadius: '50%',
            animation: 'spin 1s linear infinite',
            margin: '0 auto'
          }}></div>
          <p style={{ marginTop: '20px', color: '#666' }}>Loading professionals...</p>
          <style>
            {`
              @keyframes spin {
                0% { transform: rotate(0deg); }
                100% { transform: rotate(360deg); }
              }
            `}
          </style>
        </div>
      ) : (
        <>
          <p style={{ marginBottom: '20px', color: '#666' }}>
            Found {professionals.length} professional{professionals.length !== 1 ? 's' : ''}
          </p>

          {professionals.length === 0 ? (
            <div style={{ textAlign: 'center', padding: '50px', color: '#666' }}>
              <p style={{ fontSize: '18px' }}>No professionals found.</p>
              <p>Try a different search term!</p>
            </div>
          ) : (
            <div style={{
              display: 'grid',
              gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
              gap: '20px'
            }}>
              {professionals.map((professional) => (
                <div
                  key={professional.userId}
                  style={{
                    backgroundColor: 'white',
                    padding: '20px',
                    borderRadius: '8px',
                    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
                    transition: 'transform 0.2s, box-shadow 0.2s'
                  }}
                  onMouseEnter={(e) => {
                    e.currentTarget.style.transform = 'translateY(-4px)';
                    e.currentTarget.style.boxShadow = '0 4px 12px rgba(0,0,0,0.15)';
                  }}
                  onMouseLeave={(e) => {
                    e.currentTarget.style.transform = 'translateY(0)';
                    e.currentTarget.style.boxShadow = '0 2px 4px rgba(0,0,0,0.1)';
                  }}
                >
                  <h3 style={{ margin: '0 0 10px 0', fontSize: '20px' }}>{professional.fullName}</h3>
                  
                  {professional.headline && (
                    <p style={{ color: '#666', margin: '0 0 10px 0', fontSize: '14px' }}>
                      {professional.headline}
                    </p>
                  )}

                  {professional.location && (
                    <p style={{ color: '#666', margin: '0 0 15px 0', fontSize: '14px' }}>
                      📍 {professional.location}
                    </p>
                  )}

                  <div style={{
                    display: 'inline-block',
                    padding: '4px 12px',
                    backgroundColor: getBadgeColor(professional.verificationBadge),
                    color: professional.verificationBadge === 'GOLD' ? '#000' : '#fff',
                    borderRadius: '12px',
                    fontSize: '12px',
                    fontWeight: 'bold',
                    marginBottom: '15px'
                  }}>
                    {professional.verificationBadge}
                  </div>

                  <Link
                    to={`/profile/${professional.userId}`}
                    style={{
                      display: 'block',
                      textAlign: 'center',
                      padding: '10px',
                      backgroundColor: '#007bff',
                      color: 'white',
                      textDecoration: 'none',
                      borderRadius: '4px',
                      fontWeight: 'bold'
                    }}
                  >
                    View Profile
                  </Link>
                </div>
              ))}
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default DirectorySearch;