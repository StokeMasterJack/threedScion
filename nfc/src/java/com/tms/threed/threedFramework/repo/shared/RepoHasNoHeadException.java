package com.tms.threed.threedFramework.repo.shared;

public class RepoHasNoHeadException extends RuntimeException {

    public RepoHasNoHeadException() {
    }

    public RepoHasNoHeadException(String message) {
        super(message);
    }

    public RepoHasNoHeadException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepoHasNoHeadException(Throwable cause) {
        super(cause);
    }
}
