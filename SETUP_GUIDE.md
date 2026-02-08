# VeriCV - Digitally Verifiable CV Platform 🎓

Secure platform for creating digitally verifiable, tamper-proof CVs with automated credential verification.

## 🎯 Project Overview

VeriCV solves the critical problem of CV fraud by enabling:
- **Digital Verification**: Real-time credential verification with institutions and companies
- **Tamper-Proof**: Cryptographic signatures using SHA256withRSA prevent CV falsification
- **Automated Validation**: Streamlined verification workflow reducing manual checks
- **Permission-Based Access**: Control who can view and verify your CV

**Problem:** 1 in 5 people lie on their CVs, costing companies £132,000+ per bad hire  
**Solution:** Automated, cryptographically-signed credential verification

---

## 🛠️ Tech Stack

### Backend
- **Framework**: Spring Boot 3.2 + Java 17
- **Database**: PostgreSQL 15+
- **Security**: Spring Security + JWT Authentication
- **Cryptography**: Bouncy Castle (SHA256withRSA)
- **API Docs**: Swagger/OpenAPI
- **Build Tool**: Maven 3.9+

### Frontend
- **Framework**: React 18
- **Styling**: Tailwind CSS
- **Build Tool**: Vite

### Testing & Deployment
- **Testing**: JUnit 5 + Mockito
- **Deployment**: Heroku
- **Version Control**: Git + GitHub

---

## 🚀 Quick Start Guide

### Prerequisites

