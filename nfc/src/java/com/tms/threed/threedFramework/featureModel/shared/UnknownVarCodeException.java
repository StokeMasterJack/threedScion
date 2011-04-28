package com.tms.threed.threedFramework.featureModel.shared;

public class UnknownVarCodeException extends RuntimeException {

    private final String badVarCode;

    public UnknownVarCodeException(String badVarCode) {
        this.badVarCode = badVarCode;
    }

    public String getBadVarCode() {
        return badVarCode;
    }


    @Override public String getMessage() {
        return "varCode [" + badVarCode + "] is not in FeatureModel";
    }
}
