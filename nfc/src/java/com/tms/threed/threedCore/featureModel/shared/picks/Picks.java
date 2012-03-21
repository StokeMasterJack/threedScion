package com.tms.threed.threedCore.featureModel.shared.picks;

import com.tms.threed.threedCore.featureModel.shared.*;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.AssignmentException;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.imageModel.shared.slice.SimplePicks;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static smartsoft.util.lang.shared.Strings.isEmpty;

public class Picks implements PicksRO, PicksMutable, AutoAssignContext, SimplePicks {


//    protected final FeatureModel fm;
//    protected final HashMap<Var, Assignment> map = new HashMap<Var, Assignment>();

    protected PicksAssignment[] map;

    private boolean dirty;

    private Var mostRecentSinglePick;
    private PicksSnapshot lastState;

    protected Boolean valid;
    protected String errorMessage;

    private final PicksContext ctx;

    public Picks(PicksContext ctx) {
        this.ctx = ctx;


        this.map = new PicksAssignment[ctx.getVarCount()];

        for (int i = 0; i < map.length; i++) {
            map[i] = PicksAssignment.UNASSIGNED;
        }
    }

    public void assignTrue(Var var, int depth) throws AssignmentException {
        boolean ch = autoAssign(var, true);
        if (ch) {
            dirty();
        }
    }

    public void assignTrue(Var var) throws AssignmentException {
        this.assignTrue(var, 0);
    }

    public void assignFalse(Var var, int depth) throws AssignmentException {
        boolean ch = autoAssign(var, false);
        if (ch) {
            dirty();
        }
    }

    public void assignFalse(Var var) throws AssignmentException {
        this.assignFalse(var, 0);
    }

    private void dirty() {
        this.dirty = true;
    }

    private void clean() {
        this.dirty = false;
    }

    public boolean getValid() {
        return valid;
    }

    public boolean isValid() {
        if (valid == null) throw new IllegalStateException();
        return valid;
    }

    @Override
    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void resetDirtyFlag() {
        dirty = false;
        storeSnapshot();
        mostRecentSinglePick = null;
    }

    private void storeSnapshot() {
        lastState = this.createSnapshot();
    }

    public void resetAllAssignments() {
        for (int i = 0; i < map.length; i++) {
            map[i] = PicksAssignment.UNASSIGNED;
        }
        mostRecentSinglePick = null;
    }

    //auto-assign
    public boolean assign(Var var, boolean newValue) {

        Bit newVal = newValue ? Bit.TRUE : Bit.FALSE;
        Bit oldValue = map[var.index].getValue();
        if (oldValue.equals(newVal)) return false;

        map[var.index] = PicksAssignment.create(newVal, Source.Fixup);

        dirty = true;
        return true;
    }

    public boolean autoAssign(Var var, boolean newValue) {
        return assign(var, newValue);
    }

    public void initialAssign(Var var, boolean newValue) {
        Bit newVal = newValue ? Bit.TRUE : Bit.FALSE;
        map[var.index] = PicksAssignment.create(newVal, Source.Initial);
    }

    public void pick(Var var) {
        userAssign(var, Bit.TRUE);
    }

    public void userAssign(Var var, Bit newValue) {
        Bit oldValue = map[var.index].getValue();
        if (oldValue.equals(newValue)) {
            mostRecentSinglePick = null;
            return;
        }

        if (var.isXorChild()) {
            if (newValue.isFalse()) throw new IllegalArgumentException();
            if (newValue.isOpen()) throw new IllegalArgumentException();
            Var pickOneGroup = var.getParent();
            for (Var childVar : pickOneGroup.getChildVars()) {
                map[childVar.index] = PicksAssignment.create(Bit.FALSE, Source.User);
            }

        }
        map[var.index] = PicksAssignment.create(newValue, Source.User);
        dirty = true;
        mostRecentSinglePick = var;

    }

    public void userAssign(Var var, boolean value) {
        userAssign(var, Bit.fromBool(value));
    }

    public void userPick(String varCode) {
        Var var = ctx.getVarOrNull(varCode);
        if (var == null) {
            //System.out.println("Ignoring pick [" + varCode + "]. It is not in the FeatureModel.");
        } else {
            pick(var);
        }
    }

    public void pick(Set<String> vars) {
        for (String varCode : vars) {
            userPick(varCode);
        }
    }

    public void userAssign(String varCode, boolean value) {
        final Var var = ctx.getVarOrNull(varCode);
        if (var == null) {
            //System.out.println("Ignoring assignment for var: [" + varCode + "]. Not in FeatureModel.");
        } else {
            userAssign(var, Bit.fromBool(value));
        }
    }

    public void userAssign(String code, Bit value) {
        userAssign(ctx.getVarOrNull(code), value);
    }

    public void userAssign(List<Var> vars) {
        for (Var var : vars) {
            userAssign(var, true);
        }
    }


    public void pick(Var... vars) {
        for (Var var : vars) {
            userAssign(var, true);
        }
    }

    public void pick(String... vars) {
        for (String code : vars) {
            userAssign(code, true);
        }
    }