Make sure you have installed:
- **Java 17+** ([Download](https://adoptium.net/))
- **Maven 3.9+** ([Download](https://maven.apache.org/download.cgi))
- **Node.js 18+** ([Download](https://nodejs.org/))
- **Docker** (Optional but recommended) ([Download](https://www.docker.com/))
- **PostgreSQL 15+** (if not using Docker)

### Step 1: Clone the Repository

```bash
git clone https://github.com/jonathankadiri0/VeriCV.git
cd VeriCV
```

### Step 2: Start PostgreSQL Database

**Option A: Using Docker (Recommended)**
```bash
docker-compose up -d
```

**Option B: Local PostgreSQL**
```sql
CREATE DATABASE vericv_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE vericv_db TO postgres;
```

### Step 3: Configure Environment Variables

Create `backend/src/main/resources/application-local.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/vericv_db
    username: postgres
    password: postgres

jwt:
  secret: your-super-secret-jwt-key-change-this-in-production-minimum-256-bits
  
spring:
  mail:
    username: your-email@gmail.com
    password: your-app-specific-password
```

### Step 4: Build and Run Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

The backend will start on `http://localhost:8080`

**API Documentation**: http://localhost:8080/swagger-ui.html

### Step 5: Setup Frontend (Coming Soon)

```bash
cd frontend
npm install
npm run dev
```

Frontend will run on `http://localhost:5173`

---

## 📁 Project Structure

```
VeriCV/
├── backend/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/vericv/
│   │   │   │   ├── VeriCVApplication.java
│   │   │   │   ├── config/
│   │   │   │   │   └── SecurityConfig.java
│   │   │   │   ├── model/
│   │   │   │   │   ├── User.java
│   │   │   │   │   ├── CV.java
│   │   │   │   │   ├── Education.java
│   │   │   │   │   ├── Experience.java
│   │   │   │   │   ├── VerificationRequest.java
│   │   │   │   │   ├── Institution.java
│   │   │   │   │   ├── Company.java
│   │   │   │   │   └── CVAccessPermission.java
│   │   │   │   ├── repository/
│   │   │   │   │   ├── UserRepository.java
│   │   │   │   │   └── CVRepository.java
│   │   │   │   ├── service/
│   │   │   │   ├── controller/
│   │   │   │   ├── dto/
│   │   │   │   ├── security/
│   │   │   │   │   ├── JwtTokenProvider.java
│   │   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   │   └── CustomUserDetailsService.java
│   │   │   │   └── util/
│   │   │   └── resources/
│   │   │       └── application.yml
│   │   └── test/
│   └── pom.xml
├── frontend/
│   └── (React app - coming soon)
├── docker-compose.yml
└── README.md
```

---

## 🔐 Core Features

### Phase 1: Authentication & User Management ✅ (Current)
- [x] User registration with email verification
- [x] JWT-based authentication
- [x] Password reset functionality
- [x] User profile management

### Phase 2: CV Management (In Progress)
- [ ] Create and edit CVs
- [ ] Add education entries
- [ ] Add work experience
- [ ] Upload supporting documents

### Phase 3: Digital Signatures & Verification
- [ ] Generate RSA key pairs for users
- [ ] Sign CV components with SHA256withRSA
- [ ] Verify digital signatures
- [ ] Tamper detection

### Phase 4: Verification Workflow
- [ ] Request verification from institutions
- [ ] Email verification links
- [ ] Public verification endpoint
- [ ] Verification status tracking

### Phase 5: Access Control
- [ ] Grant CV access to employers
- [ ] Token-based temporary access
- [ ] Access analytics

---

## 🗄️ Database Schema

Key entities:
- **users**: User accounts with authentication
- **cvs**: CV metadata and signatures
- **cv_education**: Educational qualifications
- **cv_experience**: Work experience
- **verification_requests**: Verification tracking
- **institutions**: Educational institutions
- **companies**: Employer companies
- **cv_access_permissions**: Access control

---

## 🧪 Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Run with coverage
mvn test jacoco:report
```

---

## 📡 API Endpoints (Planned)

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/verify-email` - Verify email
- `POST /api/auth/forgot-password` - Request password reset

### CV Management
- `GET /api/cv` - Get user's CVs
- `POST /api/cv` - Create new CV
- `PUT /api/cv/{id}` - Update CV
- `DELETE /api/cv/{id}` - Delete CV

### Verification
- `POST /api/verification/request` - Request verification
- `GET /api/verification/public/{token}` - Public verification endpoint
- `POST /api/verification/verify` - Verify credential

---

## 🚢 Deployment (Heroku)

### Prerequisites
- Heroku CLI installed
- Heroku account created

### Deploy Backend

```bash
# Login to Heroku
heroku login

# Create app
heroku create vericv-backend

# Add PostgreSQL addon
heroku addons:create heroku-postgresql:essential-0

# Set environment variables
heroku config:set JWT_SECRET=your-secret-key

# Deploy
git push heroku main
```

---

## 🤝 Development Workflow

### Branch Strategy
- `main` - Production-ready code
- `develop` - Development branch
- `feature/*` - Feature branches
- `bugfix/*` - Bug fix branches

### Commit Convention
```
feat: Add user registration endpoint
fix: Fix JWT token expiration
docs: Update README with setup instructions
test: Add tests for CV service
```

---

## 📊 Development Roadmap

### Week 1-2: Foundation ✅
- [x] Project setup
- [x] Database schema
- [x] Authentication system

### Week 3-4: CV Management
- [ ] CRUD operations for CVs
- [ ] Education/Experience entries
- [ ] File upload support

### Week 5-6: Digital Signatures
- [ ] Bouncy Castle integration
- [ ] Key generation
- [ ] Signing/verification

### Week 7-8: Verification System
- [ ] Verification workflow
- [ ] Email notifications
- [ ] Public verification

### Week 9-10: Frontend
- [ ] React setup
- [ ] Authentication UI
- [ ] CV builder interface

### Week 11-12: Testing & Deployment
- [ ] Unit tests
- [ ] Integration tests
- [ ] Heroku deployment

---

## 🐛 Common Issues & Solutions

### Issue: Database connection failed
**Solution**: Make sure PostgreSQL is running and credentials match in `application.yml`

### Issue: JWT token invalid
**Solution**: Ensure JWT secret is properly set and tokens haven't expired

### Issue: Maven build fails
**Solution**: Run `mvn clean install -U` to force update dependencies

---

## 📚 Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Security JWT Tutorial](https://www.bezkoder.com/spring-boot-jwt-authentication/)
- [Bouncy Castle Documentation](https://www.bouncycastle.org/documentation.html)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

## 👨‍💻 Author

**Jonathan Kadiri**  
Final Year Project - BSc Computer Science

---

## 📄 License

This project is part of an academic final year project.

---

## ⚡ Next Steps

1. **Test the backend**: Start the application and visit Swagger UI
2. **Create first API endpoint**: Implement user registration
3. **Build authentication flow**: Complete login/register functionality
4. **Design frontend mockups**: Plan the user interface
5. **Implement CV CRUD**: Start building CV management features

Good luck with your project! 🚀
