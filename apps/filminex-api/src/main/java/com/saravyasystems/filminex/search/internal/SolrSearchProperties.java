package com.saravyasystems.filminex.search.internal;

import java.net.URI;
import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("filminex.search.solr")
record SolrSearchProperties(URI url, Duration connectTimeout, Duration readTimeout) {

    SolrSearchProperties {
        if (url == null || !"http".equalsIgnoreCase(url.getScheme())
                && !"https".equalsIgnoreCase(url.getScheme())) {
            throw new IllegalArgumentException("Solr URL must use HTTP or HTTPS");
        }
        if (connectTimeout == null || connectTimeout.isNegative() || connectTimeout.isZero()) {
            throw new IllegalArgumentException("Solr connect timeout must be positive");
        }
        if (readTimeout == null || readTimeout.isNegative() || readTimeout.isZero()) {
            throw new IllegalArgumentException("Solr read timeout must be positive");
        }
    }
}
