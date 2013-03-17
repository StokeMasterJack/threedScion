package c3i.featureModel.shared.node;

import c3i.featureModel.shared.AutoAssignContext;
import c3i.featureModel.shared.Bit;
import c3i.featureModel.shared.Tri;
import c3i.featureModel.shared.VarComparator;
import c3i.featureModel.shared.VarSpace;
import c3i.featureModel.shared.VarStates;
import c3i.featureModel.shared.boolExpr.AssignmentException;
import c3i.featureModel.shared.boolExpr.BoolExpr;
import c3i.featureModel.shared.boolExpr.ConflictingAssignmentException;
import c3i.featureModel.shared.boolExpr.CspFailure;
import c3i.featureModel.shared.boolExpr.Formula;
import c3i.featureModel.shared.boolExpr.NegatingVarsException;
import c3i.featureModel.shared.boolExpr.SimplifiedToFalse;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SimplePicks;
import c3i.featureModel.shared.search.ForEachSolutionSearch;
import c3i.featureModel.shared.search.IsSatSearch;
import c3i.featureModel.shared.search.ProductCountSearch;
import c3i.featureModel.shared.search.ProductHandler;
import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * A simple copy-on-branch csp node
 */
public class Csp implements AutoAssignContext, SimplePicks {

    private static final VarComparator VAR_COMPARATOR = new VarComparator();

    //shallow copy
    private final CspContext context;

    //deep copy
    protected final Bit[] assignments;   //index is var.index
    private BoolExpr[] constraints;
    private LinkedList<Var> openOutVars;


    private CspFailure cspFailure;
    private int clauseCount;

    //do not copy queue - all csp's start out clean - can not copy a dirty csp
    private final LinkedList<Var> dirtyQueue = new LinkedList<Var>();

    /**
     * Create new top-level csp node
     */
    public Csp(CspContext context) {
        this.context = context;
        this.constraints = initConstraints(context.getConstraints());
        this.assignments = initAssignments(context.getVarCount());
        this.openOutVars = initOpenOutVars(context.getVarSpace());
        clauseCount = constraints.length;
    }

    /**
     * Refinement
     * @param parent
     */
    public Csp(Csp parent) {
        assert parent.isOpen();

        //shallow copy
        this.context = parent.context;

        //deep copy constraints
        this.clauseCount = parent.clauseCount;

        int nullClauseCount = parent.constraints.length - parent.clauseCount;

        if (nullClauseCount > 10) {
            this.constraints = new BoolExpr[parent.clauseCount];
            int j = 0;
            for (int i = 0; i < parent.constraints.length; i++) {
                if (parent.constraints[i] != null) {
                    this.constraints[j] = parent.constraints[i];
                }
            }
        } else {
            this.constraints = new BoolExpr[parent.constraints.length];
            System.arraycopy(parent.constraints, 0, this.constraints, 0, constraints.length);
        }


        //deep copy assignments
        this.assignments = new Bit[parent.assignments.length];
        System.arraycopy(parent.assignments, 0, this.assignments, 0, assignments.length);

        //deep copy assignments
        this.openOutVars = (LinkedList<Var>) parent.openOutVars.clone();

    }

    /**
     * non-refinement
     */
    public Csp(Csp parent, Var var, boolean value) {
        this(parent);
        assign(var, value);
        maybeSimplify();
    }


    public Var decide() {
        return openOutVars.removeFirst();
    }

    private void dirty(BoolExpr simple) {
        if (simple.isVar()) {
            dirtyQueue.add(simple.asVar());
        } else {
            simple.asNot().getExpr().asVar();
        }
    }

    private void dirty(Var var) {
        dirtyQueue.add(var);
    }

    public boolean isDirty() {
        return !dirtyQueue.isEmpty();
    }

    public boolean isOpen() {
        if (constraints == null) {
            return false;
        } else {
            assert constraints.length != 0;
            return true;
        }
    }

    public boolean isTrue() {
        return clauseCount == 0;
    }

    public boolean isFalse() {
        return cspFailure != null;
    }

    private boolean isConstant() {
        return !isOpen();
    }

