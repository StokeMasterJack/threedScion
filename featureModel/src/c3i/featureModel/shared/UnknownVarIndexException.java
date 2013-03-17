package c3i.featureModel.shared;

public class UnknownVarIndexException extends RuntimeException {

    private final int badVarIndex;

    public UnknownVarIndexException(int badVarIndex) {
        this.badVarIndex = badVarIndex;
    }

    public int getBadVarIndex() {
        return badVarIndex;
    }


    @Override
    public String getMessage() {
        return "varIndex [" + badVarIndex + "] is not in FeatureModel";
    }
}
