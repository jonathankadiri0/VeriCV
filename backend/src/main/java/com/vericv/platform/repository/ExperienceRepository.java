package com.vericv.platform.repository;

import com.vericv.platform.model.Experience;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperienceRepository extends JpaRepository<Experience, Long> {

    // Find all experience entries for a CV
    List<Experience> findByCvId(Long cvId);

    // Find all verified experience entries for a CV
    List<Experience> findByCvIdAndIsVerifiedTrue(Long cvId);

    // Find current positions for a CV
    List<Experience> findByCvIdAndIsCurrentTrue(Long cvId);
}