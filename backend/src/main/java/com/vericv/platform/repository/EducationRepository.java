package com.vericv.platform.repository;

import com.vericv.platform.model.Education;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EducationRepository extends JpaRepository<Education, Long> {

    // Find all education entries for a CV
    List<Education> findByCvId(Long cvId);

    // Find all verified education entries for a CV
    List<Education> findByCvIdAndIsVerifiedTrue(Long cvId);
}