    private static Bit[] initAssignments(int varCount) {
        Bit[] assignments = new Bit[varCount];
        for (int i = 0; i < assignments.length; i++) {
            assignments[i] = Bit.OPEN;
        }
        return assignments;
    }

    private static LinkedList<Var> initOpenOutVars(VarSpace varSpace) {
        LinkedList<Var> openOutVars = Lists.newLinkedList(varSpace);
        Collections.sort(openOutVars, VAR_COMPARATOR);
        return openOutVars;
    }

    /**
     * @return true is this dirtied the assignments - a new assignment
     */
    public boolean assign(Var var, boolean newValue) throws ConflictingAssignmentException {
        int varIndex = var.getIndex();
        Bit currentValue = assignments[varIndex];

        Bit proposedValue = Bit.fromBool(newValue);

        if (currentValue.isOpen()) {
            assignments[varIndex] = proposedValue;
            dirty(var);
            return true;
        } else {
            if (currentValue == proposedValue) {
                //no action
                System.out.println("Dup Assignment");
                return false;
            } else {
                throw new ConflictingAssignmentException(var, newValue, this);
            }
        }

    }

    @Override
    public AutoAssignContext copy() {
        return new Csp(this);
    }

    public void pop(Var var) {
        assert isTrue(); //we should be in dontCare mode
        int varIndex = var.getIndex();
        Bit currentValue = assignments[varIndex];
        assert currentValue.isTrue();
        assignments[varIndex] = Bit.FALSE;
    }


    public boolean assignTrue(Var var) throws ConflictingAssignmentException {
        return assign(var, true);
    }

    public boolean assignTrue(Collection<Var> picks) throws ConflictingAssignmentException {
        boolean anyChange = false;
        for (Var pick : picks) {
            boolean ch = assign(pick, true);
            if (ch) anyChange = true;
        }
        return anyChange;
    }

    public boolean assignFalse(Var var) throws ConflictingAssignmentException {
        return assign(var, false);
    }

    public Bit getValue(Var var) {
        return assignments[var.getIndex()];
    }

    public boolean isPicked(Var var) {
        return isTrue(var);
    }

    public boolean isAssigned(Var var) {
        return !isOpen(var);
    }

    public boolean isTrue(Var var) {
        return assignments[var.getIndex()].isTrue();
    }

    public boolean isTrue(int varIndex) {
        return assignments[varIndex].isTrue();
    }

    public boolean isFalse(Var var) {
        return assignments[var.getIndex()].isFalse();
    }

    public boolean isFalse(int varIndex) {
        return assignments[varIndex].isFalse();
    }

    public boolean isOpen(Var var) {
        return assignments[var.getIndex()].isOpen();
    }

    public boolean isOpen(int varIndex) {
        return assignments[varIndex].isOpen();
    }

    public VarSpace getAllVars() {
        return context.getVarSpace();
    }


    public boolean isComplete() {
        for (int i = 0; i < assignments.length; i++) {
            if (assignments[i].isOpen()) return false;
        }
        return true;
    }

//    public Set<Var> getCareVars() {
//        LinkedHashSet<Var> a = new LinkedHashSet<Var>();
//        for (int i = 0; i < constraints.length; i++) {
//            BoolExpr e = constraints[i];
//            a.addAll(e.getCareVars());
//        }
//        return a;
//    }

    public Set<Var> getTrueVars() {
        final LinkedHashSet<Var> set = new LinkedHashSet<Var>();
        for (int i = 0; i < context.getVarCount(); i++) {
            Var var = context.getVar(i);
            if (assignments[i].isTrue() && var.isLeaf()) {
                set.add(var);
            }
        }
        return set;
    }

    private static BoolExpr[] initConstraints(Collection<BoolExpr> expressions) {
        if (expressions == null || expressions.isEmpty()) {
            return null;
        }

        BoolExpr[] a = new BoolExpr[expressions.size()];
        Iterator<BoolExpr> it = expressions.iterator();
        for (int i = 0; i < a.length; i++) {
            a[i] = it.next();
        }

        return a;
    }


