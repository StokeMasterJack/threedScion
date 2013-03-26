package c3i.featureModel.shared.node;

import c3i.featureModel.shared.AutoAssignContext;
import c3i.featureModel.shared.Bit;
import c3i.featureModel.shared.Tri;
import c3i.featureModel.shared.VarComparator;
import c3i.featureModel.shared.VarStates;
import c3i.featureModel.shared.boolExpr.BoolExpr;
import c3i.featureModel.shared.boolExpr.ConflictingAssignmentException;
import c3i.featureModel.shared.boolExpr.False;
import c3i.featureModel.shared.boolExpr.Formula;
import c3i.featureModel.shared.boolExpr.True;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SimplePicks;
import c3i.featureModel.shared.explanations.Cause;
import c3i.featureModel.shared.search.ForEachSolutionSearch;
import c3i.featureModel.shared.search.IsSatSearch;
import c3i.featureModel.shared.search.ProductCountSearch;
import c3i.featureModel.shared.search.ProductHandler;
import c3i.featureModel.shared.search.Search;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;
import static smartsoft.util.shared.Console.prindent;

/**
 * A simple copy-on-branch csp node
 *
 * OutVars:
 */
public class Csp implements AutoAssignContext, SimplePicks {

    private static final boolean EVAL_MODE = false;

    private static final int MAX_CLAUSE_COUNT = 2000;

    private static final VarComparator VAR_COMPARATOR = new VarComparator();
    private static final String ONLY_OPEN_COMPLEX_OR_NULL_ALLOWED_IN_MASTER_CONSTRAINT_ARRAY = "Only open complex clauses or null are allowed in constraints array.";

    //shallow copy
    private final CspContext context;

    //core csp state assignments + constraints
    //csp core state is deep copied as we walk the search tree
    protected final Bit[] assignments;   //index is var.index

    private BoolExpr[] constraints;   //only valid elements are: open clauses or null

    private int openClauseCount;

    private Search search;

    //do not copy queue - all csp's start out clean - can not copy a dirty csp
    private LinkedList<Var> dirtyQueue = new LinkedList<Var>();

    private LinkedList<Var> openOutVars;

    /**
     * Create a new top-level cspContext
     */
    public Csp(CspContext context) {
        this.context = context;
        this.assignments = initAssignments(context.getVarList());
        this.constraints = initComplexConstraints(context.getComplexConstraints());

        openClauseCount = constraints.length;

        for (BoolExpr simple : context.getSimpleConstraints()) {
            assignSimple(simple, Cause.DECISION);
        }

        processDirtyQueue();
    }

    /**
     * Copy constructor. This constructors copies with no refinement
     */
    public Csp(Csp parent) {
        checkState(parent.isStable());
        checkState(parent.isOpen());
        parent.checkOpenClauseCount();

        //shallow copy
        this.context = parent.context;

        this.openClauseCount = parent.openClauseCount;

        //deep copy constraints from parent
        copyConstraintsFromParent(parent, this);

        //deep copy assignments from parent
        this.assignments = new Bit[parent.assignments.length];
        System.arraycopy(parent.assignments, 0, this.assignments, 0, assignments.length);


        //deep copy openOutVars
        if (parent.openOutVars != null) {
            this.openOutVars = (LinkedList<Var>) parent.openOutVars.clone();
        } else {
            this.openOutVars = null;
        }

    }

    /**
     * This constructors copies AND refines
     *      it adds the new assignment and
     *      calls maybeSimplify()
     */
    public Csp(Csp parent, Var var, boolean value, Cause cause) {
        this(parent);
//        System.out.println("About to assign: " + var + "=" + value);
        assign(var, value, cause);
        processDirtyQueue();
    }


    public void setSearchContext(ImmutableSet<Var> outVars) {
        if (outVars == null) {
            outVars = ImmutableSet.copyOf(context.getVarList());
        }
        Set<Var> openVars = getOpenVars();
        LinkedList<Var> list = Lists.newLinkedList(Sets.intersection(outVars, openVars));
        Collections.sort(list, VAR_COMPARATOR);
    }

    private int getNullClauseCount() {
        int nullCount = 0;
        for (int i = 0; i < constraints.length; i++) {
            if (constraints[i] == null) {
                nullCount++;
            }
        }
        return nullCount;
    }


    public void maybeGcConstraints() {
        int c = getNullClauseCount();
        if (c > 10) {
            int L = constraints.length - c;
            BoolExpr[] newArray = new BoolExpr[L];
            int j = 0;
            for (int i = 0; i < constraints.length; i++) {
                if (constraints[i] != null) {
                    newArray[j] = constraints[i];
                }
            }
            constraints = newArray;
        }
    }

