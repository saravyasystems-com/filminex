package com.saravyasystems.filminex.search.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saravyasystems.filminex.search.api.MediaSearch;
import java.net.http.HttpClient;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(SolrSearchProperties.class)
class SolrSearchConfiguration {

    @Bean
    MediaSearch mediaSearch(SolrSearchProperties properties, ObjectMapper objectMapper) {
        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(properties.connectTimeout())
                .build();
        return new SolrMediaSearch(client, objectMapper, properties);
    }
}
