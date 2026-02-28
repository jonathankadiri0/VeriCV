import { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { directoryAPI, cvAPI } from '../services/api';

function PublicProfile() {
  const { userId } = useParams();
  const [profile, setProfile] = useState(null);
  const [cv, setCV] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    loadProfile();
  }, [userId]);

  const loadProfile = async () => {
    setLoading(true);
    setError('');
    
    try {
      const profileResponse = await directoryAPI.getProfile(userId);
      setProfile(profileResponse.data);
      
      // Try to get CV details
      try {
        const cvResponse = await cvAPI.getByUserId(userId);
        setCV(cvResponse.data);
      } catch (cvErr) {
        console.log('No CV found for user');
      }
    } catch (err) {
      setError('Failed to load profile. Please try again.');
      console.error(err);
    } finally {
      setLoading(false);
    }
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

  if (loading) {
    return (
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
        <p style={{ marginTop: '20px', color: '#666' }}>Loading profile...</p>
        <style>
          {`
            @keyframes spin {
              0% { transform: rotate(0deg); }
              100% { transform: rotate(360deg); }
            }
          `}
        </style>
      </div>
    );
  }

  if (error || !profile) {
    return (
      <div style={{ textAlign: 'center', padding: '50px' }}>
        <p style={{ fontSize: '18px', color: '#c33' }}>{error || 'Profile not found'}</p>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '900px', margin: '30px auto', padding: '20px' }}>
      {/* Header */}
      <div style={{ 
        backgroundColor: 'white', 
        padding: '30px', 
        borderRadius: '8px', 
        marginBottom: '20px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
      }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
          <div>
            <h1 style={{ margin: '0 0 10px 0' }}>{profile.fullName}</h1>
            {profile.headline && (
              <p style={{ fontSize: '18px', color: '#666', margin: '0 0 10px 0' }}>
                {profile.headline}
              </p>
            )}
            {profile.location && (
              <p style={{ color: '#666', margin: '0' }}>
                📍 {profile.location}
              </p>
            )}
          </div>
          <div style={{ 
            display: 'inline-block',
            padding: '8px 16px',
            backgroundColor: getBadgeColor(profile.verificationBadge),
            color: profile.verificationBadge === 'GOLD' ? '#000' : '#fff',
            borderRadius: '20px',
            fontSize: '16px',
            fontWeight: 'bold'
          }}>
            {profile.verificationBadge}
          </div>
        </div>
      </div>

      {/* CV Content */}
      {cv ? (
        <>
          {/* Summary */}
          {cv.cv?.summary && (
            <div style={{ 
              backgroundColor: 'white', 
              padding: '30px', 
              borderRadius: '8px', 
              marginBottom: '20px',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}>
              <h2 style={{ marginTop: 0 }}>About</h2>
              <p style={{ lineHeight: '1.6' }}>{cv.cv.summary}</p>
            </div>
          )}

          {/* Education */}
          {cv.education && cv.education.length > 0 && (
            <div style={{ 
              backgroundColor: 'white', 
              padding: '30px', 
              borderRadius: '8px', 
              marginBottom: '20px',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}>
              <h2 style={{ marginTop: 0 }}>Education</h2>
              {cv.education.map((edu, index) => (
                <div key={index} style={{ marginBottom: '20px', paddingBottom: '20px', borderBottom: index < cv.education.length - 1 ? '1px solid #eee' : 'none' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start' }}>
                    <div>
                      <h3 style={{ margin: '0 0 5px 0' }}>{edu.institution}</h3>
                      <p style={{ margin: '0 0 5px 0', fontSize: '16px', fontWeight: '500' }}>
                        {edu.degree} - {edu.fieldOfStudy}
                      </p>
                      <p style={{ margin: '0', color: '#666', fontSize: '14px' }}>
                        {edu.startDate} - {edu.endDate}
                      </p>
                    </div>
                    {edu.isVerified && (
                      <span style={{ color: '#28a745', fontWeight: 'bold' }}>✓ Verified</span>
                    )}
                  </div>
                </div>
              ))}
            </div>
          )}

          {/* Experience */}
          {cv.experience && cv.experience.length > 0 && (
            <div style={{ 
              backgroundColor: 'white', 
              padding: '30px', 
              borderRadius: '8px', 
              marginBottom: '20px',
              boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
            }}>
              <h2 style={{ marginTop: 0 }}>Experience</h2>
              {cv.experience.map((exp, index) => (
                <div key={index} style={{ marginBottom: '20px', paddingBottom: '20px', borderBottom: index < cv.experience.length - 1 ? '1px solid #eee' : 'none' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'start', marginBottom: '10px' }}>
                    <div>
                      <h3 style={{ margin: '0 0 5px 0' }}>{exp.role}</h3>
                      <p style={{ margin: '0 0 5px 0', fontSize: '16px', fontWeight: '500' }}>
                        {exp.company}
                      </p>
                      <p style={{ margin: '0', color: '#666', fontSize: '14px' }}>
                        {exp.startDate} - {exp.isCurrent ? 'Present' : exp.endDate}
                      </p>
                    </div>
                    {exp.isVerified && (
                      <span style={{ color: '#28a745', fontWeight: 'bold' }}>✓ Verified</span>
                    )}
                  </div>
                  {exp.description && (
                    <p style={{ margin: '10px 0 0 0', lineHeight: '1.6' }}>{exp.description}</p>
                  )}
                </div>
              ))}
            </div>
          )}
        </>
      ) : (
        <div style={{ 
          backgroundColor: 'white', 
          padding: '30px', 
          borderRadius: '8px',
          textAlign: 'center',
          color: '#666'
        }}>
          <p>This user hasn't added detailed CV information yet.</p>
        </div>
      )}
    </div>
  );
}

export default PublicProfile;