    public void resetAutoAssignments() {
        for (int i = 0; i < map.length; i++) {
            Source source = map[i].getSource();
            if (source != null && source.isFixup()) map[i] = PicksAssignment.UNASSIGNED;
        }
    }


    public void fixup() throws AssignmentException {
        System.out.println();
        System.out.println("Picks.fixup");
        valid = null;
        errorMessage = null;
        try {
            fixupBasedOnConstraints();  //todo freeze entire system
            fixupLeafVarsBasedOnDefaults();
            fixupNonLeafVarsBasedOnDefaults();
            valid = true;
            System.out.println("valid=A");
        } catch (AssignmentException e) {
            valid = false;
            System.out.println("valid=B");
            errorMessage = e.getMessage();
            this.resetAutoAssignments();
            throw e;
        } catch (RuntimeException e) {
            valid = null;
            System.out.println("valid=C");
            this.resetAutoAssignments();
            throw e;
        } catch (Exception e) {
            System.out.println("valid=D");
            this.resetAutoAssignments();
            throw new RuntimeException(e);
        }
    }


    private void fixupBasedOnConstraints() throws AssignmentException {
        clean();
        ctx.getConstraint().autoAssignTrue(this);
        if (isDirty()) fixupBasedOnConstraints();
    }

    public void fixupLeafVarsBasedOnDefaults() {
        for (Var var : getUnassignedVars()) {
            if (var.isLeaf()) {
                var.fixupAssignDefaultForUi(this);
            }
        }
    }

    public void initVisibleDefaults() {
//        System.out.println("Picks.initVisibleDefaults [map.length: " + map.length + "]");
        for (int i = 0; i < map.length; i++) {

            Var var = ctx.getVar(i);
            PicksAssignment a = map[i];
            boolean open = a.isOpen();
            boolean leaf = var.isLeaf();
            boolean derived = var.isDerived();
            boolean OLND = open && leaf && !derived;
            if (OLND) {
//                System.out.println("\t" + var + "\t (open:" + open + " leaf:" + leaf + " !derived: " + !derived + ") = " + OLND);
                var.initialAssignDefault(this);
            }

        }
//        Set<Var> unassignedVars = getUnassignedVars();
//        System.out.println("unassignedVars.size() = " + unassignedVars.size());
//        for (Var var : unassignedVars) {
//            if (var.isLeaf() && !var.isDerived()) {
//                var.initialAssignDefault(this);
//            }
//        }
    }

    public void fixupNonLeafVarsBasedOnDefaults() {
        for (Var var : getUnassignedVars()) {
            if (!var.isLeaf()) {
                var.fixupAssignDefaultForUi(this);
            }
        }
    }

    public void unpick(Var var) {
        userAssign(var, Bit.FALSE);
    }

    public void parseAndPick(String commaDelimitedList) {

        if (isEmpty(commaDelimitedList)) return;
        commaDelimitedList = commaDelimitedList.trim();

        final String[] a = commaDelimitedList.split(",");
        if (a == null || a.length == 0) return;

        for (String code : a) {
            if (isEmpty(code)) continue;
            String varCode = code.trim();
            userPick(varCode);
        }
    }


    @Override
    public PicksContext getPicksContext() {
        return ctx;
    }

    @Override
    public Bit getValue(Var var) {
        return map[var.index].getValue();
    }

    @Override
    public Set<Var> getUnassignedVars() {
        return getVarsByValue(Bit.OPEN);
    }

    @Override
    public Set<Var> getVarsByValue(Bit filter) {
        return getVarsByValue(filter, false);
    }

    /**
     * @param filter null is wildcard
     * @param leaf   null is wildcard
     */
    public Set<Var> getVarsByValue(Bit filter, Boolean leaf) {
        Set<Var> set = new HashSet<Var>();

        for (int varIndex = 0; varIndex < map.length; varIndex++) {
            PicksAssignment assignment = map[varIndex];
            Tri value;
            if (assignment == null) {
                value = Bit.OPEN;
            } else {
                value = assignment.getValue();
            }
            assert value != null;
            if (filter == null || value.equals(filter)) {
                Var var = ctx.getVar(varIndex);
                if (leaf == null || leaf.equals(var.isLeaf())) {
                    set.add(var);
                }
            }
        }

        return set;
    }


    @Override
    public boolean anyAssignments() {
        for (int i = 0; i < map.length; i++) {
            PicksAssignment assignment = map[i];
            if (assignment.isAssigned()) return true;
        }
        return false;
    }

    @Override
    public Set<Var> toVarSet(Bit value, Source source) {

        HashSet<Var> set = new HashSet<Var>();

        for (int i = 0; i < map.length; i++) {
            PicksAssignment assignment = map[i];
            Var itVar = ctx.getVar(i);
            Tri itValue = assignment.getValue();
            Source itSource = assignment.getSource();

            if (value != null && !value.equals(itValue)) continue;
            if (source != null && !source.equals(itSource)) continue;

            set.add(itVar);
        }
        return set;
    }


    @Override
    public Set<Var> getUserPicks() {
        return toVarSet(Bit.TRUE, Source.User);
    }

