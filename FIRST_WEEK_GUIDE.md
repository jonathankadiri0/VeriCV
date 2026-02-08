# 🎯 YOUR FIRST 7 DAYS - Implementation Guide

This guide breaks down exactly what to do in your first week to get VeriCV up and running.

---

## 📅 DAY 1: Project Setup & Database (4-5 hours)

### Morning Session (2-3 hours)

**1. Upload files to your GitHub repo**
```bash
# In your local VeriCV directory
git add .
git commit -m "feat: Initial project setup with Spring Boot structure"
git push origin main
```

**2. Start PostgreSQL**
```bash
# Using Docker (easier)
docker-compose up -d

# Verify it's running
docker ps
```

**3. Test Backend Startup**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

**Expected output:**
```
🚀 VeriCV Application Started Successfully!
📚 Swagger UI: http://localhost:8080/swagger-ui.html
📡 API Docs: http://localhost:8080/api-docs
```

### Afternoon Session (2 hours)

**4. Verify database connection**
- Open pgAdmin or any PostgreSQL client
- Connect to `localhost:5432`
- Check that `vericv_db` database exists
- Run the app again and check the logs - you should see Hibernate creating tables

**5. Test Swagger UI**
- Open browser: http://localhost:8080/swagger-ui.html
- You should see the API documentation interface (even though no endpoints yet)

**✅ Day 1 Success Criteria:**
- Backend starts without errors
- Database connection works
- Swagger UI loads

---

## 📅 DAY 2: Authentication Service (6-8 hours)

### Task 1: Create DTOs (1 hour)

Create `backend/src/main/java/com/vericv/dto/auth/`:

**RegisterRequest.java**
```java
package com.vericv.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

**LoginRequest.java**
```java
package com.vericv.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
}
```

**AuthResponse.java**
```java
package com.vericv.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String fullName;
    private Set<String> roles;
}
```

### Task 2: Create AuthService (2-3 hours)

Create `backend/src/main/java/com/vericv/service/AuthService.java`:

```java
package com.vericv.service;

import com.vericv.dto.auth.AuthResponse;
import com.vericv.dto.auth.LoginRequest;
import com.vericv.dto.auth.RegisterRequest;
import com.vericv.model.User;
import com.vericv.repository.UserRepository;
import com.vericv.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if user exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        // Create new user
        Set<String> roles = new HashSet<>();
        roles.add("ROLE_USER");

        User user = User.builder()
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .isVerified(true) // For now, skip email verification
                .isActive(true)
                .roles(roles)
                .verificationToken(UUID.randomUUID().toString())
                .build();

        userRepository.save(user);

        // Generate token
        String token = tokenProvider.generateTokenFromEmail(user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        // Authenticate user
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        // Generate token
        String token = tokenProvider.generateToken(authentication);

        // Get user details
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        return AuthResponse.builder()
                .token(token)
                .email(user.getEmail())
                .fullName(user.getFullName())
                .roles(user.getRoles())
                .build();
    }
}
```

### Task 3: Create AuthController (1-2 hours)

Create `backend/src/main/java/com/vericv/controller/AuthController.java`:

```java
package com.vericv.controller;

import com.vericv.dto.auth.AuthResponse;
import com.vericv.dto.auth.LoginRequest;
import com.vericv.dto.auth.RegisterRequest;
import com.vericv.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    @Operation(summary = "Login user")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    @Operation(summary = "Test endpoint to verify auth is working")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Authentication module is working!");
    }
}
```

### Task 4: Test with Postman/Curl (1-2 hours)

**Test 1: Register**
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "John Doe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

**Test 2: Login**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john@example.com",
    "password": "password123"
  }'
```

**✅ Day 2 Success Criteria:**
- Can register new users
- Can login successfully
- Receive JWT token in response
- User data saved in database

---

## 📅 DAY 3: CV CRUD Operations (6-8 hours)

### Task 1: Create CV DTOs (1 hour)

**CreateCVRequest.java**
```java
package com.vericv.dto.cv;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateCVRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    private String summary;
    private String phoneNumber;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
}
```

**CVResponse.java**
```java
package com.vericv.dto.cv;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CVResponse {
    private UUID id;
    private String title;
    private String summary;
    private String phoneNumber;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private Boolean isPublished;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Task 2: Create CVService (2-3 hours)

```java
package com.vericv.service;

