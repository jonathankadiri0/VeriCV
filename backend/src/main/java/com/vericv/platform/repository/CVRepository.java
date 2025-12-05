package com.vericv.platform.repository;

import com.vericv.platform.model.CV;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CVRepository extends JpaRepository<CV, Long> {

    // Find CV by user ID
    Optional<CV> findByUserId(Long userId);

    // Find all public CVs (for directory)
    List<CV> findByIsPublicTrue();

    // Check if user already has a CV
    boolean existsByUserId(Long userId);
}