    public void checkStable() throws IllegalStateException {
        checkState(isStable());
    }

    private static void copyConstraintsFromParent(Csp parent, Csp child) {
//        parent.maybeGcConstraints();
        int L = parent.constraints.length;
        child.constraints = new BoolExpr[L];
        System.arraycopy(parent.constraints, 0, child.constraints, 0, L);
    }

    private void dirty(Var var) {
        dirtyQueue.add(var);
    }

    public boolean isDirty() {
        return !isStable();
    }

    public boolean isStable() {
        if (isConstant()) {
            return true;
        } else {
            return dirtyQueue.isEmpty();
        }
    }

    private int simplifyLite() {

        int openClauseCount = 0;
        int trueClauseCount = 0;
        int falseClauseCount = 0;

        if (constraints == null) {
            return 0;
        } else {
            int occ = 0;
            for (BoolExpr constraint : constraints) {
                if (constraint != null && constraint.isOpen()) {
                    if (constraint.isOpen()) {
                        occ++;
                    }
                }
            }
            return occ;
        }
    }

    private void analyseCspFailure(Object cspFailure) {
        if (cspFailure instanceof Var) {
//            System.out.println("Conflicting Var Assignment: " + cspFailure);
        } else if (cspFailure instanceof BoolExpr) {
//            System.out.println("Clause failed: " + cspFailure);
        } else {
            throw new IllegalStateException();
        }
    }

    private void removeClause(int i) {
        BoolExpr before = constraints[i];
        assert before != null;
        constraints[i] = null;
        openClauseCount--;
        if (openClauseCount == 0) {
            cspSolved();
        }
    }

    private void cspSolved() {
        assert openClauseCount == 0;
        constraints = null;   //cspSolved
        dirtyQueue = null;
    }

    private void cspFailed(Object cspFailure) {
        assert isOpen();
        this.openClauseCount = -1;
        constraints = null;
        dirtyQueue = null;
        analyseCspFailure(cspFailure);
    }


    public boolean isFalse() {
        if (openClauseCount == -1) {
            checkState(constraints == null);
            return true;
        } else {
            boolean t = openClauseCount == 0 && constraints == null;
            boolean o = openClauseCount > 0 && constraints != null;
            checkState(t || o);
            return false;
        }
    }

    public boolean isOpen() {
        if (openClauseCount > 0) {
            checkState(constraints != null);
            return true;
        } else {
            checkState(constraints == null, "Expecting null constraints because clause openClauseCount is " + openClauseCount);
            checkState(openClauseCount == 0 || openClauseCount == -1);
            return false;
        }
    }

    public boolean isTrue() {
        if (openClauseCount == 0) {
            checkState(constraints == null);
            return true;
        } else {
            boolean f = openClauseCount == -1 && constraints == null;
            boolean o = openClauseCount > 0 && constraints != null;
            checkState(f || o);
            return false;
        }
    }

    private boolean isConstant() {
        return !isOpen();
    }

    /**
     * Note the subtle difference between isProduct() and  isSolution()
     * a Product will never contain dontCares
     */
    public boolean isProduct() {
        return isOutComplete() && isSat();
    }

    /**
     * A node is a solution if it is: (outComplete and (true or sat))
     *
     * Note: a Solution may contain dontCares
     */
    public boolean isSolution() {
        return isTrue() || isProduct();
    }


    private static Bit[] initAssignments(ImmutableList<Var> vars) {
        Bit[] a = new Bit[vars.size()];
        for (int i = 0; i < vars.size(); i++) {
            a[i] = Bit.OPEN;
        }
        return a;
    }

    private static BoolExpr[] initComplexConstraints(ImmutableList<BoolExpr> complexConstraints) {
        int size = complexConstraints.size();
        BoolExpr[] a = new BoolExpr[size];
        complexConstraints.toArray(a);
        return a;
    }

//    public void setOpenOutVars(Collection<Var> outVars) {
//        LinkedList<Var> openOutVars = Lists.newLinkedList(outVars);
//        Collections.sort(openOutVars, VAR_COMPARATOR);
//        return openOutVars;
//    }

    /**
     * TODO make sure this works correctly with deeply nested Not's
     * @param simple
     */
    public void assignSimple(BoolExpr simple, Cause cause) {
        if (simple.isVar()) {
            assign(simple.asVar(), true, cause);
        } else {
            Var var = simple.getVarIfSimple();
            assign(var, false, cause);
        }

    }

