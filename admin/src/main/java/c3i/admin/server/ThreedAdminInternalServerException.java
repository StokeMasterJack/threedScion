package c3i.admin.server;

public class ThreedAdminInternalServerException extends RuntimeException {

    public ThreedAdminInternalServerException() {
    }

    public ThreedAdminInternalServerException(String message) {
        super(message);
    }

    public ThreedAdminInternalServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public ThreedAdminInternalServerException(Throwable cause) {
        super(cause);
    }
}