    @Override
    public PicksKey getKey() {
        return new PicksKey(getUserPicks());
    }


    @Override
    public int getUnassignedVarCount() {
        int count = 0;
        for (int i = 0; i < map.length; i++) {
            PicksAssignment assignment = map[i];
            if (!assignment.isAssigned()) count++;
        }
        return count;
    }

    @Override
    public int getAssignedVarCount() {
        int count = 0;
        for (int i = 0; i < map.length; i++) {
            PicksAssignment assignment = map[i];
            if (assignment.isAssigned()) count++;
        }
        return count;
    }

    @Override
    public int getPickCount() {
        int count = 0;

        for (int i = 0; i < map.length; i++) {
            PicksAssignment assignment = map[i];
            if (assignment.isTrue()) count++;
        }
        return count;
    }


    @Override
    public void printPicks() {
        System.out.println("Picks: (map.length:" + map.length);

        for (int varIndex = 0; varIndex < map.length; varIndex++) {
            PicksAssignment assignment = map[varIndex];
            Bit val = assignment.getValue();
            Var var = ctx.getVar(varIndex);
            System.out.println("\t" + var + ": " + val);
        }
    }

    @Override
    public void printAssignments(Bit filter) {
        printAssignments(filter, null);
    }

    @Override
    public void printAssignments(Boolean leaf) {
        printAssignments(null, leaf);
    }

    @Override
    public void printAssignments() {
        printAssignments(Bit.TRUE, true);
        printAssignments(Bit.FALSE, true);
        printAssignments(Bit.OPEN, true);
        System.out.println();
    }

    /**
     * @param filter null is wildcard
     * @param leaf   null is wildcard
     */
    @Override
    public void printAssignments(Bit filter, Boolean leaf) {

        Set<Var> vars = getVarsByValue(filter, leaf);
        String prefix = filter == null ? "*" : filter.toChar() + "";
        System.out.println(prefix + "\t" + vars.size() + "\t" + vars);
    }

    @Override
    public void printUnassignedVars() {
        System.out.println("Unassigned Vars:");
        Set<Var> vars = getVarsByValue(Bit.OPEN);

        for (Var var : vars) {
            System.out.println("\t" + var);
        }
    }


    @Override
    public Set<Var> getAllPicks() {
        return getVarsByValue(Bit.TRUE);
    }

    @Override
    public Set<String> getAllPicks2() {
        final HashSet<String> picks = new HashSet<String>();
        for (Var var : getAllPicks()) {
            picks.add(var.getCode());
        }
        return picks;
    }

    @Override
    public boolean isAssigned(Var var) {
        Bit bit = getValue(var);
        return bit.isAssigned();
    }

    @Override
    public boolean isUnassigned(Var var) {
        Tri bit = getValue(var);
        return bit.isOpen();
    }

    @Override
    public boolean isTrue(Var var) {
        Tri bit = getValue(var);
        return bit.isTrue();
    }

    @Override
    public boolean isFalse(Var var) {
        Tri bit = getValue(var);
        return bit.isFalse();
    }

    @Override
    public boolean isPicked(Var var) {
        assert var.index <= map.length;
        return isTrue(var);
    }

    @Override
    public boolean isPicked(String code) {
        Var var = ctx.getVar(code);
        return isPicked(var);
    }

    @Override
    public boolean containsAll(Collection<String> features) {
        for (String varCode : features) {
            if (!isPicked(varCode)) return false;
        }
        return true;
    }

    @Override
    public boolean containsAllVars(Collection<Var> features) {
        for (Var var : features) {
            if (!isPicked(var)) return false;
        }
        return true;
    }

    @Override
    public String toString() {
        Set<Var> varSet = null;
        varSet = getVarsByValue(Bit.TRUE, true);
        assert varSet != null;
        return varSet.toString();

    }

    @Override
    public boolean equals(Object o) {
        throw new UnsupportedOperationException("Picks does not support equals method");
    }

    @Override
    public int hashCode() {
        throw new UnsupportedOperationException("Picks does not support hashCode method");
    }

    @Override
    public PicksAssignment getAssignment(Var var) {
        return map[var.index];
    }


    @Override
    public PicksSnapshot createSnapshot() {
        PicksSnapshotImpl picksSnapshot = new PicksSnapshotImpl(ctx);

        for (int i = 0; i < map.length; i++) {
            picksSnapshot.map[i] = map[i];
        }

        picksSnapshot.valid = this.valid;
        picksSnapshot.errorMessage = this.errorMessage;
        return picksSnapshot;
    }

    @Override
    public Picks copyIgnoreFixupPicks() {
        Picks picks = new Picks(ctx);
        for (int i = 0; i < map.length; i++) {
            Source source = map[i].getSource();
            if (source != null && source.isFixup()) map[i] = PicksAssignment.UNASSIGNED;
            else picks.map[i] = map[i];
        }
        picks.valid = this.valid;
        picks.errorMessage = this.errorMessage;
        return picks;
    }

    @Override
    public AutoAssignContext copy() {
        throw new UnsupportedOperationException();
    }
}