    /**
     * @return true is this dirtied the assignments - a new assignment
     */
    public void assign(Var var, boolean newValue, Cause cause) {
        int varIndex = var.getIndex();
        Bit currentValue = assignments[varIndex];
        Bit proposedValue = Bit.fromBool(newValue);
        if (currentValue.isOpen()) {
            assignments[varIndex] = proposedValue;
            if (cause.isInference()) {
                openOutVarsRemove(var);
            }
            dirty(var);
        } else {
            if (currentValue == proposedValue) {
                //no action
                System.out.println("Dup Assignment: " + var + "=" + proposedValue);
            } else {
                cspFailed(var);
            }
        }
    }

    protected void openOutVarsRemove(Var var) {
        openOutVars.removeFirst();
    }


    public void pop(Var var) {
        assert isTrue(); //we should be in dontCare mode
        int varIndex = var.getIndex();
        Bit currentValue = assignments[varIndex];
        assert currentValue.isTrue();
        assignments[varIndex] = Bit.FALSE;
    }


    public void assignTrue(Collection<Var> picks, Cause cause) throws ConflictingAssignmentException {
        for (Var pick : picks) {
            assign(pick, true, cause);
        }
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


    public boolean assignmentsEquals(Csp that) {
        if (this == that) return true;
        if (that == null) return false;
        return Arrays.deepEquals(this.assignments, that.assignments);
    }

    public int assignmentsHashCode() {
        return Arrays.deepHashCode(assignments);
    }

    public void propagate() {
        processDirtyQueue();
    }

    public void processDirtyQueue() {
        while (isOpen() && isDirty()) {
            Var var = dirtyQueue.remove();
            simplifyClausesContainingVar(var);
        }
    }

    private void simplifyClausesContainingVar(Var var) {
        if (isConstant()) return;

        if (constraints == null) {
            throw new IllegalStateException();
        }

        int L = constraints.length;


        for (int i = 0; i < L; i++) {

            boolean constant = isConstant();

            if (constant) {
                return;
            }


            BoolExpr constraint = constraints[i];

            if (constraint != null) {
                if (constraint.containsVar(var)) {
                    maybeSimplifyClause(i);
                }
            }
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

    private void maybeSimplifyAllClauses() {
        for (int i = 0; i < constraints.length; i++) {
            if (isConstant()) return;
            maybeSimplifyClause(i);
        }
    }

    private void maybeSimplifyClause(int i) {

        BoolExpr before = constraints[i];
        if (before == null) {
            //
        } else if (before.isTrue()) {
            throw new IllegalStateException(ONLY_OPEN_COMPLEX_OR_NULL_ALLOWED_IN_MASTER_CONSTRAINT_ARRAY);
        } else if (before.isFalse()) {
            throw new IllegalStateException(ONLY_OPEN_COMPLEX_OR_NULL_ALLOWED_IN_MASTER_CONSTRAINT_ARRAY);
        } else if (before.isSimple()) {
            String s = "constraints[" + i + "].before was Simple[" + before + "  ] but" + ONLY_OPEN_COMPLEX_OR_NULL_ALLOWED_IN_MASTER_CONSTRAINT_ARRAY;
            throw new IllegalStateException(s);
        } else if (before.isComplex()) {
            BoolExpr after = before.simplify(this);  // Bit after = constraint.eval(ctx);
            if (after.isTrue()) {
                removeClause(i);
            } else if (after.isFalse()) {
                cspFailed(before);
            } else if (after.isSimple()) {
                assignSimple(after, Cause.INFERENCE);
                constraints[i] = null;
                openClauseCount--;
                if (openClauseCount == 0) {
                    cspSolved();
                }
            } else if (after.isComplex()) {
                if (before == after) {
                    //no change to this clause
                } else {
                    constraints[i] = after;
                    //openComplex to simplerOpenComplex
                }
            } else {
                throw new IllegalStateException();
            }
        } else {
            throw new IllegalStateException();
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

    public void forEachSolution(ForEachSolutionSearch search) {
        ProductCountSearch productCounter = new ProductCountSearch();
        productCounter.setOutVars(search.getOutVars());
        productCounter.start(this);
    }

    public void forEachProduct(ProductHandler productHandler) {
        this.forEachProduct(productHandler, null);
    }

    public void forEachProduct(ProductHandler productHandler, ImmutableSet<Var> outVars) {
        this.setSearchContext(outVars);
        ForEachSolutionSearch search = new ForEachSolutionSearch();
        search.setProductHandler(productHandler);
        search.start(this);
    }

    public boolean isSat() {
        search = new IsSatSearch(this);
        if (isTrue()) {
            return true;
        } else if (isFalse()) {
            return false;
        } else {
            IsSatSearch search = new IsSatSearch(this);
            return search.isSat();
        }
    }

    public void print() {
        System.out.println(this);
//        print("");
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

    public Set<Var> getAllVarsAsSet() {
        HashSet<Var> all = new HashSet<Var>();
        for (int i = 0; i < context.getVarCount(); i++) {
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
        ImmutableList<Var> vars = context.getVarList();
        final LinkedHashSet<Var> set = new LinkedHashSet<Var>();
        for (int i = 0; i < vars.size(); i++) {
            if (assignments[i].equals(val)) {
                set.add(vars.get(i));
            }
        }
        return set;
    }

    public Var getVar(int varIndex) {
        return context.getVar(varIndex);
    }

    public void assignTrue(String... vars) {
        for (String code : vars) {
            Var var = context.getVar(code);
            assign(var, true, Cause.USER);
        }
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

    public long getProductCount(ImmutableSet<Var> outVars) {
        this.setSearchContext(outVars);

        ProductCountSearch search = new ProductCountSearch();
        search.setOutVars(outVars);
        search.start(this);
        return search.getProductCount();
    }

//    private Csp createSearchNode(Collection<Var> outVars) {
//        return new SearchNode(outVars);
//    }

    public ImmutableList<Var> getAllVars() {
        return context.getVarList();
    }

    public static class VarReport {

        int varCount;

        int trueCount;
        int falseCount;
        int openCount;

        public VarReport(Csp csp) {
            Bit[] a = csp.assignments;
            this.varCount = a.length;

            CspContext context = csp.getContext();
            checkState(varCount == context.getVarCount());
            final LinkedHashSet<Var> set = new LinkedHashSet<Var>();
            for (int i = 0; i < context.getVarCount(); i++) {
                Bit assignment = a[i];
                if (assignment.isTrue()) trueCount++;
                else if (assignment.isFalse()) falseCount++;
                else if (assignment.isOpen()) openCount++;
                else throw new IllegalStateException();
            }

        }

        public void print() {
            print(0);
        }

        public void print(int depth) {
            prindent(depth, "varCount = " + varCount);
            prindent(depth + 1, "trueCount = " + trueCount);
            prindent(depth + 1, "falseCount = " + falseCount);
            prindent(depth + 1, "openCount = " + openCount);
            prindent();
        }

        public int getTrueCount() {
            return trueCount;
        }

        public int getFalseCount() {
            return falseCount;
        }

        public int getOpenCount() {
            return openCount;
        }

        public int getVarCount() {
            return varCount;
        }
    }

    public CspContext getContext() {
        return context;
    }

    public VarReport getVarReport() {
        return new VarReport(this);
    }

    @Override
    public String toString() {
        if (isTrue()) {
            return True.TRUE.toString();
        } else if (isFalse()) {
            return False.FALSE.toString();
        } else if (isOpen()) {

            StringBuilder sb = new StringBuilder();
            for (BoolExpr constraint : constraints) {
                if (constraint != null) {
                    String str = constraint.toString(this);
                    sb.append(str);
                    sb.append("  ");
                }
            }

            return sb.toString();
        } else {
            throw new IllegalStateException();
        }
    }

    private int computeOpenClauseCount() {
        if (constraints == null) {
            return 0;
        } else {
            int o = 0;
            for (BoolExpr constraint : constraints) {
                if (constraint != null) {
                    if (constraint.isConstant()) {
                        throw new IllegalStateException();
                    }
                    o++;
                }
            }
            return o;
        }
    }

    public void checkOpenClauseCount() throws IllegalStateException {
        if (openClauseCount == -1) {
            checkState(constraints == null);
        } else if (openClauseCount == 0) {
            checkState(constraints == null);
        } else {
            int occ1 = computeOpenClauseCount();
            if (openClauseCount != occ1) {
                throw new IllegalStateException("cache.openClauseCount[" + openClauseCount + "] did not match computed.openClauseCount[" + occ1 + "]");
            }
        }
    }

    public LinkedList<Var> getOpenOutVars() {
        return openOutVars;
    }

    public int getOpenOutVarCount() {
        return openOutVars.size();
    }

    public boolean isOutVar(Var var) {
        return openOutVars.contains(var);
    }

    /**
     * @return true if all outVars are assigned
     */
    public boolean isOutComplete() {
        return openOutVars.isEmpty();
    }

    public Var decide() {
        return openOutVars.removeFirst();
    }

    @Override
    public AutoAssignContext copy() {
        return new Csp(this);
    }
}