import com.vericv.dto.cv.CreateCVRequest;
import com.vericv.dto.cv.CVResponse;
import com.vericv.model.CV;
import com.vericv.model.User;
import com.vericv.repository.CVRepository;
import com.vericv.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CVService {

    private final CVRepository cvRepository;
    private final UserRepository userRepository;

    @Transactional
    public CVResponse createCV(CreateCVRequest request) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CV cv = CV.builder()
                .user(user)
                .title(request.getTitle())
                .summary(request.getSummary())
                .phoneNumber(request.getPhoneNumber())
                .linkedinUrl(request.getLinkedinUrl())
                .githubUrl(request.getGithubUrl())
                .portfolioUrl(request.getPortfolioUrl())
                .isPublished(false)
                .build();

        CV savedCV = cvRepository.save(cv);
        return mapToResponse(savedCV);
    }

    public List<CVResponse> getUserCVs() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return cvRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public CVResponse getCVById(UUID id) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CV cv = cvRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new RuntimeException("CV not found"));

        return mapToResponse(cv);
    }

    private CVResponse mapToResponse(CV cv) {
        return CVResponse.builder()
                .id(cv.getId())
                .title(cv.getTitle())
                .summary(cv.getSummary())
                .phoneNumber(cv.getPhoneNumber())
                .linkedinUrl(cv.getLinkedinUrl())
                .githubUrl(cv.getGithubUrl())
                .portfolioUrl(cv.getPortfolioUrl())
                .isPublished(cv.getIsPublished())
                .createdAt(cv.getCreatedAt())
                .updatedAt(cv.getUpdatedAt())
                .build();
    }
}
```

### Task 3: Create CVController (1 hour)

```java
package com.vericv.controller;

import com.vericv.dto.cv.CreateCVRequest;
import com.vericv.dto.cv.CVResponse;
import com.vericv.service.CVService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/cv")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "CV Management", description = "CV CRUD operations")
public class CVController {

    private final CVService cvService;

    @PostMapping
    @Operation(summary = "Create a new CV")
    public ResponseEntity<CVResponse> createCV(@Valid @RequestBody CreateCVRequest request) {
        CVResponse response = cvService.createCV(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    @Operation(summary = "Get all CVs for current user")
    public ResponseEntity<List<CVResponse>> getUserCVs() {
        List<CVResponse> cvs = cvService.getUserCVs();
        return ResponseEntity.ok(cvs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get CV by ID")
    public ResponseEntity<CVResponse> getCVById(@PathVariable UUID id) {
        CVResponse cv = cvService.getCVById(id);
        return ResponseEntity.ok(cv);
    }
}
```

### Task 4: Test CV endpoints (2 hours)

**Test: Create CV**
```bash
# First, login to get token
TOKEN="your-jwt-token-from-login"

curl -X POST http://localhost:8080/api/cv \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Software Engineer CV",
    "summary": "Experienced software engineer...",
    "phoneNumber": "+1234567890",
    "linkedinUrl": "linkedin.com/in/johndoe"
  }'
```

**✅ Day 3 Success Criteria:**
- Can create CVs (requires authentication)
- Can retrieve user's CVs
- Data persists in database

---

## 🎉 After 3 Days, You'll Have:

1. ✅ **Working Backend**: Spring Boot application running
2. ✅ **Database**: PostgreSQL connected and creating tables
3. ✅ **Authentication**: Register and login working with JWT
4. ✅ **CV Management**: Basic CRUD for CVs
5. ✅ **API Documentation**: Swagger UI with all endpoints

---

## 📝 Next Steps (Days 4-7)

**Day 4**: Add Education and Experience entities  
**Day 5**: Implement Digital Signature utility  
**Day 6**: Create Verification Request system  
**Day 7**: Start Frontend with React

---

## 💡 Pro Tips

1. **Commit frequently**: After each working feature
2. **Test immediately**: Don't write too much without testing
3. **Use Swagger**: It's your best friend for API testing
4. **Check logs**: Spring Boot logs are very helpful for debugging
5. **Start simple**: Get basic features working before adding complexity

---

## 🆘 Getting Help

If you're stuck:
1. Check the error logs in the terminal
2. Google the error message
3. Check Stack Overflow
4. Review Spring Boot documentation
5. Ask for help with specific error messages

---

## 🎯 Success Metrics

By end of Day 3, you should be able to:
- [ ] Start the backend without errors
- [ ] Register a new user via API
- [ ] Login and receive JWT token
- [ ] Create a CV using the token
- [ ] View your CVs via API
- [ ] See all data in the database

---

Good luck! You've got this! 🚀
