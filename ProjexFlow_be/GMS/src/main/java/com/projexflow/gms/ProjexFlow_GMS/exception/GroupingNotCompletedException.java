package com.projexflow.gms.ProjexFlow_GMS.exception;

/**
 * Thrown when two students belong to different courses but attempt a group operation
 * that must be restricted to the same course.
 */
public class GroupingNotCompletedException extends BadRequestException {
    public GroupingNotCompletedException(String message) { super(message); }
}
