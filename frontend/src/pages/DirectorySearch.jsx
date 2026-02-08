import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { directoryAPI } from '../services/api';

function DirectorySearch() {
  const [results, setResults] = useState([]);
  const [keyword, setKeyword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    searchDirectory('');
  }, []);

  const searchDirectory = async (searchTerm) => {
    setLoading(true);
    setError('');
    
    try {
      const response = await directoryAPI.search(searchTerm);
      setResults(response.data.results || []);
    } catch (err) {
      setError('Failed to load directory. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleSearch = (e) => {
    e.preventDefault();
    searchDirectory(keyword);
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
    <div style={{ maxWidth: '1200px', margin: '0 auto', padding: '20px' }}>
      <h1 style={{ textAlign: 'center', marginBottom: '10px' }}>
        📖 VeriCV Professional Directory
      </h1>
      <p style={{ textAlign: 'center', color: '#666', marginBottom: '30px' }}>
        Find verified professionals in our phone book
      </p>

      {/* Search Bar */}
      <form onSubmit={handleSearch} style={{ marginBottom: '30px' }}>
        <div style={{ display: 'flex', gap: '10px', maxWidth: '600px', margin: '0 auto' }}>
          <input
            type="text"
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
            placeholder="Search by name, company, university..."
            style={{
              flex: 1,
              padding: '12px',
              border: '1px solid #ccc',
              borderRadius: '4px',
              fontSize: '16px'
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
              fontWeight: 'bold',
              cursor: 'pointer'
            }}
          >
            Search
          </button>
        </div>
      </form>

      {error && (
        <div style={{ 
          padding: '15px', 
          backgroundColor: '#fee', 
          color: '#c33', 
          borderRadius: '4px', 
          marginBottom: '20px',
          textAlign: 'center'
        }}>
          {error}
        </div>
      )}

      {loading ? (
        <p style={{ textAlign: 'center', fontSize: '18px' }}>Loading...</p>
      ) : (
        <>
          <p style={{ marginBottom: '20px', color: '#666' }}>
            Found {results.length} professional{results.length !== 1 ? 's' : ''}
          </p>

          <div style={{ 
            display: 'grid', 
            gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
            gap: '20px'
          }}>
            {results.map((person) => (
              <div
                key={person.id}
                style={{
                  border: '1px solid #ddd',
                  borderRadius: '8px',
                  padding: '20px',
                  backgroundColor: 'white',
                  boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
                }}
              >
                <div style={{ marginBottom: '15px' }}>
                  <h3 style={{ margin: '0 0 5px 0', fontSize: '20px' }}>
                    {person.fullName}
                  </h3>
                  {person.headline && (
                    <p style={{ margin: '0', color: '#666', fontSize: '14px' }}>
                      {person.headline}
                    </p>
                  )}
                </div>

                {person.location && (
                  <p style={{ margin: '5px 0', color: '#666', fontSize: '14px' }}>
                    📍 {person.location}
                  </p>
                )}

                <div style={{ 
                  display: 'inline-block',
                  padding: '4px 12px',
                  backgroundColor: getBadgeColor(person.verificationBadge),
                  color: person.verificationBadge === 'GOLD' ? '#000' : '#fff',
                  borderRadius: '12px',
                  fontSize: '12px',
                  fontWeight: 'bold',
                  marginTop: '10px'
                }}>
                  {person.verificationBadge}
                </div>

                <Link
                  to={`/profile/${person.userId}`}
                  style={{
                    display: 'block',
                    marginTop: '15px',
                    padding: '10px',
                    backgroundColor: '#007bff',
                    color: 'white',
                    textAlign: 'center',
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

          {results.length === 0 && !loading && (
            <div style={{ textAlign: 'center', padding: '40px', color: '#666' }}>
              <p style={{ fontSize: '18px' }}>No professionals found.</p>
              <p>Try a different search term!</p>
            </div>
          )}
        </>
      )}
    </div>
  );
}

export default DirectorySearch;