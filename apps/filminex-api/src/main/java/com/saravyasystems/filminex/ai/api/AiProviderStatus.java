package com.saravyasystems.filminex.ai.api;

/** Safe provider status; credentials are never exposed. */
public record AiProviderStatus(String provider, String model, boolean available) {}
