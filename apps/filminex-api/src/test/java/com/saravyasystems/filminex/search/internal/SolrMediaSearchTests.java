package com.saravyasystems.filminex.search.internal;

import static org.assertj.core.api.Assertions.assertThat;

import com.saravyasystems.filminex.search.api.MediaSearch;
import com.saravyasystems.filminex.search.api.SearchDocument;
import com.saravyasystems.filminex.search.api.SearchPage;
import com.saravyasystems.filminex.search.api.SearchQuery;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SolrMediaSearchTests {

    @Autowired
    private MediaSearch mediaSearch;

    @AfterEach
    void clearProjection() {
        mediaSearch.deleteAll();
    }

    @Test
    void indexesSearchesScopesAndDeletesAProjection() {
        mediaSearch.index(new SearchDocument(
                "scene-101",
                "workspace-a",
                "scene",
                "Monsoon Reunion",
                List.of("Asha returns to the railway station", "Hindi transcript"),
                Map.of("language", "hi")));
        mediaSearch.index(new SearchDocument(
                "scene-202",
                "workspace-b",
                "scene",
                "Monsoon Elsewhere",
                List.of("Another tenant must remain invisible"),
                Map.of()));

        SearchPage result = mediaSearch.search(new SearchQuery("workspace-a", "Monsoon", 0, 20));

        assertThat(result.total()).isEqualTo(1);
        assertThat(result.hits()).singleElement().satisfies(hit -> {
            assertThat(hit.id()).isEqualTo("scene-101");
            assertThat(hit.entityType()).isEqualTo("scene");
            assertThat(hit.title()).isEqualTo("Monsoon Reunion");
        });

        mediaSearch.delete("scene-101");

        assertThat(mediaSearch.search(new SearchQuery("workspace-a", "Monsoon", 0, 20)).total())
                .isZero();
    }
}
