package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import java.util.ArrayList;

public class XorTermsStates {

    private final Xor xor;

    private final ArrayList<BoolExpr> trueTerms = new ArrayList<BoolExpr>();
    private final ArrayList<BoolExpr> falseTerms = new ArrayList<BoolExpr>();
    private final ArrayList<BoolExpr> openTerms = new ArrayList<BoolExpr>();

    public XorTermsStates(Xor xor) {
        this.xor = xor;
    }

    public void pushTrueTerm(BoolExpr term) throws MoreThanOneTrueTermXorAssignmentException {
        trueTerms.add(term);

        if (trueTerms.size() > 1) {
            throw new MoreThanOneTrueTermXorAssignmentException(xor, term, this);
        }
    }

    public void pushFalseTerm(BoolExpr term) {
        falseTerms.add(term);
    }

    public void pushOpenTerm(BoolExpr term) {
        openTerms.add(term);
    }


    public void print() {
        System.out.println("trueTerms = " + trueTerms);
        System.out.println("falseTerms = " + falseTerms);
        System.out.println("openTerms = " + openTerms);
    }

    public int getFalseCount() {
        return falseTerms.size();
    }

    public int getOpenCount() {
        return openTerms.size();
    }

    @Override
    public String toString() {
        return " trueTerms[" + trueTerms + "]   falseTerms[" + falseTerms + "]   openTerms[" + openTerms + "]";
    }
}
