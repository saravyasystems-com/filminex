package com.saravyasystems.filminex.search.internal;

import com.saravyasystems.filminex.audit.api.DomainEvent;
import com.saravyasystems.filminex.audit.api.DomainEventHandler;
import com.saravyasystems.filminex.search.api.MediaSearch;
import com.saravyasystems.filminex.search.api.SearchDocument;
import java.util.List;
import java.util.Map;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

final class SearchProjectionEventHandler implements DomainEventHandler {

    static final String UPSERTED = "search.document.upserted.v1";
    static final String DELETED = "search.document.deleted.v1";

    private final MediaSearch mediaSearch;
    private final ObjectMapper objectMapper;

    SearchProjectionEventHandler(MediaSearch mediaSearch, ObjectMapper objectMapper) {
        this.mediaSearch = mediaSearch;
        this.objectMapper = objectMapper;
    }

    @Override
    public boolean supports(String eventType) {
        return UPSERTED.equals(eventType) || DELETED.equals(eventType);
    }

    @Override
    public void handle(DomainEvent event) {
        try {
            JsonNode payload = objectMapper.readTree(event.payload());
            if (DELETED.equals(event.eventType())) {
                mediaSearch.delete(requiredText(payload, "id"));
                return;
            }
            mediaSearch.index(new SearchDocument(
                    requiredText(payload, "id"),
                    event.workspaceId().toString(),
                    requiredText(payload, "entityType"),
                    requiredText(payload, "title"),
                    objectMapper.convertValue(
                            payload.path("searchableText"), new TypeReference<List<String>>() {}),
                    objectMapper.convertValue(
                            payload.path("attributes"),
                            new TypeReference<Map<String, String>>() {})));
        } catch (JacksonException | IllegalArgumentException exception) {
            throw new IllegalStateException("Invalid search projection event " + event.id(), exception);
        }
    }

    private static String requiredText(JsonNode payload, String field) {
        String value = payload.path(field).asText();
        if (value.isBlank()) {
            throw new IllegalArgumentException(field + " must not be blank");
        }
        return value;
    }
}
