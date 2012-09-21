package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.Bit;
import c3i.core.featureModel.shared.EvalContext;
import c3i.core.featureModel.shared.Tri;

import java.util.*;

public class FixupVars {

    private final Collection<Var> pngVars;

    private final LinkedHashMap<Var, FixupVar> nonPngs;

    private final LinkedHashMap<Var, FixupVar> map;

    private final LinkedHashSet<BoolExpr> removedClauses;

    public FixupVars(Collection<Var> pngVars) {
        this.pngVars = pngVars;
        map = new LinkedHashMap<Var, FixupVar>();
        nonPngs = new LinkedHashMap<Var, FixupVar>();

        removedClauses = new LinkedHashSet<BoolExpr>();
    }

    private FixupVar addFixupVar(FixupVar fixupVar) {
        Var v = fixupVar.var;
        if (pngVars.contains(v)) {
            map.put(v, fixupVar);
        } else {
            nonPngs.put(v, fixupVar);
        }
        return fixupVar;
    }

    public FixupVar get(Var var) {
        return map.get(var);
    }

    public FixupVar addFixupVar(Var var, LinkedHashSet<Imp> simpleImpls) {
        return this.addFixupVar(new FixupVar(var, simpleImpls));
    }

    public int size() {
        return map.size();
    }

    public void print() {
        print(null);
    }

    public void print(VarAnalyzer varAnalyzer) {
        for (Var var : map.keySet()) {
            FixupVar fixupVar = map.get(var);
            fixupVar.print(varAnalyzer);
        }
    }

    public LinkedHashSet<BoolExpr> getRemovedClauses() {
        return removedClauses;
    }

    public boolean contains(Var var) {
        return isFixupVar(var);
    }

    public Set<Var> getImplicants() {
        Set<Var> a = new HashSet<Var>();
        for (FixupVar fixupVar : map.values()) {
            Collection<Var> implicants = fixupVar.getImplicants();
            a.addAll(implicants);
        }
        return a;
    }

    public static class CalculatedValue {

        private final BoolExpr test;
        private final Tri trueResult;
        private final Tri falseResult;
        private final Tri openResult;

        public CalculatedValue(BoolExpr test, Tri trueResult, Tri falseResult, Tri openResult) {
            assert test != null;
            assert trueResult != null;
            assert falseResult != null;
            assert openResult != null;

            this.test = test;
            this.trueResult = trueResult;
            this.falseResult = falseResult;
            this.openResult = openResult;
        }


        public Tri eval(EvalContext ctx) {
            Tri val = test.eval(ctx);
            if (val.isTrue()) {
                return trueResult;
            } else if (val.isFalse()) {
                return falseResult;
            } else if (val.isOpen()) {
                return openResult;
            } else {
                throw new IllegalStateException();
            }
        }

        public BoolExpr getTest() {
            return test;
        }

        @Override
        public String toString() {
            return "(" + test + "?" + trueResult + ":" + falseResult + ")";
        }

        public Collection<Var> getTestVars() {
            LinkedHashSet<Var> vars = new LinkedHashSet<Var>();
            Collection<BoolExpr> expressions = test.getExpressions();
            for (BoolExpr e : expressions) {
                if (e.isVar()) {
                    vars.add(e.asVar());
                } else {
                    System.out.println("NON VAR");
                }
            }
            return vars;

        }
    }


    public static class FixupVar {

        private final Var var;
        private final LinkedHashSet<Imp> simpleImpls;
        private final CalculatedValue calculatedValue;

        FixupVar(Var var, LinkedHashSet<Imp> simpleImpls) {
            this.var = var;
            this.simpleImpls = simpleImpls;
            this.calculatedValue = initCalculatedValue();
        }

        public void print() {
            print(null);
        }

        public void print(VarAnalyzer varAnalyzer) {
            String impliedBy = calculatedValue.test.toString();
            boolean png = varAnalyzer.isPng(var);
            boolean support = varAnalyzer.isConstraintCareVar(var);

            System.out.println(var + " <= " + impliedBy + "\t png:" + png + "\t support:" + support + "\t derived:" + var.isDerived());

            System.out.println("\t\t Implicants:");

            Collection<BoolExpr> implicantVars = calculatedValue.test.getExpressions();
            for (BoolExpr implicantVar : implicantVars) {
                Var vv = implicantVar.asVar();

                boolean vvSupport = varAnalyzer.isConstraintCareVar(vv);
                boolean vvPng = varAnalyzer.isPng(vv);
                System.out.println("\t\t\t " + vv + "\t png:" + vvPng + "\t support:" + vvSupport + "\t derived:" + var.isDerived());
            }
        }

        public Collection<Var> getTestVars() {
            return calculatedValue.getTestVars();
        }