    public void simplify(int i) {
        BoolExpr before = constraints[i];
        assert before != null;

        BoolExpr after = before.simplify(this);
        assert after != null;

        boolean ch = before != after;

        if (after.isTrue()) {
            constraints[i] = null;
            clauseCount--;
        } else if (after.isFalse()) {
            throw new AssignmentException(before, false, this);
        } else {
            Var var = after.getVarIfSimple();
            if (var != null) {
                //yes simple
                constraints[i] = null;
                clauseCount--;
                dirty(var);
            } else {
                if (ch) {
                    //dirty - complex
                    constraints[i] = after;
                } else {
                    //clean - complex
                }
            }
        }

    }


    public boolean assignmentsEquals(Csp that) {
        if (this == that) return true;
        if (that == null) return false;
        return Arrays.deepEquals(this.assignments, that.assignments);
    }

    public int assignmentsHashCode() {
        return Arrays.deepHashCode(assignments);
    }

    public void propagate() {
        maybeSimplify();
    }

    public void maybeSimplify() {
        try {
            while (isDirty()) {
                Var var = dirtyQueue.remove();
                propagate2(var);
                simplify();
            }

        } catch (AssignmentException e) {
            setFalse(e);
        }
    }

    private void propagate1(Var var) {
        for (int i = 0; i < constraints.length; i++) {
            BoolExpr constraint = constraints[i];
            if (constraint.containsVar(var)) {
                Tri v = constraints[i].eval(this);
            }
        }
    }

    private void propagate2(Var var) {
        for (int i = 0; i < constraints.length; i++) {
            BoolExpr constraint = constraints[i];
            if (constraint.containsVar(var)) {
                constraints[i].autoAssignTrue(this);
            }
        }
    }

    public void simplify() {
        try {
            simplify(this);
        } catch (NegatingVarsException e) {
            setFalse(e);
        } catch (AssignmentException e) {
            setFalse(e);
        }
    }

    private void simplify(AutoAssignContext ctx) throws NegatingVarsException, AssignmentException {
        if (isConstant()) return;
        assert constraints.length > 0;

        for (int i = 0; i < constraints.length; i++) {
            simplify(i);
        }

        if (clauseCount == 0) {
            setTrue();
        }

    }

    private void setTrue() {
        assert isOpen();
        constraints = null;
    }


    public void setFalse(CspFailure cspFailure) {
        assert isOpen();
        this.cspFailure = cspFailure;
        constraints = null;
    }

