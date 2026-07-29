package com.saravyasystems.filminex.search.internal;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.saravyasystems.filminex.search.api.MediaSearch;
import com.saravyasystems.filminex.search.api.SearchDocument;
import com.saravyasystems.filminex.search.api.SearchHit;
import com.saravyasystems.filminex.search.api.SearchPage;
import com.saravyasystems.filminex.search.api.SearchQuery;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

final class SolrMediaSearch implements MediaSearch {

    private final HttpClient client;
    private final ObjectMapper objectMapper;
    private final SolrSearchProperties properties;

    SolrMediaSearch(
            HttpClient client, ObjectMapper objectMapper, SolrSearchProperties properties) {
        this.client = client;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    @Override
    public void index(SearchDocument document) {
        Map<String, Object> projection = new LinkedHashMap<>();
        projection.put("id", document.id());
        projection.put("workspace_id_s", document.workspaceId());
        projection.put("entity_type_s", document.entityType());
        projection.put("title_t", document.title());
        projection.put("searchable_text_txt", document.searchableText());
        document.attributes().forEach((key, value) -> projection.put("attribute_" + key + "_s", value));
        post("/update?commit=true", List.of(projection));
    }

    @Override
    public SearchPage search(SearchQuery query) {
        String text = escapeQueryValue(query.text());
        String workspace = escapeQueryValue(query.workspaceId());
        String queryString = "q=" + encode("(title_t:" + text + " OR searchable_text_txt:" + text + ")")
                + "&fq=" + encode("workspace_id_s:" + workspace)
                + "&start=" + query.offset()
                + "&rows=" + query.limit()
                + "&fl=" + encode("id,entity_type_s,title_t")
                + "&wt=json";
        JsonNode response = get("/select?" + queryString).path("response");
        List<SearchHit> hits = new ArrayList<>();
        response.path("docs").forEach(node -> hits.add(new SearchHit(
                node.path("id").asText(),
                node.path("entity_type_s").asText(),
                firstText(node.path("title_t")))));
        return new SearchPage(response.path("numFound").asLong(), hits);
    }

    @Override
    public void delete(String documentId) {
        post("/update?commit=true", Map.of("delete", Map.of("id", documentId)));
    }

    @Override
    public void deleteAll() {
        post("/update?commit=true", Map.of("delete", Map.of("query", "*:*")));
    }

    private JsonNode get(String path) {
        HttpRequest request = HttpRequest.newBuilder(resolve(path))
                .timeout(properties.readTimeout())
                .GET()
                .build();
        return send(request);
    }

    private void post(String path, Object body) {
        try {
            HttpRequest request = HttpRequest.newBuilder(resolve(path))
                    .timeout(properties.readTimeout())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();
            send(request);
        } catch (JsonProcessingException exception) {
            throw new SearchUnavailableException("Could not serialize the search projection", exception);
        }
    }

    private JsonNode send(HttpRequest request) {
        try {
            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new SearchUnavailableException(
                        "Solr returned HTTP " + response.statusCode() + ": " + response.body());
            }
            return response.body().isBlank()
                    ? objectMapper.createObjectNode()
                    : objectMapper.readTree(response.body());
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new SearchUnavailableException("Solr request was interrupted", exception);
        } catch (IOException exception) {
            throw new SearchUnavailableException("Solr is unavailable", exception);
        }
    }

    private URI resolve(String path) {
        return URI.create(properties.url().toString().replaceAll("/$", "") + path);
    }

    private static String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }

    private static String escapeQueryValue(String value) {
        return "\"" + value.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
    }

    private static String firstText(JsonNode node) {
        return node.isArray() ? node.path(0).asText() : node.asText();
    }
}
