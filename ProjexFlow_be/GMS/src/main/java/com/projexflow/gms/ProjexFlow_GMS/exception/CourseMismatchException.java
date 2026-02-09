package com.projexflow.gms.ProjexFlow_GMS.exception;

/**
 * Thrown when two students belong to different courses but attempt a group operation
 * that must be restricted to the same course.
 */
public class CourseMismatchException extends BadRequestException {
    public CourseMismatchException(String message) { super(message); }
}
