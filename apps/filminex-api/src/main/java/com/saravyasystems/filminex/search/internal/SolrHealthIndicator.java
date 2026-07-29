package com.saravyasystems.filminex.search.internal;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("solr")
final class SolrHealthIndicator implements HealthIndicator {

    private final HttpClient client;
    private final SolrSearchProperties properties;

    SolrHealthIndicator(SolrSearchProperties properties) {
        this.properties = properties;
        this.client = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();
    }

    @Override
    public Health health() {
        try {
            HttpRequest request = HttpRequest.newBuilder(
                            properties.url().resolve(properties.url().getPath() + "/admin/ping?wt=json"))
                    .timeout(properties.readTimeout())
                    .GET()
                    .build();
            HttpResponse<Void> response =
                    client.send(request, HttpResponse.BodyHandlers.discarding());
            return response.statusCode() == 200
                    ? Health.up().withDetail("core", properties.url().getPath()).build()
                    : Health.down().withDetail("status", response.statusCode()).build();
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return Health.down(exception).build();
        } catch (Exception exception) {
            return Health.down(exception).build();
        }
    }
}
