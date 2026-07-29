package com.saravyasystems.filminex.search.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.saravyasystems.filminex.audit.api.DomainEvent;
import com.saravyasystems.filminex.search.api.MediaSearch;
import com.saravyasystems.filminex.search.api.SearchDocument;
import com.saravyasystems.filminex.search.api.SearchPage;
import com.saravyasystems.filminex.search.api.SearchQuery;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

class SearchProjectionEventHandlerTests {

    private static final UUID WORKSPACE_ID =
            UUID.fromString("314e4370-e98c-4893-a0a1-20fa8819fbc2");

    private final RecordingSearch search = new RecordingSearch();
    private final SearchProjectionEventHandler handler =
            new SearchProjectionEventHandler(search, JsonMapper.builder().build());

    @Test
    void translatesUpsertEventIntoWorkspaceScopedProjection() {
        handler.handle(event(
                SearchProjectionEventHandler.UPSERTED,
                """
                {
                  "id": "scene-7",
                  "entityType": "scene",
                  "title": "The Arrival",
                  "searchableText": ["night", "railway platform"],
                  "attributes": {"episode": "1"}
                }
                """));

        assertThat(search.indexed.id()).isEqualTo("scene-7");
        assertThat(search.indexed.workspaceId()).isEqualTo(WORKSPACE_ID.toString());
        assertThat(search.indexed.searchableText()).containsExactly("night", "railway platform");
        assertThat(search.indexed.attributes()).containsEntry("episode", "1");
    }

    @Test
    void translatesDeleteEventIntoIdempotentProjectionRemoval() {
        handler.handle(event(
                SearchProjectionEventHandler.DELETED,
                """
                {"id": "scene-7"}
                """));

        assertThat(search.deletedId).isEqualTo("scene-7");
    }

    private DomainEvent event(String type, String payload) {
        return new DomainEvent(
                UUID.randomUUID(),
                WORKSPACE_ID,
                "scene",
                "scene-7",
                type,
                payload,
                Instant.parse("2026-07-29T12:00:00Z"));
    }

    private static final class RecordingSearch implements MediaSearch {

        private SearchDocument indexed;
        private String deletedId;

        @Override
        public void index(SearchDocument document) {
            indexed = document;
        }

        @Override
        public SearchPage search(SearchQuery query) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void delete(String documentId) {
            deletedId = documentId;
        }

        @Override
        public void deleteAll() {
            throw new UnsupportedOperationException();
        }
    }
}
