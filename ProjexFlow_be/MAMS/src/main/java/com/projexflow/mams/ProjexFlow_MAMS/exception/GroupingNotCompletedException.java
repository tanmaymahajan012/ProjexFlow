package com.projexflow.mams.ProjexFlow_MAMS.exception;

public class GroupingNotCompletedException extends RuntimeException {
    public static final String DEFAULT_MESSAGE = "Grouping is still OPEN. This resource is available only after the admin CLOSES grouping.";
    public GroupingNotCompletedException() {
        super(DEFAULT_MESSAGE);
    }
    public GroupingNotCompletedException(String message) {
        super(message);
    }
}
