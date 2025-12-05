package com.vericv.platform.repository;

import com.vericv.platform.model.DirectoryEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DirectoryEntryRepository extends JpaRepository<DirectoryEntry, Long> {

    // Find directory entry by user ID
    Optional<DirectoryEntry> findByUserId(Long userId);

    // Find all visible entries (for public directory)
    List<DirectoryEntry> findByIsVisibleTrue();

    // Search by name (case-insensitive)
    List<DirectoryEntry> findByIsVisibleTrueAndFullNameContainingIgnoreCase(String name);

    // Search by headline
    List<DirectoryEntry> findByIsVisibleTrueAndHeadlineContainingIgnoreCase(String headline);

    // Find by verification badge level
    List<DirectoryEntry> findByIsVisibleTrueAndVerificationBadge(DirectoryEntry.VerificationBadge badge);

    // Simple search across multiple fields
    @Query("SELECT d FROM DirectoryEntry d WHERE d.isVisible = true AND " +
            "(LOWER(d.fullName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.headline) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(d.searchableText) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<DirectoryEntry> searchDirectory(@Param("keyword") String keyword);

    // Check if user already has a directory entry
    boolean existsByUserId(Long userId);
}