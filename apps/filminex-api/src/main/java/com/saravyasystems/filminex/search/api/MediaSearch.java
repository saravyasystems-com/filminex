package com.saravyasystems.filminex.search.api;

/** Public boundary for a disposable, rebuildable search projection. */
public interface MediaSearch {

    void index(SearchDocument document);

    SearchPage search(SearchQuery query);

    void delete(String documentId);

    void deleteAll();
}
