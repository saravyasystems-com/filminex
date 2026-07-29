package com.saravyasystems.filminex.search.api;

/** Minimal result returned without exposing Solr-specific response types. */
public record SearchHit(String id, String entityType, String title) {}
