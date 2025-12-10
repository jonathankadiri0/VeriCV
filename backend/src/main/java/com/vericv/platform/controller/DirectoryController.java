package com.vericv.platform.controller;

import com.vericv.platform.model.DirectoryEntry;
import com.vericv.platform.model.User;
import com.vericv.platform.repository.UserRepository;
import com.vericv.platform.service.DirectoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/directory")
@Tag(name = "Directory", description = "Phone book directory for verified professionals")
public class DirectoryController {

    private final DirectoryService directoryService;
    private final UserRepository userRepository;

    public DirectoryController(DirectoryService directoryService, UserRepository userRepository) {
        this.directoryService = directoryService;
        this.userRepository = userRepository;
    }

    // ===== PUBLIC Endpoints (No Auth Required) =====

    @GetMapping("/search")
    @Operation(summary = "Search directory", description = "Search for professionals in the directory (PUBLIC)")
    public ResponseEntity<?> searchDirectory(@RequestParam(required = false) String q) {
        try {
            List<DirectoryEntry> results = directoryService.searchDirectory(q);
            return ResponseEntity.ok(Map.of(
                    "results", results,
                    "count", results.size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/profile/{userId}")
    @Operation(summary = "View public profile", description = "View a professional's public profile (PUBLIC)")
    public ResponseEntity<?> getPublicProfile(@PathVariable Long userId) {
        try {
            DirectoryEntry profile = directoryService.getPublicProfile(userId);
            return ResponseEntity.ok(profile);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/filter/badge/{badge}")
    @Operation(summary = "Filter by verification badge", description = "Get all profiles with specific badge level (PUBLIC)")
    public ResponseEntity<?> filterByBadge(@PathVariable String badge) {
        try {
            DirectoryEntry.VerificationBadge badgeLevel = DirectoryEntry.VerificationBadge.valueOf(badge.toUpperCase());
            List<DirectoryEntry> results = directoryService.getByVerificationBadge(badgeLevel);
            return ResponseEntity.ok(Map.of(
                    "results", results,
                    "badge", badge,
                    "count", results.size()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Invalid badge level. Use: NONE, BRONZE, SILVER, GOLD, or PLATINUM"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ===== PROTECTED Endpoints (Auth Required) =====

    @PostMapping("/me/join")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Join directory", description = "Add yourself to the public directory")
    public ResponseEntity<?> joinDirectory(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            DirectoryEntry entry = directoryService.addToDirectory(userId);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Successfully joined directory",
                    "entry", entry));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/me/leave")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Leave directory", description = "Remove yourself from the public directory")
    public ResponseEntity<?> leaveDirectory(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            directoryService.removeFromDirectory(userId);
            return ResponseEntity.ok(Map.of("message", "Successfully left directory"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Get my directory entry", description = "Get your own directory entry")
    public ResponseEntity<?> getMyDirectoryEntry(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            DirectoryEntry entry = directoryService.getUserDirectoryEntry(userId);

            if (entry == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("error", "You are not in the directory. Use POST /api/directory/me/join to join"));
            }

            return ResponseEntity.ok(entry);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update directory entry", description = "Update your headline and location")
    public ResponseEntity<?> updateMyDirectoryEntry(
            @RequestParam(required = false) String headline,
            @RequestParam(required = false) String location,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            DirectoryEntry entry = directoryService.updateHeadlineAndLocation(userId, headline, location);
            return ResponseEntity.ok(Map.of(
                    "message", "Directory entry updated",
                    "entry", entry));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/me/visibility")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Update visibility", description = "Show or hide your profile in directory")
    public ResponseEntity<?> updateVisibility(
            @RequestParam Boolean visible,
            Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            DirectoryEntry entry = directoryService.updateVisibility(userId, visible);
            return ResponseEntity.ok(Map.of(
                    "message", visible ? "Profile is now visible" : "Profile is now hidden",
                    "entry", entry));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/me/refresh")
    @SecurityRequirement(name = "bearer-jwt")
    @Operation(summary = "Refresh directory entry", description = "Update verification badge and searchable text")
    public ResponseEntity<?> refreshDirectoryEntry(Authentication authentication) {
        try {
            Long userId = getUserIdFromAuth(authentication);
            DirectoryEntry entry = directoryService.updateDirectoryEntry(userId);
            return ResponseEntity.ok(Map.of(
                    "message", "Directory entry refreshed",
                    "entry", entry));
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