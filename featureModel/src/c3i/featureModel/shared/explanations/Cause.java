package c3i.featureModel.shared.explanations;

public enum Cause {

    INFERENCE,
    DECISION,
    USER,
    DEFAULT;

    public boolean isInference() {
        return this == INFERENCE;
    }

}
