import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar() {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <nav style={{
      backgroundColor: '#007bff',
      padding: '15px 0',
      boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
    }}>
      <div style={{
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '0 20px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center'
      }}>
        <Link
          to="/"
          style={{
            color: 'white',
            textDecoration: 'none',
            fontSize: '24px',
            fontWeight: 'bold'
          }}
        >
          📖 VeriCV
        </Link>

        <div style={{ display: 'flex', gap: '20px', alignItems: 'center' }}>
          <Link
            to="/directory"
            style={{
              color: 'white',
              textDecoration: 'none',
              fontWeight: '500'
            }}
          >
            Directory
          </Link>

          {isAuthenticated ? (
            <>
              <Link
                to="/dashboard"
                style={{
                  color: 'white',
                  textDecoration: 'none',
                  fontWeight: '500'
                }}
              >
                Dashboard
              </Link>
              <span style={{ color: 'white' }}>
                Hi, {user?.fullName}
              </span>
              <button
                onClick={handleLogout}
                style={{
                  padding: '8px 20px',
                  backgroundColor: 'white',
                  color: '#007bff',
                  border: 'none',
                  borderRadius: '4px',
                  fontWeight: 'bold',
                  cursor: 'pointer'
                }}
              >
                Logout
              </button>
            </>
          ) : (
            <>
              <Link
                to="/login"
                style={{
                  color: 'white',
                  textDecoration: 'none',
                  fontWeight: '500'
                }}
              >
                Login
              </Link>
              <Link
                to="/register"
                style={{
                  padding: '8px 20px',
                  backgroundColor: 'white',
                  color: '#007bff',
                  textDecoration: 'none',
                  borderRadius: '4px',
                  fontWeight: 'bold'
                }}
              >
                Register
              </Link>
            </>
          )}
        </div>
      </div>
    </nav>
  );
}

export default Navbar;