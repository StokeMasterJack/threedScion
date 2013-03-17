package c3i.featureModel.shared.boolExpr;

import java.util.Iterator;
import java.util.LinkedHashSet;

/**
 * Top-level And
 */
public class Formula {

    public static BoolExpr[] flattenTopLevelImplications(BoolExpr[] constraints) {

        boolean needsFlattening = false;
        int size = 0;

        for (int i = 0; i < constraints.length; i++) {
            BoolExpr e = constraints[i];

            BoolExpr[] andTerms = isImpThatNeedsFlattening(e);
            if (andTerms == null) {
                size++;
            } else {
                size += andTerms.length;
                needsFlattening = true;
            }
        }

        if (!needsFlattening) return constraints;

        assert size > constraints.length;

        BoolExpr[] newConstraints = new BoolExpr[size];

//        if(true) return;

        int pos = 0;
        for (int i = 0; i < constraints.length; i++) {
            BoolExpr e = constraints[i];
            BoolExpr[] andTerms = isImpThatNeedsFlattening(e);

            if (andTerms == null) {
                newConstraints[pos] = e;
                pos++;
            } else {
                for (int j = 0; j < andTerms.length; j++) {
                    BoolExpr andTerm = andTerms[j];
                    newConstraints[pos] = new Imp(e.getExpr1(), andTerm);
                    pos++;
                }
            }
        }

        return newConstraints;

    }

    public static BoolExpr[] flattenTopLevelConflicts(BoolExpr[] constraints) {

        boolean needsFlattening = false;
        int size = 0;

        for (int i = 0; i < constraints.length; i++) {
            BoolExpr e = constraints[i];

            BoolExpr[] orTerms = isConflictThatNeedsFlattening(e);
            if (orTerms == null) {
                size++;
            } else {
                size += orTerms.length;
                needsFlattening = true;
            }
        }

        if (!needsFlattening) return constraints;


        assert size > constraints.length;

        BoolExpr[] newConstraints = new BoolExpr[size];

        int pos = 0;
        for (int i = 0; i < constraints.length; i++) {
            BoolExpr e = constraints[i];

            BoolExpr[] orTerms = isConflictThatNeedsFlattening(e);
            if (orTerms == null) {
                newConstraints[pos] = e;
                pos++;
            } else {
                for (int j = 0; j < orTerms.length; j++) {
                    BoolExpr orTerm = orTerms[j];
                    newConstraints[pos] = new Conflict(e.getExpr1(), orTerm);
                    pos++;
                }
            }
        }

        return newConstraints;

    }


    /**
     * @param e
     * @return null is not an ImpThatNeedsFlattening, else it returns the e.asImp.expr2.expressions
     */
    private static BoolExpr[] isImpThatNeedsFlattening(BoolExpr e) {

        if (!e.isImp()) return null;

        BoolExpr eExpr2 = e.asImp().getExpr2();

        if (!eExpr2.isAnd()) return null;

        LinkedHashSet<BoolExpr> andTerms = eExpr2.asAnd().getExpressions();
        Iterator<BoolExpr> it = andTerms.iterator();
        BoolExpr[] aAndTerms = new BoolExpr[andTerms.size()];
        for (int i = 0; i < aAndTerms.length; i++) {
            aAndTerms[i] = it.next();
        }

        return aAndTerms;
    }

    /**
     * @return null if not an ConflictThatNeedsFlattening, else it returns the e.asConflict.expr2.expressions
     */
    private static BoolExpr[] isConflictThatNeedsFlattening(BoolExpr e) {

        if (!e.isConflict()) return null;
        BoolExpr eExpr2 = e.asConflict().getExpr2();

        if (!eExpr2.isOr()) return null;

        LinkedHashSet<BoolExpr> orTerms = eExpr2.asOr().getExpressions();
        Iterator<BoolExpr> it = orTerms.iterator();
        BoolExpr[] aOrTerms = new BoolExpr[orTerms.size()];
        for (int i = 0; i < aOrTerms.length; i++) {
            aOrTerms[i] = it.next();
        }

        return aOrTerms;

    }


}