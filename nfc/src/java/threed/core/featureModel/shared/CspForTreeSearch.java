package threed.core.featureModel.shared;

import threed.core.featureModel.shared.boolExpr.MasterConstraint;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.featureModel.shared.search.decision.Decisions;
import threed.core.featureModel.shared.search.decision.SimpleDecisions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

public class CspForTreeSearch extends Csp<AssignmentsForTreeSearch, CspForTreeSearch> {

    private final AssignmentsForTreeSearch assignments;
    private final OpenVars openVars;

    public CspForTreeSearch(Vars vars, MasterConstraint constraint, Collection<Var> outputVars) {
        super(vars, constraint);

        Comparator<Var> varComparator = new VarComparator();


        if (outputVars != null) {

            ArrayList<Var> vars1 = new ArrayList<Var>(outputVars);
            ArrayList<Var> vars2 = new ArrayList<Var>();

            for (int i = 0; i < vars.size(); i++) {
                Var var = vars.get(i);
                if (!vars1.contains(var)) {
                    vars2.add(var);
                }
            }

            Collections.sort(vars1, varComparator);
            Collections.sort(vars2, varComparator);


            this.openVars = new OpenVars(vars1, vars2);

        } else {

            ArrayList<Var> vars1 = new ArrayList<Var>();
            for (int i = 0; i < vars.size(); i++) {
                Var var = vars.get(i);
                vars1.add(var);
            }


            Collections.sort(vars1, varComparator);

            this.openVars = new OpenVars(vars1);
        }


        this.assignments = new AssignmentsForTreeSearch(vars, openVars);
    }

    public CspForTreeSearch(Vars vars, MasterConstraint constraint) {
        this(vars, constraint, null);
    }

    public CspForTreeSearch(CspForTreeSearch that) {
        super(that);
        openVars = that.openVars.copy();
        this.assignments = that.assignments.copy(openVars);
    }


    @Override
    public AssignmentsForTreeSearch getAssignments() {
        return assignments;
    }

    @Override
    public CspForTreeSearch copy() {
        return new CspForTreeSearch(this);
    }

    public List<Var> getOpenOutputVars() {
        return assignments.getOpenVars1();
    }

    @Override
    public String toString() {
        return "openVars: " + getOpenOutputVarCount() + ":" + getOpenOutputVars();
    }

    public Set<Var> getTrueOutputVars() {
        return getAssignments().getTrueOutputVars();
    }

    public int getOpenOutputVarCount() {
        return assignments.getOpenOutputVarCount();
    }


    public Collection<Var> getOutputVars() {
        return assignments.getVars1();
    }

    public boolean isSolved1() {
        return assignments.isSolved1();
    }

    public OpenVars getOpenVars() {
        return getAssignments().getOpenVars();
    }

    public Decisions decide2() {
        if(true) throw new UnsupportedOperationException();
        Var var = getNextDecisionVar();
        if (var == null) return null;
        else return new SimpleDecisions(var);
//        Decisions decisions = assignments.decide();
//        return decisions;
    }

    public Decisions decide() {
        Var var = openVars.getNextDecisionVar();
        if (var == null) return null;
        else return new SimpleDecisions(var);
//        Decisions decisions = assignments.decide();
//        return decisions;
    }

    private Var getNextDecisionVar() {

        Var maxVar = null;
        int maxConflictCount = 0;

        List<Var> openVars11 = assignments.getOpenVars1();

        for (Var var : openVars11) {
            if (var.isModelCodeXorChild()) return var;
            if (var.isInteriorColorXorChild()) return var;
            if (var.isExteriorColorXorChild()) return var;
            if (var.isXorChild()) return var;

            if (maxVar == null) {
                maxVar = var;
                maxConflictCount = getConflictCountForVar(var);
            } else {
                int conflictCount = getConflictCountForVar(var);
                if (conflictCount > maxConflictCount) {
                    maxVar = var;
                    maxConflictCount = conflictCount;
                }
            }
        }


        return maxVar;
    }




    public class VarComparator implements Comparator<Var> {

