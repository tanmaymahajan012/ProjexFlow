package com.projexflow.mams.ProjexFlow_MAMS.dto.external;

/**
 * Mirror of the UMS student response used by GMS, duplicated here to avoid cross-service jar dependency.
 */
public class UmsStudentResponse {
    private Long id;
    private String name;
    private String email;
    private String prn;
    private Long batchId;
    private String course;
    private String profilePicUrl;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPrn() { return prn; }
    public void setPrn(String prn) { this.prn = prn; }

    public Long getBatchId() { return batchId; }
    public void setBatchId(Long batchId) { this.batchId = batchId; }

    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }

    public String getProfilePicUrl() { return profilePicUrl; }
    public void setProfilePicUrl(String profilePicUrl) { this.profilePicUrl = profilePicUrl; }
}
