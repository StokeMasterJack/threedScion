package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.ReassignmentException;
import c3i.featureModel.shared.boolExpr.Var;
import smartsoft.util.shared.Strings;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

abstract public class AbstractAssignments<A extends Assignments> implements Assignments<A> {

    //shallow copy   todo can shallow copy be replaced by NO copy? These values never change for the duration of a search
    protected final Vars allVars;

    //not copied at all, a newly copies csp should start out clean
    private boolean dirty;

    //deep copy
    protected final Bit[] assignments;   //index is var.index

    public AbstractAssignments(Vars vars) {
        this.allVars = vars;
        assignments = new Bit[vars.size()];
        for (int i = 0; i < assignments.length; i++) {
            assignments[i] = Bit.OPEN;
        }
    }

    /**
     * This is the *copy* in our Copying-based branch-and-prune tree search
     *
     * @param that
     */
    protected AbstractAssignments(AbstractAssignments that) {
        assert that != null;

        //immutable (once search is started), can shallow copy
        this.allVars = that.allVars;

        //deep copy
        this.assignments = new Bit[that.assignments.length];
        System.arraycopy(that.assignments, 0, this.assignments, 0, assignments.length);
    }


//    public Decisions decide() {
//
//        PeekingIterator<Var> it;
//
//        if (!openVars1.isEmpty()) {
//            it = new PeekingImpl<Var>(openVars1.iterator());
//        } else if (!openVars2.isEmpty()) {
//            it = new PeekingImpl<Var>(openVars2.iterator());
//        } else {
//            return null;
//        }
//
//        Decisions d;
//
////        d = XorDecisions.create(it);
////        if (d != null) return d;
//
//        d = SimpleDecisions.create(it);
//        if (d != null) return d;
//
//        return null;
//
//    }


    @Override
    public Vars getVars() {
        return allVars;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentsSimple that = (AssignmentsSimple) o;
        return Arrays.deepEquals(assignments, that.assignments);
    }

    @Override
    public int hashCode() {
        return Arrays.deepHashCode(assignments);
    }

    @Override
    public boolean isTrue(Var var) {
        return assignments[var.getIndex()].isTrue();
    }

    @Override
    public boolean isTrue(int varIndex) {
        return assignments[varIndex].isTrue();
    }

    @Override
    public boolean isFalse(Var var) {
        return assignments[var.getIndex()].isFalse();
    }

    @Override
    public boolean isFalse(int varIndex) {
        return assignments[varIndex].isFalse();
    }

    @Override
    public boolean isOpen(Var var) {
        return assignments[var.getIndex()].isOpen();
    }

    @Override
    public boolean isOpen(int varIndex) {
        return assignments[varIndex].isOpen();
    }

//    public boolean isOutputVar(Var var) {
//        return allVarsState[var.index].isOutputVar();
////        return outputVars == null || outputVars.contains(var);
//    }

//    public boolean isOutputVar(int varIndex) {
//        return allVarsState[varIndex].isOutputVar();
//        Var var = get(varIndex);
//        return outputVars == null || outputVars.contains(var);
//    }

    @Override
    public boolean isAssigned(Var var) {
        return !isOpen(var);
    }

    public static boolean logAssignments;

    @Override
    public void logAssignTrue(Var var, int depth) {
        if (logAssignments) {
            log(depth, "Assign " + var + " true");
        }
    }

    @Override
    public void logAssignFalse(Var var, int depth) {
        if (logAssignments) {
            log(depth, "Assign " + var + " false");
        }
    }

    private static void log(int depth, String msg) {
        System.err.println(Strings.indent(depth) + msg);
    }

    @Override
    public void assignTrue(Var var) throws ReassignmentException {
        assignTrue(var, 0);
    }

    @Override
    public void assignFalse(Var var) throws ReassignmentException {
        assignFalse(var, 0);
    }

    @Override
    public void assignTrue(Var var, int depth) throws ReassignmentException {
        if (isFalse(var)) {
            throw new ReassignmentException(var, true, this);
        } else if (isTrue(var)) {
            //no action
        } else if (isOpen(var)) {
            logAssignTrue(var, depth);
//            System.err.println("Assign [" + var + "] true");
            assignments[var.getIndex()] = Bit.TRUE;
            boolean removed = removeFromOpenVars(var);
            assert removed;
            dirty();
        } else {
            throw new IllegalStateException();
        }
    }

    abstract protected boolean removeFromOpenVars(Var var);

