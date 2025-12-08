package com.vericv.platform.dto.cv;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateCVRequest {

    @NotBlank(message = "Headline is required")
    @Size(max = 200, message = "Headline must be less than 200 characters")
    private String headline;

    @Size(max = 2000, message = "Summary must be less than 2000 characters")
    private String summary;

    private Boolean isPublic = true;

    // Constructors
    public CreateCVRequest() {
    }

    public CreateCVRequest(String headline, String summary) {
        this.headline = headline;
        this.summary = summary;
    }

    // Getters and Setters
    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Boolean isPublic) {
        this.isPublic = isPublic;
    }
}