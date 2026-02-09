package com.projexflow.UMS.ProjexFlow_UMS.dto;

/**
 * Minimal internal response to resolve role-specific IDs without leaking full profiles.
 */
public record IdOnlyResponse(Long id) {}
