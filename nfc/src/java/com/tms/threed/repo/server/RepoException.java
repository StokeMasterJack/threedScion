package com.tms.threed.repo.server;

public class RepoException extends RuntimeException {

    public RepoException() {
    }

    public RepoException(String message) {
        super(message);
    }

    public RepoException(String message, Throwable cause) {
        super(message, cause);
    }

    public RepoException(Throwable cause) {
        super(cause);
    }
}
