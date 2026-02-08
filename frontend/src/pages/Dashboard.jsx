import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Dashboard() {
  const { user } = useAuth();

  return (
    <div style={{ maxWidth: '800px', margin: '50px auto', padding: '20px' }}>
      <h1 style={{ marginBottom: '10px' }}>Welcome, {user?.fullName}! 👋</h1>
      <p style={{ color: '#666', marginBottom: '40px' }}>{user?.email}</p>

      <div style={{ 
        display: 'grid', 
        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
        gap: '20px',
        marginBottom: '40px'
      }}>
        <Link
          to="/create-cv"
          style={{
            display: 'block',
            padding: '30px',
            backgroundColor: '#007bff',
            color: 'white',
            textDecoration: 'none',
            borderRadius: '8px',
            textAlign: 'center',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
          }}
        >
          <h3 style={{ margin: '0 0 10px 0' }}>📝 Create CV</h3>
          <p style={{ margin: 0, fontSize: '14px' }}>Build your professional profile</p>
        </Link>

        <Link
          to="/directory"
          style={{
            display: 'block',
            padding: '30px',
            backgroundColor: '#28a745',
            color: 'white',
            textDecoration: 'none',
            borderRadius: '8px',
            textAlign: 'center',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
          }}
        >
          <h3 style={{ margin: '0 0 10px 0' }}>📖 Browse Directory</h3>
          <p style={{ margin: 0, fontSize: '14px' }}>Search verified professionals</p>
        </Link>
      </div>

      <div style={{ 
        padding: '20px', 
        backgroundColor: '#f8f9fa', 
        borderRadius: '8px',
        border: '1px solid #dee2e6'
      }}>
        <h3 style={{ marginTop: 0 }}>Quick Actions</h3>
        <ul style={{ lineHeight: '2' }}>
          <li>Create your CV and add education/experience</li>
          <li>Join the public directory to be discoverable</li>
          <li>Search for other professionals</li>
          <li>Get verified to earn badges</li>
        </ul>
      </div>
    </div>
  );
}

export default Dashboard;