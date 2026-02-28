package com.vericv.platform.controller;

import com.vericv.platform.dto.cv.CreateCVRequest;
import com.vericv.platform.dto.cv.EducationDto;
import com.vericv.platform.dto.cv.ExperienceDto;
import com.vericv.platform.model.CV;
import com.vericv.platform.model.Education;
import com.vericv.platform.model.Experience;
import com.vericv.platform.model.User;
import com.vericv.platform.repository.UserRepository;
import com.vericv.platform.repository.CVRepository;
import com.vericv.platform.service.CVService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cv")
@Tag(name = "CV Management", description = "Endpoints for managing CVs, education, and experience")
public class CVController {

    private final CVService cvService;
    private final UserRepository userRepository;
    private final CVRepository cvRepository;

    public CVController(CVService cvService, UserRepository userRepository, CVRepository cvRepository) {
        this.cvService = cvService;
        this.userRepository = userRepository;
        this.cvRepository = cvRepository;
    }

    // ===== CV Endpoints =====

    @PostMapping
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Create a new CV")
    public ResponseEntity<?> createCV(@Valid @RequestBody CreateCVRequest request,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            CV cv = cvService.createCV(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(cv);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Get my CV")
    public ResponseEntity<?> getMyCV(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            CV cv = cvService.getUserCV(userId);

            List<Education> education = cvService.getEducationByCvId(cv.getId());
            List<Experience> experience = cvService.getExperienceByCvId(cv.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("cv", cv);
            response.put("education", education);
            response.put("experience", experience);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{cvId}")
    @Operation(summary = "Get CV by CV ID (public endpoint)")
    public ResponseEntity<?> getCVById(@PathVariable Long cvId) {
        try {
            CV cv = cvRepository.findById(cvId)
                    .orElseThrow(() -> new RuntimeException("CV not found"));

            if (!cv.getIsPublic()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "This CV is private"));
            }

            List<Education> education = cvService.getEducationByCvId(cv.getId());
            List<Experience> experience = cvService.getExperienceByCvId(cv.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("cv", cv);
            response.put("education", education);
            response.put("experience", experience);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "CV not found"));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get CV by User ID (public endpoint)")
    public ResponseEntity<?> getCVByUserId(@PathVariable Long userId) {
        try {
            CV cv = cvService.getUserCV(userId);

            if (!cv.getIsPublic()) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("error", "This CV is private"));
            }

            List<Education> education = cvService.getEducationByCvId(cv.getId());
            List<Experience> experience = cvService.getExperienceByCvId(cv.getId());

            Map<String, Object> response = new HashMap<>();
            response.put("cv", cv);
            response.put("education", education);
            response.put("experience", experience);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "CV not found for this user"));
        }
    }

    @PutMapping("/{cvId}")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update CV")
    public ResponseEntity<?> updateCV(@PathVariable Long cvId,
            @Valid @RequestBody CreateCVRequest request,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            CV cv = cvService.updateCV(cvId, userId, request);
            return ResponseEntity.ok(cv);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{cvId}")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Delete CV")
    public ResponseEntity<?> deleteCV(@PathVariable Long cvId, Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            cvService.deleteCV(cvId, userId);
            return ResponseEntity.ok(Map.of("message", "CV deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== Education Endpoints =====

    @PostMapping("/{cvId}/education")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Add education")
    public ResponseEntity<?> addEducation(@PathVariable Long cvId,
            @Valid @RequestBody EducationDto dto,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Education education = cvService.addEducation(cvId, userId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(education);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/education/{educationId}")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update education")
    public ResponseEntity<?> updateEducation(@PathVariable Long educationId,
            @Valid @RequestBody EducationDto dto,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Education education = cvService.updateEducation(educationId, userId, dto);
            return ResponseEntity.ok(education);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/education/{educationId}")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Delete education")
    public ResponseEntity<?> deleteEducation(@PathVariable Long educationId,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            cvService.deleteEducation(educationId, userId);
            return ResponseEntity.ok(Map.of("message", "Education deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== Experience Endpoints =====

    @PostMapping("/{cvId}/experience")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Add experience")
    public ResponseEntity<?> addExperience(@PathVariable Long cvId,
            @Valid @RequestBody ExperienceDto dto,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Experience experience = cvService.addExperience(cvId, userId, dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(experience);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/experience/{experienceId}")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update experience")
    public ResponseEntity<?> updateExperience(@PathVariable Long experienceId,
            @Valid @RequestBody ExperienceDto dto,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            Experience experience = cvService.updateExperience(experienceId, userId, dto);
            return ResponseEntity.ok(experience);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/experience/{experienceId}")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Delete experience")
    public ResponseEntity<?> deleteExperience(@PathVariable Long experienceId,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            cvService.deleteExperience(experienceId, userId);
            return ResponseEntity.ok(Map.of("message", "Experience deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== Helper Method =====

    private Long getUserIdFromAuth(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}