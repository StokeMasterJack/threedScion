package c3i.featureModel.shared.boolExpr;

import c3i.featureModel.shared.AutoAssignContext;

public class MoreThanOneTrueTermXorAssignmentException extends XorAssignmentException {

    private final BoolExpr secondTrueTerm;
    private final XorTermsStates statesOfAllTerms;

    public MoreThanOneTrueTermXorAssignmentException(Xor xor, BoolExpr secondTrueTerm, XorTermsStates statesOfAllTerms, AutoAssignContext context) {
        super(xor, context);
        this.secondTrueTerm = secondTrueTerm;
        this.statesOfAllTerms = statesOfAllTerms;
    }

    public BoolExpr getSecondTrueTerm() {
        return secondTrueTerm;
    }

    public XorTermsStates getStatesOfAllTerms() {
        return statesOfAllTerms;
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "    secondTrueTerm[" + secondTrueTerm + "]   statesOfAllTerms[" + statesOfAllTerms + "]";
    }

    public void print() {
        System.out.println(getMessage());
    }
}
