package com.saravyasystems.filminex.search.api;

import java.util.List;

public record SearchPage(long total, List<SearchHit> hits) {

    public SearchPage {
        hits = List.copyOf(hits);
    }
}
