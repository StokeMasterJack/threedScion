package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.boolExpr.MasterConstraint;
import c3i.featureModel.shared.boolExpr.ReassignmentException;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.search.decision.Decision;
import c3i.featureModel.shared.search.decision.Decisions;
import c3i.featureModel.shared.search.decision.SimpleDecisions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

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
        return getTrueVars().toString();
//        return "State:" + getState() + ":openVars:" + getOpenOutputVarCount() + ":" + getOpenOutputVars();
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

    public CspForTreeSearch refine(Decision decision) {

        CspForTreeSearch copy = copy();

        try {
            copy.makeAssignments(decision);
            copy.propagate();
            copy.simplify();

            checkState(!copy.isFailed());
            checkState(!copy.isFalse());

        } catch (AssignmentException e) {
            checkState(copy.isFailed());
            checkState(copy.isFalse());
        }

        return copy;
    }

    public void makeAssignments(Decision decision) throws ReassignmentException {
        checkState(!isFailed());
        try {
            decision.makeAssignment(assignments);
        } catch (AssignmentException e) {
            System.out.println("@@@@@@@");
            this.assignmentException = e;
            throw e;
        }
    }

    public boolean isSolved1() {
        return assignments.isSolved1();
    }

    public OpenVars getOpenVars() {
        return getAssignments().getOpenVars();
    }


    public Decisions decide() {
        Var var = openVars.getNextDecisionVar();
        if (var == null) return null;
        else return new SimpleDecisions(var);
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
                    String parentCode1 = getParentCode(v1);
                    String parentCode2 = getParentCode(v2);
                    return parentCode1.compareTo(parentCode2);
                }

            } else {
                return 0;
//                Integer conflictCount1 = getConflictCountForVar(v1);
//                Integer conflictCount2 = getConflictCountForVar(v2);
//                return conflictCount2.compareTo(conflictCount1);
            }
        }

        public String getParentCode(Var var) {
            boolean r = var.isRoot();
            if (r) {
                return "NoParent";
            } else {
                return var.getParent().getCode();
//                return p.getCode();
            }

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

                String parentName = var.isRoot() ? "NoParent" : getParentCode(var);

                System.err.println("\t " + sPng + "\t" + sXorChild + "\t" + sSiblingCount + "\t" + parentName + "\t" + var.getCode());
            }

        }

//        public void printVarSort(List<Var> vars) {
//            printVarSort(vars, null);
//        }


    }


    public boolean anyOpenOutputVars() {
        return getAssignments().anyOpenVars1();
    }

    public boolean isOutComplete() {
        return !anyOpenOutputVars();
    }

    public String getState() {
        if (isTrue()) {
            return "True";
        } else if (isFalse()) {
            return "False";
        } else if (isOpen()) {
            return "Open";
        } else {
            throw new IllegalStateException();
        }
    }


}
