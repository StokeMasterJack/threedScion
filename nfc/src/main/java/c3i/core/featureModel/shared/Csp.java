package c3i.core.featureModel.shared;

import c3i.core.featureModel.shared.boolExpr.AssignmentException;
import c3i.core.featureModel.shared.boolExpr.BoolExpr;
import c3i.core.featureModel.shared.boolExpr.MasterConstraint;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.ProductHandler;
import c3i.core.featureModel.shared.search.TreeSearch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

abstract public class Csp<A extends Assignments, C extends Csp> implements AutoAssignContext {

    //shallow copy
    protected final Vars vars;
    //try using shallow copy as these are immutable
    protected final MasterConstraint constraint;
    //deep copy


    public Csp(Vars vars, MasterConstraint constraint) {
        this.vars = vars;
        this.constraint = constraint;
    }

    protected Csp(C that) {
        this.vars = that.vars;
        this.constraint = new MasterConstraint(that.constraint);
    }


    abstract public A getAssignments();

    abstract public C copy();

    public boolean isClean() {
        return !isDirty();
    }

    public boolean check() {
        assert constraint.getExprCount() != 0;
        assert constraint.getCareVars().size() != 0;
        return true;
    }

//    public BoolExpr getRootExpr() {
//        return constraint.getConstraints();
//    }

    public MasterConstraint getConstraint() {
        return constraint;
    }

    public boolean isConstant() {
        return constraint.isConstant();
    }


    public VarStates snapVarStates() {
        return null;
//        return assignments.snapVarsStates();
    }

    public Collection<Var> getConstraintSupport() {
        return constraint.getCareVars();
    }

    public int getVarCount() {
        return getConstraintSupport().size();
    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException();
    }

    public boolean isTrue(Var var) {
        return getAssignments().isTrue(var);
    }

    public boolean isTrue() {
        return constraint.isTrue();
    }

    public boolean isFalse() {
        return constraint.isFalse();
    }

    public boolean isOpen() {
        return constraint.isOpen();
    }

    public boolean isFalse(Var var) {
        return getAssignments().isFalse(var);
    }

    public boolean isOpen(Var var) {
        return getAssignments().isOpen(var);
    }

    public boolean isAssigned(Var var) {
        return !isOpen(var);
    }

    public void assignTrue(Var var, int depth) throws AssignmentException {
        getAssignments().assignTrue(var, depth);
    }

    public void assignFalse(Var var, int depth) throws AssignmentException {
        getAssignments().assignFalse(var, depth);
    }

    public void assignTrue(Var var) throws AssignmentException {
        getAssignments().assignTrue(var);
    }

    public void assignFalse(Var var) throws AssignmentException {
        getAssignments().assignFalse(var);
    }

    public void assignTrue(String varCode) throws AssignmentException {
        Var var = vars.get(varCode);
        getAssignments().assignTrue(var);
    }

    public void assignFalse(String varCode) throws AssignmentException {
        Var var = vars.get(varCode);
        getAssignments().assignFalse(var);
    }

    public Bit getValue(Var var) {
        return getAssignments().getValue(var);
    }


//    public void print(String prefix) {
//        getAssignments().dumpVars(prefix);
//    }

//    public void print() {
//        getAssignments().dumpVars("");
//        constraint.print();
//    }

     public void print(String prefix) {
        getAssignments().dumpVars(prefix);
        constraint.print(prefix);
    }

    public void print() {
        print("");
    }

    boolean isDirty() {
        return constraint.isDirty() || getAssignments().isDirty();
    }

    public void clean() {
        getAssignments().clean();
        constraint.clean();
    }

    public void propagate() throws AssignmentException {
        propagate(0);
    }

    private void propagate(int count) throws AssignmentException {
//        System.err.println();
//        System.err.println("propagateCount: " + count);

        clean();
        constraint.autoAssignTrue(getAssignments());
        if (isDirty()) {
            propagate(count + 1);
        }
    }

    /**
     * If simplify is used in a tree search. It's meaning must be interpreted correctly:
     * <p/>
     * If simplify reduces the constraint to TRUE
     * that does not mean we should stop the search
     * we need to continue walking the tree and finding solutions
     * Why: any as yet unassigned vars become dontCares, and must be iterated
     * However, subsequent actions could vary depending on our goal:
     * If goal os allSat: we must keep on looping
     * If goal is satCount: we can compute the unvisited solutions
     */
    public void simplify() {
        assert constraint.check();
        clean();
        constraint.simplify(getAssignments());
        if (isDirty()) simplify();
    }

