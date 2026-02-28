import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { cvAPI, directoryAPI } from '../services/api';

function CreateCV() {
  const [formData, setFormData] = useState({
    headline: '',
    summary: '',
    isPublic: true
  });
  const [education, setEducation] = useState([{
    institution: '',
    degree: '',
    fieldOfStudy: '',
    startDate: '',
    endDate: ''
  }]);
  const [experience, setExperience] = useState([{
    company: '',
    role: '',
    description: '',
    startDate: '',
    endDate: '',
    isCurrent: false
  }]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState(false);
  const navigate = useNavigate();

  const handleChange = (e) => {
    setFormData({
      ...formData,
      [e.target.name]: e.target.value
    });
  };

  const handleEducationChange = (index, field, value) => {
    const newEducation = [...education];
    newEducation[index][field] = value;
    setEducation(newEducation);
  };

  const addEducation = () => {
    setEducation([...education, {
      institution: '',
      degree: '',
      fieldOfStudy: '',
      startDate: '',
      endDate: ''
    }]);
  };

  const removeEducation = (index) => {
    if (education.length > 1) {
      const newEducation = education.filter((_, i) => i !== index);
      setEducation(newEducation);
    }
  };

  const handleExperienceChange = (index, field, value) => {
    const newExperience = [...experience];
    newExperience[index][field] = value;
    setExperience(newExperience);
  };

  const addExperience = () => {
    setExperience([...experience, {
      company: '',
      role: '',
      description: '',
      startDate: '',
      endDate: '',
      isCurrent: false
    }]);
  };

  const removeExperience = (index) => {
    if (experience.length > 1) {
      const newExperience = experience.filter((_, i) => i !== index);
      setExperience(newExperience);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');
    setLoading(true);

    try {
      // 1. Create CV
      const cvResponse = await cvAPI.create(formData);
      const cvId = cvResponse.data.id;

      // 2. Add education entries
      for (const edu of education) {
        if (edu.institution) {
          await cvAPI.addEducation(cvId, edu);
        }
      }

      // 3. Add experience entries
      for (const exp of experience) {
        if (exp.company) {
          await cvAPI.addExperience(cvId, exp);
        }
      }

      // 4. Join directory
      await directoryAPI.joinDirectory();

      setSuccess(true);
      setTimeout(() => {
        navigate('/dashboard');
      }, 2000);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to create CV. Please try again.');
      window.scrollTo({ top: 0, behavior: 'smooth' });
    } finally {
      setLoading(false);
    }
  };

  if (success) {
    return (
      <div style={{ maxWidth: '800px', margin: '30px auto', padding: '20px', textAlign: 'center' }}>
        <div style={{
          backgroundColor: '#d4edda',
          color: '#155724',
          padding: '20px',
          borderRadius: '8px',
          marginBottom: '20px'
        }}>
          <h2>✅ CV Created Successfully!</h2>
          <p>You are now in the directory. Redirecting to dashboard...</p>
        </div>
      </div>
    );
  }

  return (
    <div style={{ maxWidth: '800px', margin: '30px auto', padding: '20px' }}>
      <h1 style={{ marginBottom: '30px' }}>Create Your Professional CV</h1>

      {error && (
        <div style={{ 
          padding: '15px', 
          backgroundColor: '#f8d7da', 
          color: '#721c24', 
          borderRadius: '4px', 
          marginBottom: '20px',
          border: '1px solid #f5c6cb'
        }}>
          <strong>Error:</strong> {error}
        </div>
      )}

      <form onSubmit={handleSubmit}>
        {/* Basic Info */}
        <div style={{ marginBottom: '30px', padding: '20px', backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
          <h2 style={{ marginBottom: '20px' }}>Basic Information</h2>
          
          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
              Professional Headline *
            </label>
            <input
              type="text"
              name="headline"
              value={formData.headline}
              onChange={handleChange}
              placeholder="e.g., Full-Stack Developer | React & Node.js Expert"
              required
              style={{
                width: '100%',
                padding: '10px',
                border: '1px solid #ccc',
                borderRadius: '4px',
                fontSize: '16px'
              }}
            />
          </div>

          <div style={{ marginBottom: '15px' }}>
            <label style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
              Professional Summary *
            </label>
            <textarea
              name="summary"
              value={formData.summary}
              onChange={handleChange}
              placeholder="Brief description of your professional background..."
              rows="4"
              required
              style={{
                width: '100%',
                padding: '10px',
                border: '1px solid #ccc',
                borderRadius: '4px',
                fontSize: '16px',
                resize: 'vertical'
              }}
            />
          </div>
        </div>

        {/* Education */}
        <div style={{ marginBottom: '30px', padding: '20px', backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
          <h2 style={{ marginBottom: '20px' }}>Education</h2>
          
          {education.map((edu, index) => (
            <div key={index} style={{ marginBottom: '20px', paddingBottom: '20px', borderBottom: '1px solid #eee' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '15px' }}>
                <h3 style={{ fontSize: '16px', margin: 0 }}>Education #{index + 1}</h3>
                {education.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeEducation(index)}
                    style={{
                      padding: '5px 15px',
                      backgroundColor: '#dc3545',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontSize: '14px'
                    }}
                  >
                    Remove
                  </button>
                )}
              </div>
              
              <div style={{ marginBottom: '10px' }}>
                <input
                  type="text"
                  placeholder="Institution (e.g., National University of Ireland Maynooth)"
                  value={edu.institution}
                  onChange={(e) => handleEducationChange(index, 'institution', e.target.value)}
                  style={{
                    width: '100%',
                    padding: '10px',
                    border: '1px solid #ccc',
                    borderRadius: '4px'
                  }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', marginBottom: '10px' }}>
                <input
                  type="text"
                  placeholder="Degree (e.g., BSc Computer Science)"
                  value={edu.degree}
                  onChange={(e) => handleEducationChange(index, 'degree', e.target.value)}
                  style={{
                    padding: '10px',
                    border: '1px solid #ccc',
                    borderRadius: '4px'
                  }}
                />
                <input
                  type="text"
                  placeholder="Field of Study"
                  value={edu.fieldOfStudy}
                  onChange={(e) => handleEducationChange(index, 'fieldOfStudy', e.target.value)}
                  style={{
                    padding: '10px',
                    border: '1px solid #ccc',
                    borderRadius: '4px'
                  }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                <div>
                  <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', color: '#666' }}>Start Date</label>
                  <input
                    type="date"
                    value={edu.startDate}
                    onChange={(e) => handleEducationChange(index, 'startDate', e.target.value)}
                    style={{
                      width: '100%',
                      padding: '10px',
                      border: '1px solid #ccc',
                      borderRadius: '4px'
                    }}
                  />
                </div>
                <div>
                  <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', color: '#666' }}>End Date</label>
                  <input
                    type="date"
                    value={edu.endDate}
                    onChange={(e) => handleEducationChange(index, 'endDate', e.target.value)}
                    style={{
                      width: '100%',
                      padding: '10px',
                      border: '1px solid #ccc',
                      borderRadius: '4px'
                    }}
                  />
                </div>
              </div>
            </div>
          ))}

          <button
            type="button"
            onClick={addEducation}
            style={{
              padding: '10px 20px',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            + Add Another Education
          </button>
        </div>

        {/* Experience */}
        <div style={{ marginBottom: '30px', padding: '20px', backgroundColor: 'white', borderRadius: '8px', boxShadow: '0 2px 4px rgba(0,0,0,0.1)' }}>
          <h2 style={{ marginBottom: '20px' }}>Work Experience</h2>
          
          {experience.map((exp, index) => (
            <div key={index} style={{ marginBottom: '20px', paddingBottom: '20px', borderBottom: '1px solid #eee' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '15px' }}>
                <h3 style={{ fontSize: '16px', margin: 0 }}>Experience #{index + 1}</h3>
                {experience.length > 1 && (
                  <button
                    type="button"
                    onClick={() => removeExperience(index)}
                    style={{
                      padding: '5px 15px',
                      backgroundColor: '#dc3545',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontSize: '14px'
                    }}
                  >
                    Remove
                  </button>
                )}
              </div>
              
              <div style={{ marginBottom: '10px' }}>
                <input
                  type="text"
                  placeholder="Company"
                  value={exp.company}
                  onChange={(e) => handleExperienceChange(index, 'company', e.target.value)}
                  style={{
                    width: '100%',
                    padding: '10px',
                    border: '1px solid #ccc',
                    borderRadius: '4px'
                  }}
                />
              </div>

              <div style={{ marginBottom: '10px' }}>
                <input
                  type="text"
                  placeholder="Role/Position"
                  value={exp.role}
                  onChange={(e) => handleExperienceChange(index, 'role', e.target.value)}
                  style={{
                    width: '100%',
                    padding: '10px',
                    border: '1px solid #ccc',
                    borderRadius: '4px'
                  }}
                />
              </div>

              <div style={{ marginBottom: '10px' }}>
                <textarea
                  placeholder="Description of responsibilities and achievements"
                  value={exp.description}
                  onChange={(e) => handleExperienceChange(index, 'description', e.target.value)}
                  rows="3"
                  style={{
                    width: '100%',
                    padding: '10px',
                    border: '1px solid #ccc',
                    borderRadius: '4px',
                    resize: 'vertical'
                  }}
                />
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
                <div>
                  <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', color: '#666' }}>Start Date</label>
                  <input
                    type="date"
                    value={exp.startDate}
                    onChange={(e) => handleExperienceChange(index, 'startDate', e.target.value)}
                    style={{
                      width: '100%',
                      padding: '10px',
                      border: '1px solid #ccc',
                      borderRadius: '4px'
                    }}
                  />
                </div>
                <div>
                  <label style={{ display: 'flex', alignItems: 'center', gap: '10px', marginTop: '25px' }}>
                    <input
                      type="checkbox"
                      checked={exp.isCurrent}
                      onChange={(e) => handleExperienceChange(index, 'isCurrent', e.target.checked)}
                    />
                    Currently working here
                  </label>
                  {!exp.isCurrent && (
                    <>
                      <label style={{ display: 'block', marginBottom: '5px', fontSize: '14px', color: '#666' }}>End Date</label>
                      <input
                        type="date"
                        value={exp.endDate}
                        onChange={(e) => handleExperienceChange(index, 'endDate', e.target.value)}
                        style={{
                          width: '100%',
                          padding: '10px',
                          border: '1px solid #ccc',
                          borderRadius: '4px'
                        }}
                      />
                    </>
                  )}
                </div>
              </div>
            </div>
          ))}

          <button
            type="button"
            onClick={addExperience}
            style={{
              padding: '10px 20px',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer'
            }}
          >
            + Add Another Experience
          </button>
        </div>

        <button
          type="submit"
          disabled={loading}
          style={{
            width: '100%',
            padding: '15px',
            backgroundColor: loading ? '#ccc' : '#28a745',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            fontSize: '18px',
            fontWeight: 'bold',
            cursor: loading ? 'not-allowed' : 'pointer'
          }}
        >
          {loading ? 'Creating CV...' : 'Create CV & Join Directory'}
        </button>
      </form>
    </div>
  );
}

export default CreateCV;