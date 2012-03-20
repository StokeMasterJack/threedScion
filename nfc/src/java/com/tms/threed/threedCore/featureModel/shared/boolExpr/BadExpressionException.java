package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class BadExpressionException extends RuntimeException {

    public BadExpressionException() {
    }

    public BadExpressionException(String message) {
        super(message);
    }

    public BadExpressionException(String message, Throwable cause) {
        super(message, cause);
    }

    public BadExpressionException(Throwable cause) {
        super(cause);
    }
}