    private void setFalse(BoolExpr before) {
        setFalse(new SimplifiedToFalse(before));
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

    public void test1() throws Exception {
        ForEachSolutionSearch search = new ForEachSolutionSearch();
        search.setProductHandler(new ProductHandler() {
            @Override
            public void onProduct(SimplePicks product) {
                System.out.println(111);
            }
        });

        forEachSolution(search);

    }
//
//    public <T> T search(Search<T> search) {
//        return search.onNode(0, this);
//    }

    public void forEachSolution(ForEachSolutionSearch search) {
        ProductCountSearch productCounter = new ProductCountSearch();
        productCounter.setOutVars(search.getOutVars());
        productCounter.start(this);
    }

    public void forEachProduct(ProductHandler productHandler) {
        this.forEachProduct(productHandler, null);
    }

    public void forEachProduct(ProductHandler productHandler, Collection<Var> outVars) {
        ForEachSolutionSearch search = new ForEachSolutionSearch();
        search.setOutVars(outVars);
        search.setProductHandler(productHandler);
        search.start(this);
    }

    public boolean isSat() {
        if (isTrue()) {
            return true;
        } else if (isFalse()) {
            return false;
        } else {
            IsSatSearch search = new IsSatSearch();
            search.start(this);
            return search.isSat();
        }
    }

    /**
     * A node is a solution if it is: (outComplete and (true or sat))
     *
     * Note: a Solution may contain dontCares
     *
     *
     *
     */
    public boolean isSolution() {
        return isTrue() || isProduct();
    }

    /**
     * Note the subtle difference between isProduct() and  isSolution()
     * a Product will never contain dontCares
     */
    public boolean isProduct() {
        return isOutComplete() && isSat();
    }

    public boolean isOutComplete() {
        return openOutVars.isEmpty();
    }

    public void print() {
        print("DF");
    }


    public void print(String prefix) {
        if (isTrue()) {
            System.err.println(prefix + "Constraint: True");
        } else if (isFalse()) {
            System.err.println(prefix + "Constraint: False");
        } else {
            System.out.println(prefix + "Constraint: ");
            for (int i = 0; i < constraints.length; i++) {
                BoolExpr e = constraints[i];
                System.err.println(prefix + "\t " + e);
            }
        }
    }

    public LinkedList<Var> getOpenOutVars() {
        return openOutVars;
    }

    public int getOpenOutVarCount() {
        return openOutVars.size();
    }

    public Set<Var> getAllVarsAsSet() {
        HashSet<Var> all = new HashSet<Var>();
        for (int i = 0; i < context.size(); i++) {
            all.add(context.getVar(i));

        }
        return all;
    }

    public void flattenTopLevelImplications() {
        constraints = Formula.flattenTopLevelImplications(constraints);
    }

    public void flattenTopLevelConflicts() {
        constraints = Formula.flattenTopLevelConflicts(constraints);
    }

//    public Csp reduce(String... varCodes) {
//
//        HashSet<Var> picks = new HashSet<Var>();
//
//        for (String varCode : varCodes) {
//            Var var = varSpace.getVar(varCode);
//            picks.add(var);
//        }
//
//        return reduce(picks);
//
//    }
//
//    public Csp reduce(Set<Var> picks) {
//        Csp cp = new Csp(this,);
//        for (Var pick : picks) {
//            cp.assignTrue(pick);
//        }
//        cp.propagateSimplify();
//        return cp;
//    }

    public void fillInInitialPicks() {
        for (int i = 0; i < context.size(); i++) {
            if (assignments[i].isOpen()) {
                Var var = context.getVar(i);
                if (var.isInitiallyPicked()) {
                    assignTrue(var);
                } else {
                    assignFalse(var);
                }
            }
        }
    }


    public void dumpVars(String prefix) {

        System.err.println(prefix + "Var State:");

        VarStates vsN = new VarStates(this, VarStates.Filter.NonOutputVarsOnly);
        VarStates vsO = new VarStates(this, VarStates.Filter.OutputVarsOnly);
        VarStates vsA = new VarStates(this, VarStates.Filter.AllVars);

        System.err.println(prefix + "\t Output Vars: ");
        vsO.print(prefix + "\t\t");

        System.err.println(prefix + "\t Non-output Vars: ");
        vsN.print(prefix + "\t\t");

        System.err.println(prefix + "\t All Vars: ");
        vsA.print(prefix + "\t\t");
    }

    public Set<Var> getOpenVars() {
        return getVarsWithValue(Bit.OPEN);
    }

    public Set<Var> getVarsWithValue(Bit val) {
        VarSpace varSpace = context.getVarSpace();
        final LinkedHashSet<Var> set = new LinkedHashSet<Var>();
        for (int i = 0; i < context.size(); i++) {
            if (assignments[i].equals(val)) {
                set.add(varSpace.getVar(i));
            }
        }
        return set;
    }

    public boolean isOutVar(Var var) {
        return openOutVars.contains(var);
    }

    public Var getVar(int varIndex) {
        return context.getVar(varIndex);
    }

    public boolean assignTrue(String... vars) {
        boolean anyChange = false;
        for (String code : vars) {
            Var var = context.getVar(code);
            boolean ch = assignTrue(var);
            if (ch) {
                anyChange = true;
            }
        }
        return anyChange;
    }

    public void autoAssignTrue(AutoAssignContext ctx) {
        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] != null) {
                constraints[i].autoAssignTrue(this);
            }
        }
    }

    public BoolExpr[] getExpressions() {
        return constraints;
    }


    public long getProductCount() {
        return getProductCount(null);

    }

    public long getProductCount(Collection<Var> outVars) {
        ProductCountSearch search = new ProductCountSearch();
        search.setOutVars(outVars);
        search.start(this);

        return search.getProductCount();
    }

    @Override
    public boolean isValidBuild() {
        return true;
    }
}
