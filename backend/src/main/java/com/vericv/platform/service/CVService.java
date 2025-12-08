package com.vericv.platform.service;

import com.vericv.platform.dto.cv.CreateCVRequest;
import com.vericv.platform.dto.cv.EducationDto;
import com.vericv.platform.dto.cv.ExperienceDto;
import com.vericv.platform.model.CV;
import com.vericv.platform.model.Education;
import com.vericv.platform.model.Experience;
import com.vericv.platform.model.User;
import com.vericv.platform.repository.CVRepository;
import com.vericv.platform.repository.EducationRepository;
import com.vericv.platform.repository.ExperienceRepository;
import com.vericv.platform.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CVService {

    private final CVRepository cvRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    public CVService(CVRepository cvRepository,
            EducationRepository educationRepository,
            ExperienceRepository experienceRepository,
            UserRepository userRepository) {
        this.cvRepository = cvRepository;
        this.educationRepository = educationRepository;
        this.experienceRepository = experienceRepository;
        this.userRepository = userRepository;
    }

    // ===== CV CRUD Operations =====

    @Transactional
    public CV createCV(Long userId, CreateCVRequest request) {
        // Check if user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        // Check if user already has a CV
        if (cvRepository.existsByUserId(userId)) {
            throw new RuntimeException("User already has a CV");
        }

        // Create new CV
        CV cv = new CV();
        cv.setUserId(userId);
        cv.setHeadline(request.getHeadline());
        cv.setSummary(request.getSummary());
        cv.setIsPublic(request.getIsPublic());

        return cvRepository.save(cv);
    }

    public CV getUserCV(Long userId) {
        return cvRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("CV not found for user: " + userId));
    }

    public CV getCVById(Long cvId) {
        return cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV not found with id: " + cvId));
    }

    @Transactional
    public CV updateCV(Long cvId, Long userId, CreateCVRequest request) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV not found with id: " + cvId));

        // Check authorization - user must own the CV
        if (!cv.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You don't own this CV");
        }

        // Update CV fields
        cv.setHeadline(request.getHeadline());
        cv.setSummary(request.getSummary());
        cv.setIsPublic(request.getIsPublic());

        return cvRepository.save(cv);
    }

    @Transactional
    public void deleteCV(Long cvId, Long userId) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV not found with id: " + cvId));

        // Check authorization
        if (!cv.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You don't own this CV");
        }

        // Delete associated education and experience first
        educationRepository.deleteAll(educationRepository.findByCvId(cvId));
        experienceRepository.deleteAll(experienceRepository.findByCvId(cvId));

        cvRepository.delete(cv);
    }

    // ===== Education Operations =====

    @Transactional
    public Education addEducation(Long cvId, Long userId, EducationDto dto) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV not found with id: " + cvId));

        // Check authorization
        if (!cv.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You don't own this CV");
        }

        Education education = new Education();
        education.setCvId(cvId);
        education.setInstitution(dto.getInstitution());
        education.setDegree(dto.getDegree());
        education.setFieldOfStudy(dto.getFieldOfStudy());
        education.setStartDate(dto.getStartDate());
        education.setEndDate(dto.getEndDate());

        return educationRepository.save(education);
    }

    public List<Education> getEducationByCvId(Long cvId) {
        return educationRepository.findByCvId(cvId);
    }

    @Transactional
    public Education updateEducation(Long educationId, Long userId, EducationDto dto) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new RuntimeException("Education not found with id: " + educationId));

        // Check authorization through CV
        CV cv = cvRepository.findById(education.getCvId())
                .orElseThrow(() -> new RuntimeException("CV not found"));

        if (!cv.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You don't own this education entry");
        }

        // Update fields
        education.setInstitution(dto.getInstitution());
        education.setDegree(dto.getDegree());
        education.setFieldOfStudy(dto.getFieldOfStudy());
        education.setStartDate(dto.getStartDate());
        education.setEndDate(dto.getEndDate());

        return educationRepository.save(education);
    }

    @Transactional
    public void deleteEducation(Long educationId, Long userId) {
        Education education = educationRepository.findById(educationId)
                .orElseThrow(() -> new RuntimeException("Education not found with id: " + educationId));

        // Check authorization
        CV cv = cvRepository.findById(education.getCvId())
                .orElseThrow(() -> new RuntimeException("CV not found"));

        if (!cv.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You don't own this education entry");
        }

        educationRepository.delete(education);
    }

    // ===== Experience Operations =====

    @Transactional
    public Experience addExperience(Long cvId, Long userId, ExperienceDto dto) {
        CV cv = cvRepository.findById(cvId)
                .orElseThrow(() -> new RuntimeException("CV not found with id: " + cvId));

        // Check authorization
        if (!cv.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You don't own this CV");
        }

        Experience experience = new Experience();
        experience.setCvId(cvId);
        experience.setCompany(dto.getCompany());
        experience.setRole(dto.getRole());
        experience.setDescription(dto.getDescription());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setIsCurrent(dto.getIsCurrent());

        return experienceRepository.save(experience);
    }

    public List<Experience> getExperienceByCvId(Long cvId) {
        return experienceRepository.findByCvId(cvId);
    }

    @Transactional
    public Experience updateExperience(Long experienceId, Long userId, ExperienceDto dto) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + experienceId));

        // Check authorization
        CV cv = cvRepository.findById(experience.getCvId())
                .orElseThrow(() -> new RuntimeException("CV not found"));

        if (!cv.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You don't own this experience entry");
        }

        // Update fields
        experience.setCompany(dto.getCompany());
        experience.setRole(dto.getRole());
        experience.setDescription(dto.getDescription());
        experience.setStartDate(dto.getStartDate());
        experience.setEndDate(dto.getEndDate());
        experience.setIsCurrent(dto.getIsCurrent());

        return experienceRepository.save(experience);
    }

    @Transactional
    public void deleteExperience(Long experienceId, Long userId) {
        Experience experience = experienceRepository.findById(experienceId)
                .orElseThrow(() -> new RuntimeException("Experience not found with id: " + experienceId));

        // Check authorization
        CV cv = cvRepository.findById(experience.getCvId())
                .orElseThrow(() -> new RuntimeException("CV not found"));

        if (!cv.getUserId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You don't own this experience entry");
        }

        experienceRepository.delete(experience);
    }

    // ===== Helper Methods =====

    public List<CV> getAllPublicCVs() {
        return cvRepository.findByIsPublicTrue();
    }

    public boolean userHasCV(Long userId) {
        return cvRepository.existsByUserId(userId);
    }
}