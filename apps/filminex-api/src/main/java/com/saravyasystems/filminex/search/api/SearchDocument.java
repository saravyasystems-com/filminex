package com.saravyasystems.filminex.search.api;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Searchable projection that always references an authoritative Filminex record. */
public record SearchDocument(
        String id,
        String workspaceId,
        String entityType,
        String title,
        List<String> searchableText,
        Map<String, String> attributes) {

    public SearchDocument {
        id = requireText(id, "id");
        workspaceId = requireText(workspaceId, "workspaceId");
        entityType = requireText(entityType, "entityType");
        title = requireText(title, "title");
        searchableText = List.copyOf(Objects.requireNonNull(searchableText, "searchableText"));
        attributes = Map.copyOf(Objects.requireNonNull(attributes, "attributes"));
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }
}
