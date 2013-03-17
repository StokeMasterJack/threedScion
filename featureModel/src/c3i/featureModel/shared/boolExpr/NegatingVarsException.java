package c3i.featureModel.shared.boolExpr;

public class NegatingVarsException extends RuntimeException implements CspFailure{


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
