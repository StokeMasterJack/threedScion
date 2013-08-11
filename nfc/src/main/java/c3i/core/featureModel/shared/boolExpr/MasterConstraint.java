package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;
import c3i.core.featureModel.shared.Bit;
import c3i.core.featureModel.shared.Tri;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Top-level And
 */
public class MasterConstraint {

    public BoolExpr[] constraints;
    public boolean constantConstraint;

    private boolean dirty;

    public MasterConstraint(LinkedHashSet<BoolExpr> expressions) {
        constraints = collectionToArray(expressions);
    }

    public MasterConstraint(BoolExpr expr) {
        if (expr.isAnd()) {
            constraints = collectionToArray(expr.getExpressions());
        } else {
            constraints = new BoolExpr[1];
            constraints[0] = expr;
        }

    }


    private static BoolExpr[] collectionToArray(Collection<BoolExpr> expressions) {
        BoolExpr[] a = new BoolExpr[expressions.size()];
        Iterator<BoolExpr> it = expressions.iterator();
        for (int i = 0; i < a.length; i++) {
            a[i] = it.next();
        }
        return a;
    }

    public MasterConstraint(MasterConstraint that) {
        assert !that.isDirty();

        if (that.isConstant()) {
            this.constraints = null;
            this.constantConstraint = that.constantConstraint;
        } else {
            assert that.constraints != null;
            this.constraints = new BoolExpr[that.constraints.length];
            System.arraycopy(that.constraints, 0, this.constraints, 0, constraints.length);
        }
    }


    public BoolExpr[] getExpressions() {
        return constraints;
    }

    public void flattenTopLevelImplications() {


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

        if (!needsFlattening) return;

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

        this.constraints = newConstraints;
        dirty();

    }

    public void flattenTopLevelConflicts() {

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

        if (!needsFlattening) return;


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

        this.constraints = newConstraints;
        dirty();


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
     * @param e
     * @return null is not an ConflictThatNeedsFlattening, else it returns the e.asConflict.expr2.expressions
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

//        return e.isConflict() && e.asConflict().getExpr2().isOr();
    }

//    public boolean containsAnyTopLevelImpsThatNeedFlattening() {
//        for (int i = 0; i < constraints.length; i++) {
//            BoolExpr e = constraints[i];
//            BoolExpr[] a = isImpThatNeedsFlattening(e);
//            if (a != null) return true;
//        }
//        return false;
//    }
//
//    private boolean containsAnyTopLevelConflictsThatNeedFlattening() {
//        for (int i = 0; i < constraints.length; i++) {
//            BoolExpr e = constraints[i];
//            if (isConflictThatNeedsFlattening(e)) return true;
//        }
//        return false;
//    }

//    public void externalizeFixupVars(FixupVars fixupVars) {
//        assert !containsAnyTopLevelImpsThatNeedFlattening();
//
//        boolean anyChange = false;
//
//        //first build fixupVars database
//        for (Var var : constraints.getCareVars()) {
//            fixupVars.addFixupVarIfAppropriate(var, this);
//        }
//
//        //then remove fixupVar clauses
//        LinkedHashSet<BoolExpr> set = new LinkedHashSet<BoolExpr>();
//        for (BoolExpr clause : constraints.getExpressions()) {
//            boolean ch = fixupVars.addIfClauseShouldBeKept(clause, set);
//            if (ch) anyChange = true;
//        }
//
//
//        if (anyChange) {
//            this.constraints = new And(set);
//            dirty();
//        }
//    }
//

