package com.saravyasystems.filminex.identity.api;

/** Raised when an identity is not allowed to administer a workspace. */
public final class WorkspaceAccessDeniedException extends RuntimeException {

    public WorkspaceAccessDeniedException(String message) {
        super(message);
    }
}
