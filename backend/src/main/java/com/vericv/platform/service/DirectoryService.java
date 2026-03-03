package com.vericv.platform.service;

import com.vericv.platform.model.CV;
import com.vericv.platform.model.DirectoryEntry;
import com.vericv.platform.model.Education;
import com.vericv.platform.model.Experience;
import com.vericv.platform.model.User;
import com.vericv.platform.repository.CVRepository;
import com.vericv.platform.repository.DirectoryEntryRepository;
import com.vericv.platform.repository.EducationRepository;
import com.vericv.platform.repository.ExperienceRepository;
import com.vericv.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DirectoryService {

    private final DirectoryEntryRepository directoryEntryRepository;
    private final UserRepository userRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final CVRepository cvRepository;

    public DirectoryService(DirectoryEntryRepository directoryEntryRepository,
            UserRepository userRepository,
            EducationRepository educationRepository,
            ExperienceRepository experienceRepository,
            CVRepository cvRepository) {
        this.directoryEntryRepository = directoryEntryRepository;
        this.userRepository = userRepository;
        this.educationRepository = educationRepository;
        this.experienceRepository = experienceRepository;
        this.cvRepository = cvRepository;
    }

    // ===== Directory Entry Management =====

    @Transactional
    public DirectoryEntry addToDirectory(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (directoryEntryRepository.existsByUserId(userId)) {
            throw new RuntimeException("User already in directory");
        }

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

        int verifiedCount = 0;

        if (user.getIsVerified() != null && user.getIsVerified()) {
            verifiedCount++;
        }

        // Find the user's CV to get the correct cvId
        Optional<CV> cvOpt = cvRepository.findByUserId(userId);
        if (cvOpt.isPresent()) {
            Long cvId = cvOpt.get().getId();

            List<Education> educationList = educationRepository.findByCvIdAndIsVerifiedTrue(cvId);
            if (!educationList.isEmpty()) {
                verifiedCount++;
            }

            List<Experience> experienceList = experienceRepository.findByCvIdAndIsVerifiedTrue(cvId);
            if (!experienceList.isEmpty()) {
                verifiedCount++;
            }
        }

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

        if (user != null) {
            searchText.append(user.getFullName()).append(" ");
            searchText.append(user.getEmail()).append(" ");
        }

        directoryEntryRepository.findByUserId(userId).ifPresent(entry -> {
            if (entry.getHeadline() != null) {
                searchText.append(entry.getHeadline()).append(" ");
            }
            if (entry.getLocation() != null) {
                searchText.append(entry.getLocation()).append(" ");
            }
        });

        // Find the user's CV to get the correct cvId
        cvRepository.findByUserId(userId).ifPresent(cv -> {
            Long cvId = cv.getId();

            List<Education> educationList = educationRepository.findByCvId(cvId);
            for (Education edu : educationList) {
                searchText.append(edu.getInstitution()).append(" ");
                searchText.append(edu.getDegree()).append(" ");
                if (edu.getFieldOfStudy() != null) {
                    searchText.append(edu.getFieldOfStudy()).append(" ");
                }
            }

            List<Experience> experienceList = experienceRepository.findByCvId(cvId);
            for (Experience exp : experienceList) {
                searchText.append(exp.getCompany()).append(" ");
                searchText.append(exp.getRole()).append(" ");
            }
        });

        return searchText.toString().trim();
    }

    public boolean isUserInDirectory(Long userId) {
        return directoryEntryRepository.existsByUserId(userId);
    }

    public List<DirectoryEntry> getAllVisibleEntries() {
        return directoryEntryRepository.findByIsVisibleTrue();
    }
}