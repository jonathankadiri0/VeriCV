import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Home() {
  const { isAuthenticated } = useAuth();

  return (
    <div style={{ textAlign: 'center', padding: '50px 20px' }}>
      <h1 style={{ fontSize: '48px', marginBottom: '20px' }}>
        📖 VeriCV
      </h1>
      <p style={{ fontSize: '24px', color: '#666', marginBottom: '40px' }}>
        The Phone Book for Verified Professionals
      </p>

      <p style={{ maxWidth: '600px', margin: '0 auto 40px', fontSize: '18px', lineHeight: '1.6' }}>
        VeriCV is a searchable directory where only professionals with verified credentials appear.
        Combat CV fraud with cryptographic verification and automated credential checking.
      </p>

      <div style={{ display: 'flex', gap: '20px', justifyContent: 'center', marginBottom: '60px' }}>
        {!isAuthenticated ? (
          <>
            <Link
              to="/register"
              style={{
                padding: '15px 40px',
                backgroundColor: '#28a745',
                color: 'white',
                textDecoration: 'none',
                borderRadius: '4px',
                fontSize: '18px',
                fontWeight: 'bold'
              }}
            >
              Get Started
            </Link>
            <Link
              to="/login"
              style={{
                padding: '15px 40px',
                backgroundColor: '#007bff',
                color: 'white',
                textDecoration: 'none',
                borderRadius: '4px',
                fontSize: '18px',
                fontWeight: 'bold'
              }}
            >
              Login
            </Link>
          </>
        ) : (
          <Link
            to="/dashboard"
            style={{
              padding: '15px 40px',
              backgroundColor: '#007bff',
              color: 'white',
              textDecoration: 'none',
              borderRadius: '4px',
              fontSize: '18px',
              fontWeight: 'bold'
            }}
          >
            Go to Dashboard
          </Link>
        )}
        <Link
          to="/directory"
          style={{
            padding: '15px 40px',
            backgroundColor: '#6c757d',
            color: 'white',
            textDecoration: 'none',
            borderRadius: '4px',
            fontSize: '18px',
            fontWeight: 'bold'
          }}
        >
          Browse Directory
        </Link>
      </div>

      <div style={{ 
        maxWidth: '800px', 
        margin: '0 auto',
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))',
        gap: '30px',
        textAlign: 'center'
      }}>
        <div>
          <div style={{ fontSize: '48px', marginBottom: '10px' }}>✅</div>
          <h3>Verified Credentials</h3>
          <p style={{ color: '#666' }}>Only verified professionals appear</p>
        </div>
        <div>
          <div style={{ fontSize: '48px', marginBottom: '10px' }}>🔍</div>
          <h3>Easy Search</h3>
          <p style={{ color: '#666' }}>Find professionals by name, company, or university</p>
        </div>
        <div>
          <div style={{ fontSize: '48px', marginBottom: '10px' }}>🏆</div>
          <h3>Badge System</h3>
          <p style={{ color: '#666' }}>Earn badges for verified credentials</p>
        </div>
      </div>
    </div>
  );
}

export default Home;