    public void propagateSimplify() {
        propagate();
        simplify();
    }

    public void beforeAutoAssignTrue(BoolExpr exprBefore) {
        snap(exprBefore, "beforeAutoAssignTrue");
    }

    public void afterAutoAssignTrue(BoolExpr exprAfter) {
        snap(exprAfter, "afterAutoAssignTrue");
    }

    public void beforeAutoAssignFalse(BoolExpr exprBefore) {
        snap(exprBefore, "beforeAutoAssignFalse");
    }

    public void afterAutoAssignFalse(BoolExpr exprAfter) {
        snap(exprAfter, "afterAutoAssignFalse");
    }

    public void snap(BoolExpr expr, String msg) {
        throw new UnsupportedOperationException();
    }

//    public Tri eval() {
//        return constraint.eval(getAssignments());
//    }


    public Vars getAllVars() {
        return vars;
    }

    public Set<Var> getTrueNonSupportVars() {
        HashSet<Var> set = new HashSet<Var>();
        Collection<Var> support = getConstraintSupport();
        Set<Var> trueVars = getAssignments().getTrueVars();
        for (Var var : trueVars) {
            if (!support.contains(var)) {
                set.add(var);
            }
        }
        return set;
    }


    public Set<Var> getTrueVars() {
        return getAssignments().getTrueVars();
    }

    public void printDecision() {
    }


    public Set<Var> getAllVarsAsSet() {
        HashSet<Var> all = new HashSet<Var>();
        for (int i = 0; i < vars.size(); i++) {
            all.add(vars.get(i));

        }
        return all;
    }

    public void flattenTopLevelImplications() {

        constraint.flattenTopLevelImplications();
    }

    public void flattenTopLevelConflicts() {
        constraint.flattenTopLevelConflicts();
    }

    public void toCnf() {
        constraint.toCnf(getAssignments());
    }

//    public void printConflictMap() {
//        Map<Integer, Set<Var>> conflictCounts = getConflictCounts();
//        for (Integer integer : conflictCounts.keySet()) {
//            System.out.println(integer + " " + conflictCounts.get(integer));
//        }
//    }

    public void fillInInitialPicks() {
        getAssignments().fillInInitialPicks();
    }

    public boolean isSolved() {
        return getAssignments().isSolved();
    }

    public long satCount() {
        if (isTrue()) return 1;
        SatCountProductHandler satCountProductHandler = new SatCountProductHandler();
        findAll(satCountProductHandler);
        return satCountProductHandler.getCount();
    }

    public void findAll(ProductHandler handler) {
        if (this instanceof CspForTreeSearch) {
            if (isTrue()) {
                AssignmentsForTreeSearch  assignments = (AssignmentsForTreeSearch) getAssignments();
                handler.onProduct(assignments);
            } else {
                CspForTreeSearch csp1 = (CspForTreeSearch) this;
                TreeSearch treeSearch = new TreeSearch();
                treeSearch.setProductHandler(handler);
                treeSearch.start(csp1);
            }
        } else {
            throw new IllegalStateException();
        }
    }

     public C reduce(String... varCodes) {

        HashSet<Var> picks = new HashSet<Var>();

        for (String varCode : varCodes) {
            Var var = vars.get(varCode);
            picks.add(var);
        }

        return reduce(picks);

    }

    public C reduce(Set<Var> picks) {
        C cp = copy();
        for (Var pick : picks) {
            cp.assignTrue(pick);
        }
        cp.propagateSimplify();
        return cp;
    }


    public Assignments findFirst() {
        TreeSearch treeSearch = new TreeSearch();

        class Ph implements ProductHandler {

            Assignments product;

            @Override
            public void onProduct(AssignmentsForTreeSearch product) {
                this.product = product;
                throw new RuntimeException("STOP");
            }
        }


        Ph ph = new Ph();

        treeSearch.setProductHandler(ph);
        try {

            CspForTreeSearch cspForTreeSearch;
            if (this instanceof CspForTreeSearch) {
                cspForTreeSearch = (CspForTreeSearch) this;
            } else {
                cspForTreeSearch = new CspForTreeSearch(vars, constraint);
            }

            treeSearch.start(cspForTreeSearch);
            return null;
        } catch (Exception e) {
            return ph.product;
        }


    }


}