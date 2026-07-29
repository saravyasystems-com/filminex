package com.saravyasystems.filminex.search.internal;

final class SearchUnavailableException extends RuntimeException {

    SearchUnavailableException(String message) {
        super(message);
    }

    SearchUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
