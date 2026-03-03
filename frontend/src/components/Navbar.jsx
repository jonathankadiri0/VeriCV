import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

function Navbar() {
  const { isAuthenticated, user, logout } = useAuth();
  const navigate = useNavigate();
  const [menuOpen, setMenuOpen] = useState(false);

  const handleLogout = () => {
    logout();
    setMenuOpen(false);
    navigate('/');
  };

  const closeMenu = () => setMenuOpen(false);

  return (
    <nav style={{
      backgroundColor: '#007bff',
      padding: '15px 0',
      boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
      position: 'relative',
      zIndex: 1000
    }}>
      <div style={{
        maxWidth: '1200px',
        margin: '0 auto',
        padding: '0 20px',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        flexWrap: 'wrap'
      }}>
        <Link to="/" onClick={closeMenu} style={{ color: 'white', textDecoration: 'none', fontSize: '24px', fontWeight: 'bold' }}>
          📖 VeriCV
        </Link>

        <button
          onClick={() => setMenuOpen(!menuOpen)}
          className="nav-hamburger"
          style={{
            display: 'none',
            background: 'none',
            border: '2px solid white',
            borderRadius: '4px',
            color: 'white',
            fontSize: '20px',
            cursor: 'pointer',
            padding: '4px 8px'
          }}
        >
          ☰
        </button>

        <div className={'nav-links' + (menuOpen ? ' nav-links-open' : '')}
          style={{ display: 'flex', gap: '20px', alignItems: 'center' }}
        >
          <Link to="/directory" onClick={closeMenu} style={{ color: 'white', textDecoration: 'none', fontWeight: '500' }}>
            Directory
          </Link>

          {isAuthenticated ? (
            <>
              <Link to="/dashboard" onClick={closeMenu} style={{ color: 'white', textDecoration: 'none', fontWeight: '500' }}>
                Dashboard
              </Link>
              <span style={{ color: 'rgba(255,255,255,0.85)', fontSize: '14px' }}>
                Hi, {user?.fullName?.split(' ')[0]}
              </span>
              <button onClick={handleLogout} style={{
                padding: '8px 20px', backgroundColor: 'white', color: '#007bff',
                border: 'none', borderRadius: '4px', fontWeight: 'bold', cursor: 'pointer'
              }}>
                Logout
              </button>
            </>
          ) : (
            <>
              <Link to="/login" onClick={closeMenu} style={{ color: 'white', textDecoration: 'none', fontWeight: '500' }}>
                Login
              </Link>
              <Link to="/register" onClick={closeMenu} style={{
                padding: '8px 20px', backgroundColor: 'white', color: '#007bff',
                textDecoration: 'none', borderRadius: '4px', fontWeight: 'bold'
              }}>
                Register
              </Link>
            </>
          )}
        </div>
      </div>

      <style>{`
        @media (max-width: 768px) {
          .nav-hamburger { display: block !important; }
          .nav-links {
            display: none !important;
            width: 100%;
            flex-direction: column;
            gap: 15px;
            padding-top: 15px;
            margin-top: 15px;
            border-top: 1px solid rgba(255,255,255,0.2);
          }
          .nav-links-open { display: flex !important; }
        }
      `}</style>
    </nav>
  );
}

export default Navbar;