    @Override
    public void assignFalse(Var var, int depth) throws ReassignmentException {
        if (isTrue(var)) {
            throw new ReassignmentException(var, false, this);
        } else if (isFalse(var)) {
            //no action
        } else if (isOpen(var)) {
            logAssignFalse(var, depth);
//            System.err.println("Assign [" + var + "] false");
            assignments[var.getIndex()] = Bit.FALSE;
            boolean removed = removeFromOpenVars(var);
            assert removed : this.getClass() + " varName: " + var;
            dirty();
        } else {
            throw new IllegalStateException();
        }
    }

    @Override
    public void dirty() {
        dirty = true;
    }

    @Override
    public void clean() {
        dirty = false;
    }

    @Override
    public Bit getValue(Var var) {
        return assignments[var.getIndex()];
    }

    @Override
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

//    public void dumpOpenVars() {
//        System.err.println("openCareVars: ");
//        for (Var openVar : openVars1) {
//            System.err.println("\t" + openVar);
//        }
//    }


    @Override
    public Set<Var> getVarsWithValue(Bit val) {
        final LinkedHashSet<Var> set = new LinkedHashSet<Var>();
        for (int i = 0; i < allVars.size(); i++) {
            if (assignments[i].equals(val)) set.add(allVars.get(i));
        }
        return set;
    }

    @Override
    public Set<Var> getTrueVars() {
        final LinkedHashSet<Var> set = new LinkedHashSet<Var>();
        for (int i = 0; i < allVars.size(); i++) {
            Var var = allVars.get(i);
            if (assignments[i].isTrue() && var.isLeaf()) {

                set.add(var);
            }
        }
        return set;
    }

    @Override
    public void fillInInitialPicks() {
        for (int i = 0; i < allVars.size(); i++) {
            if (assignments[i].isOpen()) {
                Var var = allVars.get(i);

                if (var.isInitiallyPicked()) {
                    assignTrue(var);
                } else {
                    assignFalse(var);
                }
            }
        }
    }


    @Override
    public Set<Var> getFalseVars() {
//        return falseVars;
        final LinkedHashSet<Var> set = new LinkedHashSet<Var>();
        for (int i = 0; i < allVars.size(); i++) {
            if (assignments[i].isFalse()) set.add(allVars.get(i));
        }
        return set;
    }

    //    public Set<Var> getTrueOutputVars(Set<Var> varsToSuppressInSolution) {
//        final LinkedHashSet<Var> set = new LinkedHashSet<Var>();
//
//        for (int i = 0; i < vars.getVarCount(); i++) {
//
//            Var var = vars.get(i);
//            if (outputFilter != null && !outputFilter.accept(var)) continue;
//            if (varsToSuppressInSolution.contains(var)) continue;
//
//            Bit val = varStates[i].getValue();
//            if (!val.isTrue()) continue;
//
//            set.add(vars.get(i));
//        }
//        return set;
//    }
//
    @Override
    public VarStates snapVarsStates(VarStates.Filter filter) {
        return new VarStates(this, filter);
    }

    @Override
    public VarStates snapVarsStates() {
        return new VarStates(this);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public Var getFirstUnassignedXorVar() {
        for (int i = 0; i < assignments.length; i++) {
            if (assignments[i].isOpen() && allVars.get(i).isXorChild()) {
                return allVars.get(i);

            }
        }
        return null;
    }


    @Override
    public Var getVar(int varIndex) {
        return allVars.get(varIndex);
    }


//    public List<Var> getOpenVars() {
//        ArrayList<Var> a = new ArrayList<Var>();
//        a.addAll(openVars1);
//        a.addAll(openVars2);
//        return a;
//    }

    public int size() {
        return allVars.size();
    }

    public Var get(int varIndex) throws UnknownVarIndexException {
        return allVars.get(varIndex);
    }

    public Var get(String varCode) throws UnknownVarCodeException {
        return allVars.get(varCode);
    }

    public boolean containsCode(String code) {
        return allVars.containsCode(code);
    }

    public boolean containsIndex(int varIndex) {
        return allVars.containsIndex(varIndex);
    }


    @Override
    public boolean isSolved() {
        for (int i = 0; i < assignments.length; i++) {
            if (assignments[i].isOpen()) return false;
        }
        return true;
    }

    @Override
    public boolean anyOpenVars() {
        return !isSolved();
    }

    @Override
    public boolean isPicked(Var var) {
        return isTrue(var);
    }


}
