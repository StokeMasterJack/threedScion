package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import java.util.ArrayList;

public class XorTermsStates {

    private final ArrayList<BoolExpr> trueTerms = new ArrayList<BoolExpr>();
    private final ArrayList<BoolExpr> falseTerms = new ArrayList<BoolExpr>();
    private final ArrayList<BoolExpr> openTerms = new ArrayList<BoolExpr>();

    public void pushTrueTerm(BoolExpr term) {
        trueTerms.add(term);
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