    /**
     * Should be called right after flattenImplications but before converting impls to ors
     */
//    public void replaceComplimentImplicationsWithIff() {
//
//        LinkedHashSet<BoolExpr> a = new LinkedHashSet<BoolExpr>();
//
//        if (constraints.isAnd()) {
//
//            boolean anyChange = false;
//            for (BoolExpr e1 : getExpressions()) {
//                BoolExpr compliment = constraints.asAnd().findCompliment(e1);
//                if (compliment != null) {
//                    Iff newIff = new Iff(compliment.getExpr1(), compliment.getExpr2());
//                    boolean added = a.add(newIff);
//                    if (added) {
//                        System.out.println("Added new iff: " + newIff);
//                        anyChange = true;
//                    } else {
////                    System.out.println("Added dup iff: " + newIff);
//                    }
//                } else {
//                    a.add(e1);
//                }
//            }
//
//            if (anyChange) {
//                this.constraints = new And(a);
//            }
//        }
//
//
//    }

//    public void externalizeSingleVarIffs(IffContext ctx) {
//
//        boolean anySwaps = false;
//
//        for (BoolExpr e : getExpressions()) {
//            if (e.isIff() && e.getExpr1().isVar()) {
//
//                Var var = e.getExpr1().asVar();
//                if (var.getParent().isModelCodeXorParent()) continue;
//                BoolExpr exp = e.getExpr2();
//
//                System.out.println("IIF-Replace - replacing var " + var + " with " + exp);
//                boolean ch = ctx.putSingleVarIff(var, exp);
//
//                if (ch) anySwaps = true;
//
//            } else if (e.isIff() && e.getExpr2().isVar()) {
//                Var var = e.getExpr2().asVar();
//                if (var.getParent().isModelCodeXorParent()) continue;
//                BoolExpr exp = e.getExpr1();
//
//                System.out.println("IIF-Replace - replacing var " + var + " with " + exp);
//                boolean ch = ctx.putSingleVarIff(var, exp);
//
//                if (ch) anySwaps = true;
//            }
//        }
//
//
//        if (anySwaps) {
//            BoolExpr expr = constraints.cleanOutIffVars(ctx);
//            if (expr != constraints) {
//                if (expr.isAnd()) {
//                    this.constraints = expr.asAnd();
//                } else {
//                    throw new UnsupportedOperationException();
////                    this.rootExpr = new And(expr.getExpressions());
//                }
//            }
//        }
//
//    }
    public Or getLongestOr() {
        Or longestOr = null;
        for (BoolExpr e : getExpressions()) {
            if (e.isOr()) {
                int c = e.asOr().getExprCount();
                if (longestOr == null || c > longestOr.getExprCount()) longestOr = e.asOr();
            }
        }
        return longestOr;
    }

    public int getExpressionCount() {
        if (isConstant()) return 0;
        else return constraints.length;
    }

    public void print() {
        print("");

    }

    public void print(String prefix) {

        if (isConstant()) {
            System.err.println(prefix + "Constraint: " + constantConstraint);

        } else {
            System.out.println(prefix + "Constraint: ");
            for (int i = 0; i < constraints.length; i++) {
                BoolExpr e = constraints[i];
                System.err.println(prefix + "\t " + e);
            }
        }


    }


//    public void print() {
//
//        System.err.println("\t hash: " + this.hashCode());
//
//        Or longestOr = getLongestOr();
//        int longestOrCount = longestOr == null ? 0 : longestOr.getExprCount();
//        System.err.println("\t LongestOr: " + longestOrCount + ":" + longestOr);
//        System.err.println("\t Unit-clause count [" + getUnitClauseCount() + "]");
//
//        System.err.println("\t Top-level clauses:" + getExpressionCount() + ":");
//
//
//        if (isConstant()) {
//            System.err.println("Constraint expression: " + constantConstraint);
//
//        } else {
//            System.err.println("Constraint expression: ");
//            for (int i = 0; i < constraints.length; i++) {
//                BoolExpr e = constraints[i];
//                System.err.println("\t\t " + e);
//            }
//        }
//
//
//    }


    public int getUnitClauseCount() {
        int t = 0;
        for (BoolExpr expr : getExpressions()) {
            if (expr.isLiteral()) t++;
        }
        return t;
    }

    public void test2() throws Exception {

    }


    public int getExprCount() {
        return constraints.length;
    }

    public Set<Var> getCareVars() {
        LinkedHashSet<Var> a = new LinkedHashSet<Var>();
        for (int i = 0; i < constraints.length; i++) {
            BoolExpr e = constraints[i];
            a.addAll(e.getCareVars());
        }
        return a;
    }


    public void clean() {
        this.dirty = false;
    }

    public boolean isDirty() {
        return dirty;
    }

//    public void setExpr(BoolExpr newExpr) {
//        if (this.topExpr != newExpr) {
//            if (newExpr.isAnd()) {
//                this.topExpr = newExpr.asAnd();
//            } else {
//                this.topExpr = new And(newExpr);
//            }
//            dirty();
//        }
//    }

    private void dirty() {
        this.dirty = true;
//        resetPositionalState();
    }

//    public void propagate(AutoAssignContext ctx) {
//        rootExpr.autoAssignTrueOld(ctx);
//    }


    private void setFalse() {
        if (!isFalse()) {
            constraints = null;
            constantConstraint = false;
            dirty();
        }
    }

    private void setTrue() {
        if (!isTrue()) {
            constraints = null;
            constantConstraint = true;
            dirty();
        }
    }

    public boolean check() {
        assert constraints == null || constraints.length > 0;
        return true;
    }


