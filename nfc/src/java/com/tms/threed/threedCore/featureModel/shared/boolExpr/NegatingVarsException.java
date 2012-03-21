package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class NegatingVarsException extends RuntimeException {


    public NegatingVarsException() {
    }

    public NegatingVarsException(String s) {
        super(s);
    }

    public NegatingVarsException(String s, Throwable throwable) {
        super(s, throwable);
    }

    public NegatingVarsException(Throwable throwable) {
        super(throwable);
    }
}