package com.vericv.platform.service;

import com.vericv.platform.model.DirectoryEntry;
import com.vericv.platform.model.Education;
import com.vericv.platform.model.Experience;
import com.vericv.platform.model.User;
import com.vericv.platform.repository.DirectoryEntryRepository;
import com.vericv.platform.repository.EducationRepository;
import com.vericv.platform.repository.ExperienceRepository;
import com.vericv.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DirectoryService {

    private final DirectoryEntryRepository directoryEntryRepository;
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;

    public DirectoryService(DirectoryEntryRepository directoryEntryRepository,
            UserRepository userRepository,
            EducationRepository educationRepository,
            ExperienceRepository experienceRepository) {
        this.directoryEntryRepository = directoryEntryRepository;
        this.userRepository = userRepository;
        this.educationRepository = educationRepository;
        this.experienceRepository = experienceRepository;
    }

    // ===== Directory Entry Management =====

    @Transactional
    public DirectoryEntry addToDirectory(Long userId) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if already in directory
        if (directoryEntryRepository.existsByUserId(userId)) {
            throw new RuntimeException("User already in directory");
        }

        // Create directory entry
        DirectoryEntry entry = new DirectoryEntry();
        entry.setUserId(userId);
        entry.setFullName(user.getFullName());
        entry.setIsVisible(true);
        entry.setVerificationBadge(calculateVerificationBadge(userId));
        entry.setSearchableText(buildSearchableText(userId, user));

        return directoryEntryRepository.save(entry);
    }

    @Transactional
    public DirectoryEntry updateDirectoryEntry(Long userId) {
        DirectoryEntry entry = directoryEntryRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not in directory"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update entry
        entry.setFullName(user.getFullName());
        entry.setVerificationBadge(calculateVerificationBadge(userId));
        entry.setSearchableText(buildSearchableText(userId, user));
        entry.setLastActive(LocalDateTime.now());

        return directoryEntryRepository.save(entry);
    }

    @Transactional
    public void removeFromDirectory(Long userId) {
        DirectoryEntry entry = directoryEntryRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not in directory"));

        directoryEntryRepository.delete(entry);
    }

    @Transactional
    public DirectoryEntry updateVisibility(Long userId, Boolean isVisible) {
        DirectoryEntry entry = directoryEntryRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not in directory"));

        entry.setIsVisible(isVisible);
        return directoryEntryRepository.save(entry);
    }

    @Transactional
    public DirectoryEntry updateHeadlineAndLocation(Long userId, String headline, String location) {
        DirectoryEntry entry = directoryEntryRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not in directory"));

        if (headline != null) {
            entry.setHeadline(headline);
        }
        if (location != null) {
            entry.setLocation(location);
        }

        entry.setSearchableText(buildSearchableText(userId, null));
        return directoryEntryRepository.save(entry);
    }

    // ===== Search & Discovery =====

    public List<DirectoryEntry> searchDirectory(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return directoryEntryRepository.findByIsVisibleTrue();
        }
        return directoryEntryRepository.searchDirectory(keyword.trim());
    }

    public DirectoryEntry getPublicProfile(Long userId) {
        DirectoryEntry entry = directoryEntryRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found in directory"));

        // Increment profile views
        incrementProfileViews(userId);

        return entry;
    }

    public DirectoryEntry getUserDirectoryEntry(Long userId) {
        return directoryEntryRepository.findByUserId(userId)
                .orElse(null);
    }

    public List<DirectoryEntry> getByVerificationBadge(DirectoryEntry.VerificationBadge badge) {
        return directoryEntryRepository.findByIsVisibleTrueAndVerificationBadge(badge);
    }

    @Transactional
    public void incrementProfileViews(Long userId) {
        directoryEntryRepository.findByUserId(userId).ifPresent(entry -> {
            entry.setProfileViews(entry.getProfileViews() + 1);
            directoryEntryRepository.save(entry);
        });
    }

    // ===== Verification Badge Calculation =====

    public DirectoryEntry.VerificationBadge calculateVerificationBadge(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return DirectoryEntry.VerificationBadge.NONE;
        }

        // Count verified credentials
        int verifiedCount = 0;

        // Check if email is verified
        if (user.getIsVerified() != null && user.getIsVerified()) {
            verifiedCount++;
        }

        // Count verified education entries
        List<Education> educationList = educationRepository.findByCvIdAndIsVerifiedTrue(userId);
        if (!educationList.isEmpty()) {
            verifiedCount++;
        }

        // Count verified experience entries
        List<Experience> experienceList = experienceRepository.findByCvIdAndIsVerifiedTrue(userId);
        if (!experienceList.isEmpty()) {
            verifiedCount++;
        }

        // Determine badge level
        if (verifiedCount == 0) {
            return DirectoryEntry.VerificationBadge.NONE;
        } else if (verifiedCount == 1) {
            return DirectoryEntry.VerificationBadge.BRONZE;
        } else if (verifiedCount == 2) {
            return DirectoryEntry.VerificationBadge.SILVER;
        } else if (verifiedCount == 3) {
            return DirectoryEntry.VerificationBadge.GOLD;
        } else {
            return DirectoryEntry.VerificationBadge.PLATINUM;
        }
    }

    // ===== Helper Methods =====

    private String buildSearchableText(Long userId, User user) {
        StringBuilder searchText = new StringBuilder();

        // Add user name
        if (user != null) {
            searchText.append(user.getFullName()).append(" ");
            searchText.append(user.getEmail()).append(" ");
        }

        // Get directory entry for headline and location
        directoryEntryRepository.findByUserId(userId).ifPresent(entry -> {
            if (entry.getHeadline() != null) {
                searchText.append(entry.getHeadline()).append(" ");
            }
            if (entry.getLocation() != null) {
                searchText.append(entry.getLocation()).append(" ");
            }
        });

        // Add education institutions and degrees
        List<Education> educationList = educationRepository.findByCvId(userId);
        for (Education edu : educationList) {
            searchText.append(edu.getInstitution()).append(" ");
            searchText.append(edu.getDegree()).append(" ");
            if (edu.getFieldOfStudy() != null) {
                searchText.append(edu.getFieldOfStudy()).append(" ");
            }
        }

        // Add experience companies and roles
        List<Experience> experienceList = experienceRepository.findByCvId(userId);
        for (Experience exp : experienceList) {
            searchText.append(exp.getCompany()).append(" ");
            searchText.append(exp.getRole()).append(" ");
        }

        return searchText.toString().trim();
    }

    public boolean isUserInDirectory(Long userId) {
        return directoryEntryRepository.existsByUserId(userId);
    }

    public List<DirectoryEntry> getAllVisibleEntries() {
        return directoryEntryRepository.findByIsVisibleTrue();
    }
}