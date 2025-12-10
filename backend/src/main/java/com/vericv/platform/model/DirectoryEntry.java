package com.vericv.platform.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "directory_entries")
public class DirectoryEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @NotBlank(message = "Full name is required")
    @Size(max = 200, message = "Full name must be less than 200 characters")
    @Column(name = "full_name", nullable = false, length = 200)
    private String fullName;

    @Size(max = 200, message = "Headline must be less than 200 characters")
    @Column(length = 200)
    private String headline;

    @Size(max = 100, message = "Location must be less than 100 characters")
    @Column(length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_badge", nullable = false)
    private VerificationBadge verificationBadge = VerificationBadge.NONE;

    @Column(name = "is_visible", nullable = false)
    private Boolean isVisible = true;

    @Column(name = "searchable_text", length = 2000)
    private String searchableText;

    @Column(name = "profile_views", nullable = false)
    private Integer profileViews = 0;

    @Column(name = "last_active")
    private LocalDateTime lastActive;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationship: One directory entry belongs to one user
    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    // Verification badge levels enum
    public enum VerificationBadge {
        NONE, // No verification
        BRONZE, // Email verified only
        SILVER, // 1 credential verified
        GOLD, // 2+ credentials verified
        PLATINUM // All credentials verified
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        lastActive = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public DirectoryEntry() {
    }

    public DirectoryEntry(Long userId, String fullName, String headline) {
        this.userId = userId;
        this.fullName = fullName;
        this.headline = headline;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public VerificationBadge getVerificationBadge() {
        return verificationBadge;
    }

    public void setVerificationBadge(VerificationBadge verificationBadge) {
        this.verificationBadge = verificationBadge;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public String getSearchableText() {
        return searchableText;
    }

    public void setSearchableText(String searchableText) {
        this.searchableText = searchableText;
    }

    public Integer getProfileViews() {
        return profileViews;
    }

    public void setProfileViews(Integer profileViews) {
        this.profileViews = profileViews;
    }

    public LocalDateTime getLastActive() {
        return lastActive;
    }

    public void setLastActive(LocalDateTime lastActive) {
        this.lastActive = lastActive;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}