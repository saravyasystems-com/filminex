package com.saravyasystems.filminex.ai.api;

/** Normalized provider failure that does not leak provider SDK exceptions. */
public final class AiProviderException extends RuntimeException {

    public AiProviderException(String message) {
        super(message);
    }

    public AiProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