    public void simplify(AutoAssignContext ctx) {

        if (isConstant()) return;

        assert constraints.length > 0;

        boolean anyChange = false;


        Expressions newConstraints = new Expressions();

        for (int i = 0; i < constraints.length; i++) {
            BoolExpr e = constraints[i];

            if (e.isFalse()) {
                setFalse();
                return;
            } else if (e.isTrue()) {
                //skip
                anyChange = true;
            } else {
                BoolExpr v = e.simplify(ctx);

                try {
                    if (e != v) {
                        anyChange = true;
                        if (v.isFalse()) {
                            setFalse();
                            return;
                        } else if (v.isTrue()) {
                            //skip
                            anyChange = true;
                        } else { //open - changed
                            newConstraints.add(v);
                        }
                    } else {
                        newConstraints.add(e);
                    }
                } catch (NegatingVarsException e1) {
                    setFalse();
                    return;
                }
            }


        }

        if (anyChange) {
            dirty();
            if (newConstraints.isEmpty()) {
                setTrue();
            } else {
                Iterator<BoolExpr> it = newConstraints.iterator();
                constraints = new BoolExpr[newConstraints.size()];
                for (int i = 0; i < constraints.length; i++) {
                    constraints[i] = it.next();
                }
            }
        }

    }


    public void logAutoAssignTrue() {
        if (BoolExpr.logAutoAssignments) {
            System.err.println("AutoAssign ROOT true");
        }
    }

    public Tri eval(AutoAssignContext ctx) {
        assert constraints != null;

        if (constraints.length == 1) {
            return constraints[0].eval(ctx);
        } else {

            int L = constraints.length;
            int trueCount = 0;
            int openCount = 0;

            for (int i = 0; i < L; i++) {

                BoolExpr e = constraints[i];
                Tri v = e.eval(ctx);

                if (v.isFalse()) {
                    return Bit.FALSE;
                } else if (v.isTrue()) {
                    trueCount++;
                } else {
                    openCount++;
                }
            }

            boolean allTrue = trueCount == L;

            if (allTrue) {
                assert openCount > 0;
                return Bit.TRUE;
            } else {
                return Bit.OPEN;
            }
        }

    }

    public void autoAssignTrue(AutoAssignContext ctx) throws AssignmentException {
        assert check();

        if (isConstant()) {
            //ignore
        } else {

            assert constraints != null && constraints.length > 0;
            logAutoAssignTrue();

            for (int i = 0; i < constraints.length; i++) {
                BoolExpr e = constraints[i];
                try {
                    e.autoAssignTrue(ctx, 1);
                } catch (AssignmentException e1) {
                    setFalse();
                    throw e1;
                }
            }

        }

    }

//    private void propagateOneConstraintVerbose(AutoAssignContext ctx, BoolExpr constraint) {
//        Map<Var, Tri> varStateBefore = BoolExpr.getVarState(ctx, constraint);
//        try {
//
//            constraint.autoAssignTrue(ctx);
//
//            Assignments assignments = (Assignments) ctx;
//            if (assignments.isDirty()) {
//                System.err.println("AutoAssignTrue: ");
//                System.err.println("\t Before varState: " + varStateBefore);
//                System.err.println("\t After  varState: " + BoolExpr.getVarState(ctx, constraint));
//            }
//
//
//        } catch (AssignmentException e) {
//
//            System.err.println("Error AutoAssignTrue: " + e);
//            System.err.println("\t Before varState: " + varStateBefore);
//            System.err.println("\t After  varState: " + BoolExpr.getVarState(ctx, constraint));
//
//            e.printStackTrace();
//
//            throw e;
//        }
//    }

//    private void propagateOneConstraint(AutoAssignContext ctx, BoolExpr constraint) {
//        constraint.autoAssignTrue(ctx);
//    }

    public void toCnf(AutoAssignContext ctx) {
        throw new UnsupportedOperationException();
//        BoolExpr ret = constraints.toCnf(ctx);
//
//        if (ret != constraints) {
//            constraints = ret;
//            dirty();
//        }

    }


    public int getConflictCountForVar(Var var) {
        if (isConstant()) return 0;

        int t = 0;

        for (int i = 0; i < constraints.length; i++) {
            BoolExpr e = constraints[i];
            if (e.isConflict() && e.getCareVars().contains(var)) {
                t++;
            }
        }
        return t;
    }

//    public Tri eval(EvalContext ctx) {
//        return constraints.eval(ctx);
//    }

    public BoolExpr[] getConstraints() {
        return constraints;
    }

    public boolean isConstant() {
        return constraints == null;
    }

    public boolean isTrue() {
        boolean retVal = isConstant() && constantConstraint == true;
        return retVal;
    }

    public boolean isFalse() {
        return isConstant() && constantConstraint == false;
    }

    public boolean isOpen() {
        return !isConstant();
    }
}