        private BoolExpr computeTest() {
            if (simpleImpls.size() == 1) {
                return simpleImpls.iterator().next().getExpr1();
            } else {
                LinkedHashSet<BoolExpr> orChildExpressions = new LinkedHashSet<BoolExpr>();
                for (Imp simpleImpl : simpleImpls) {
                    orChildExpressions.add(simpleImpl.getExpr1());
                }
                return new Or(orChildExpressions);
            }
        }

        private BoolExpr computeDefaultValue() {
            Boolean defaultValue = var.getDefaultValue();
            if (defaultValue == null || defaultValue == false) {
                return False.FALSE;
            } else {
                return True.TRUE;
            }
        }

        public Var getVar() {
            return var;
        }

        public LinkedHashSet<Imp> getSimpleImpls() {
            return simpleImpls;
        }

        private CalculatedValue initCalculatedValue() {
            return new CalculatedValue(computeTest(), Bit.TRUE, computeDefaultValue(), Bit.OPEN);
        }

        public CalculatedValue getCalculatedValue() {
            return calculatedValue;
        }

        public Tri eval(EvalContext ctx) {
            return calculatedValue.eval(ctx);
        }

        public Collection<Var> getImplicants() {
            return getTestVars();
        }
    }

    public boolean shouldClauseBeRemoved(BoolExpr topLevelExpr) {
        if (!topLevelExpr.isImp()) return false;
        Imp e = topLevelExpr.asImp();
        if (!e.getExpr2().isVar()) return false;
        Var var = e.getExpr2().asVar();
        return map.containsKey(var) || nonPngs.containsKey(var);
    }


    public boolean shouldClauseBeKept(BoolExpr topLevelExpr) {
        return !shouldClauseBeRemoved(topLevelExpr);
    }

    public boolean addIfClauseShouldBeKept(BoolExpr clause, Set<BoolExpr> set) {

        if (shouldClauseBeKept(clause)) {
            return set.add(clause);
        } else {
            removedClauses.add(clause);
            return false;
        }
    }

    /**
     * Pre-condition, call flatten implications fist
     *
     * @return null for false else a set of top-level implyClausesThatContainVarOnlyOnRightSide
     */
    public static LinkedHashSet<Imp> isFixupVar(Var var, MasterConstraint constraint) {

//        if (!var.isDerived()) return null;

        LinkedHashSet<Imp> implyClausesThatContainVarOnlyOnRightSide = null;

        for (BoolExpr e : constraint.getExpressions()) {
            if (e.containsDeep(var)) {
                if (!e.isImp()) return null;
                boolean appearsOnLeft = e.getExpr1().containsDeep(var);
                if (appearsOnLeft) return null;

                boolean appearsOnRightAsSimpleVar = e.getExpr2().equals(var);
                if (!appearsOnRightAsSimpleVar) return null;

                //appears only on right, good so far

                if (implyClausesThatContainVarOnlyOnRightSide == null) {
                    implyClausesThatContainVarOnlyOnRightSide = new LinkedHashSet<Imp>();
                }

                implyClausesThatContainVarOnlyOnRightSide.add(e.asImp());
            }
        }

        if (implyClausesThatContainVarOnlyOnRightSide == null || implyClausesThatContainVarOnlyOnRightSide.isEmpty()) {
            throw new IllegalStateException("var[" + var + "] is a not contained in the constraint");
        } else {
            return implyClausesThatContainVarOnlyOnRightSide;
        }
    }

    public static LinkedHashSet<Iff> isFixupVar2(Var var, MasterConstraint constraint) {

        if (!var.isDerived()) return null;

        LinkedHashSet<Iff> iffClausesThatContainVarVar = new LinkedHashSet<Iff>();

        for (BoolExpr e : constraint.getExpressions()) {
            if (e.containsDeep(var)) {
                if (!e.isIff()) return null;
                iffClausesThatContainVarVar.add(e.asIff());
            }
        }

        return iffClausesThatContainVarVar;
    }

    public Tri evalVar(Var var, EvalContext ctx) {
        FixupVar fixup = map.get(var);
        return fixup.eval(ctx);
    }

    public boolean isFixupVar(Var var) {
        return map.containsKey(var);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FixupVars fixupVars = (FixupVars) o;

        if (!map.equals(fixupVars.map)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return map.keySet().hashCode();
    }

    public Set<Var> getFixupVars() {
        return map.keySet();
    }

    @Override
    public String toString() {
        return getFixupVars().toString();
    }

    public void addFixupVarIfAppropriate(Var var, MasterConstraint constraint) {
        LinkedHashSet<Imp> simpleImpls = FixupVars.isFixupVar(var, constraint);
        if (simpleImpls != null) {
            addFixupVar(var, simpleImpls);
        }
    }


}
