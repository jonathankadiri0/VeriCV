import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { cvAPI, directoryAPI } from '../services/api';

function Dashboard() {
  const { user } = useAuth();
  const [hasCV, setHasCV] = useState(false);
  const [inDirectory, setInDirectory] = useState(false);
  const [directoryEntry, setDirectoryEntry] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      try {
        await cvAPI.getMyCV();
        setHasCV(true);
      } catch (e) {}

      try {
        const dirRes = await directoryAPI.getMyEntry();
        setDirectoryEntry(dirRes.data);
        setInDirectory(true);
      } catch (e) {}
    } catch (e) {
      console.error('Error loading dashboard:', e);
    } finally {
      setLoading(false);
    }
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <p style={{ color: '#666' }}>Loading dashboard...</p>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '800px', margin: '40px auto', padding: '20px' }}>
      <h1 style={{ marginBottom: '5px' }}>Welcome, {user?.fullName}! 👋</h1>
      <p style={{ color: '#666', marginBottom: '30px' }}>{user?.email}</p>

      {/* Status Cards */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '15px',
        marginBottom: '30px'
      }}>
        <div style={{
          padding: '20px',
          backgroundColor: hasCV ? '#d4edda' : '#fff3cd',
          borderRadius: '8px',
          border: '1px solid ' + (hasCV ? '#c3e6cb' : '#ffc107'),
          textAlign: 'center'
        }}>
          <div style={{ fontSize: '28px', marginBottom: '8px' }}>{hasCV ? '✅' : '📝'}</div>
          <strong>{hasCV ? 'CV Created' : 'No CV Yet'}</strong>
        </div>

        <div style={{
          padding: '20px',
          backgroundColor: inDirectory ? '#d4edda' : '#fff3cd',
          borderRadius: '8px',
          border: '1px solid ' + (inDirectory ? '#c3e6cb' : '#ffc107'),
          textAlign: 'center'
        }}>
          <div style={{ fontSize: '28px', marginBottom: '8px' }}>{inDirectory ? '📖' : '🔒'}</div>
          <strong>{inDirectory ? 'In Directory' : 'Not In Directory'}</strong>
        </div>

        {directoryEntry && (
          <div style={{
            padding: '20px',
            backgroundColor: '#e8f4fd',
            borderRadius: '8px',
            border: '1px solid #bee5eb',
            textAlign: 'center'
          }}>
            <div style={{ fontSize: '28px', marginBottom: '8px' }}>🏆</div>
            <strong>{directoryEntry.verificationBadge || 'BRONZE'}</strong>
          </div>
        )}
      </div>

      {/* Action Cards */}
      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
        gap: '20px',
        marginBottom: '30px'
      }}>
        <Link
          to="/create-cv"
          style={{
            display: 'block',
            padding: '25px',
            backgroundColor: hasCV ? '#6c757d' : '#007bff',
            color: 'white',
            textDecoration: 'none',
            borderRadius: '8px',
            textAlign: 'center',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
          }}
        >
          <h3 style={{ margin: '0 0 8px 0' }}>{hasCV ? '✏️ Edit CV' : '📝 Create CV'}</h3>
          <p style={{ margin: 0, fontSize: '14px', opacity: 0.9 }}>
            {hasCV ? 'Update your professional profile' : 'Build your professional profile'}
          </p>
        </Link>

        <Link
          to="/directory"
          style={{
            display: 'block',
            padding: '25px',
            backgroundColor: '#28a745',
            color: 'white',
            textDecoration: 'none',
            borderRadius: '8px',
            textAlign: 'center',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
          }}
        >
          <h3 style={{ margin: '0 0 8px 0' }}>📖 Browse Directory</h3>
          <p style={{ margin: 0, fontSize: '14px', opacity: 0.9 }}>Search verified professionals</p>
        </Link>

        {inDirectory && directoryEntry?.userId && (
          <Link
            to={'/profile/' + directoryEntry.userId}
            style={{
              display: 'block',
              padding: '25px',
              backgroundColor: '#17a2b8',
              color: 'white',
              textDecoration: 'none',
              borderRadius: '8px',
              textAlign: 'center',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}
          >
            <h3 style={{ margin: '0 0 8px 0' }}>👤 View My Public Profile</h3>
            <p style={{ margin: 0, fontSize: '14px', opacity: 0.9 }}>See how others see you</p>
          </Link>
        )}
      </div>

      {/* Getting Started Guide */}
      {!hasCV && (
        <div style={{
          padding: '20px',
          backgroundColor: '#f8f9fa',
          borderRadius: '8px',
          border: '1px solid #dee2e6'
        }}>
          <h3 style={{ marginTop: 0 }}>🚀 Getting Started</h3>
          <p style={{ lineHeight: '1.8', margin: 0 }}>
            1. <strong>Create your CV</strong> — add your education and work experience<br />
            2. <strong>Join the directory</strong> — your profile becomes searchable<br />
            3. <strong>Get discovered</strong> — employers can find and verify your credentials
          </p>
        </div>
      )}
    </div>
  );
}

export default Dashboard;