        @Override
        public int compare(Var v1, Var v2) {

            boolean isModelCode1 = v1.isModelCodeXorChild();
            boolean isModelCode2 = v2.isModelCodeXorChild();
            if (isModelCode1 && !isModelCode2) return -1;
            else if (!isModelCode1 && isModelCode2) return 1;
            else if (isModelCode1 && isModelCode2) return 0;


            boolean isExtColor1 = v1.isExteriorColorXorChild();
            boolean isExtColor2 = v2.isExteriorColorXorChild();
            if (isExtColor1 && !isExtColor2) return -1;
            if (!isExtColor1 && isExtColor2) return 1;
            if (isExtColor1 && isExtColor2) return 0;


            boolean isIntColor1 = v1.isInteriorColorXorChild();
            boolean isIntColor2 = v2.isInteriorColorXorChild();
            if (isIntColor1 && !isIntColor2) return -1;
            if (!isIntColor1 && isIntColor2) return 1;
            if (isIntColor1 && isIntColor2) return 0;

            boolean xorChild1 = v1.isXorChild();
            boolean xorChild2 = v2.isXorChild();
            if (xorChild1 && !xorChild2) {
                return -1;
            } else if (!xorChild1 && xorChild2) {
                return 1;
            } else if (xorChild1 && xorChild2) {

                int xorSiblingCount1 = v1.getParent().getChildCount();
                int xorSiblingCount2 = v2.getParent().getChildCount();

                if (xorSiblingCount1 < xorSiblingCount2) {
                    return 1;
                } else if (xorSiblingCount1 > xorSiblingCount2) {
                    return -1;
                } else {

                    String parentName1 = getParentName(v1);
                    String parentName2 = getParentName(v2);

                    return parentName1.compareTo(parentName2);
                }

            } else {
                return 0;
//                Integer conflictCount1 = getConflictCountForVar(v1);
//                Integer conflictCount2 = getConflictCountForVar(v2);
//                return conflictCount2.compareTo(conflictCount1);
            }
        }

        public String getParentName(Var var) {
            return var.isRoot() ? "NoParent" : var.getParent().getName();
        }

        public void printVarSort(List<Var> vars, Collection<Var> outputVars) {

            System.err.println("Var sort:");
            for (Var var : vars) {

                boolean png = outputVars != null && outputVars.contains(var);
                boolean xorChild = var.isXorChild();
                int siblingCount = xorChild ? var.getParent().getChildCount() : 0;

                String sPng = png ? "yPng" : "nPng";
                String sXorChild = xorChild ? "yXorChild" : "nXorChild";

                String sSiblingCount = xorChild ? siblingCount + "SiblingCount" : "NSiblingCount";

                String parentName = var.isRoot() ? "NoParent" : getParentName(var);

                System.err.println("\t " + sPng + "\t" + sXorChild + "\t" + sSiblingCount + "\t" + parentName + "\t" + var.getCode());
            }

        }

//        public void printVarSort(List<Var> vars) {
//            printVarSort(vars, null);
//        }


    }

    public class VarComparator3 extends VarComparator {

        @Override
        public String getParentName(Var var) {
            return getParentNamePrefix(var) + super.getParentName(var);
        }

        String getParentNamePrefix(Var var) {
            int conflictCount;
            if (var.isModelCodeXorChild()) {
                conflictCount = getConflictCountForModelCode(var);
            } else {
                conflictCount = getConflictCountForVar(var);
            }

            return (99 - conflictCount) + "-";
        }
    }

    public boolean anyOpenOutputVars() {
        return getAssignments().anyOpenVars1();
    }

    public Map<Integer, Set<Var>> getConflictCounts() {

        TreeMap<Integer, Set<Var>> map = new TreeMap<Integer, Set<Var>>();
        for (Var var : getAllVarsAsSet()) {
            if (var.hasChildVars()) continue;
            if (var.isXorChild()) continue;
            int conflictCount = getConflictCountForVar(var);
            Set<Var> vars = map.get(conflictCount);
            if (vars == null) {
                vars = new HashSet<Var>();
                map.put(conflictCount, vars);
            }
            vars.add(var);
        }

        return map;
    }

    private int getConflictCountForVar(Var var) {
        return constraint.getConflictCountForVar(var);
    }

    private int getConflictCountForModelCode(Var var) {
        throw new UnsupportedOperationException();
//        int t = 0;
//        assert var.isModelCodeXorChild();
//
//        Collection<BoolExpr> expressions = constraint.getExpressions();
//        for (BoolExpr e : expressions) {
//            if (e.isIff() && e.getExpr1().equals(var) && e.getExpr2().isAnd()) {
//                for (BoolExpr ee : e.getExpr2().asAnd().getExpressions()) {
//                    if (ee.isVar()) {
//                        t += getConflictCountForVar(ee.asVar());
//                    }
//                }
//            }
//        }
//        return t;
    }

}
