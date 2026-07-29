package com.saravyasystems.filminex.search.api;

/** Workspace-scoped text query against a rebuildable search projection. */
public record SearchQuery(String workspaceId, String text, int offset, int limit) {

    public SearchQuery {
        if (workspaceId == null || workspaceId.isBlank()) {
            throw new IllegalArgumentException("workspaceId must not be blank");
        }
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("text must not be blank");
        }
        if (offset < 0) {
            throw new IllegalArgumentException("offset must not be negative");
        }
        if (limit < 1 || limit > 100) {
            throw new IllegalArgumentException("limit must be between 1 and 100");
        }
    }
}
