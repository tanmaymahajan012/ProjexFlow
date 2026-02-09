package com.projexflow.mams.ProjexFlow_MAMS.dto;

public class AssignResponse {

    private Long batchId;
    private String course;
    private int totalGroups;
    private int totalMentors;
    private int assignedCount;

    // No-args constructor (required by Jackson)
    public AssignResponse() {
    }

    // All-args constructor
    public AssignResponse(Long batchId, String course, int totalGroups,
                          int totalMentors, int assignedCount) {
        this.batchId = batchId;
        this.course = course;
        this.totalGroups = totalGroups;
        this.totalMentors = totalMentors;
        this.assignedCount = assignedCount;
    }

    // Getters and Setters

    public Long getBatchId() {
        return batchId;
    }

    public void setBatchId(Long batchId) {
        this.batchId = batchId;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public int getTotalGroups() {
        return totalGroups;
    }

    public void setTotalGroups(int totalGroups) {
        this.totalGroups = totalGroups;
    }

    public int getTotalMentors() {
        return totalMentors;
    }

    public void setTotalMentors(int totalMentors) {
        this.totalMentors = totalMentors;
    }

    public int getAssignedCount() {
        return assignedCount;
    }

    public void setAssignedCount(int assignedCount) {
        this.assignedCount = assignedCount;
    }

    @Override
    public String toString() {
        return "AssignResponse{" +
                "batchId=" + batchId +
                ", course='" + course + '\'' +
                ", totalGroups=" + totalGroups +
                ", totalMentors=" + totalMentors +
                ", assignedCount=" + assignedCount +
                '}';
    }
}
