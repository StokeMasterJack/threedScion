package c3i.featureModel.shared.boolExpr;

public class SimplifiedToFalse implements CspFailure {

    private final BoolExpr before;

    public SimplifiedToFalse(BoolExpr before) {
        this.before = before;
    }

    public BoolExpr getBefore() {
        return before;
    }

}
