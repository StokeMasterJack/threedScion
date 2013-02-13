package c3i.core.featureModel.shared;

public class UnknownVarIndexException extends RuntimeException {

    private final int badVarId;

    public UnknownVarIndexException(int badVarId) {
        this.badVarId = badVarId;
    }

    public int getBadVarId() {
        return badVarId;
    }


    @Override
    public String getMessage() {
        return "varId [" + badVarId + "] is not in FeatureModel";
    }
}
