package c3i.core.featureModel.shared;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.boolExpr.And;
import c3i.core.featureModel.shared.boolExpr.AssignmentException;
import c3i.core.featureModel.shared.boolExpr.BoolExpr;
import c3i.core.featureModel.shared.boolExpr.Conflict;
import c3i.core.featureModel.shared.boolExpr.False;
import c3i.core.featureModel.shared.boolExpr.Iff;
import c3i.core.featureModel.shared.boolExpr.Imp;
import c3i.core.featureModel.shared.boolExpr.MasterConstraint;
import c3i.core.featureModel.shared.boolExpr.Not;
import c3i.core.featureModel.shared.boolExpr.Or;
import c3i.core.featureModel.shared.boolExpr.True;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.boolExpr.VarsDb;
import c3i.core.featureModel.shared.boolExpr.Xor;
import c3i.core.featureModel.shared.picks.Picks;
import c3i.core.featureModel.shared.picks.PicksContextFm;
import c3i.core.threedModel.shared.SubSeries;
import c3i.imageModel.shared.ImContext;
import c3i.imageModel.shared.ImContextKey;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import smartsoft.util.shared.Strings;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class FeatureModel implements Vars, ImContext<Var> {

    public final LinkedHashSet<BoolExpr> extraConstraints = new LinkedHashSet<BoolExpr>();

    private final String displayName;

    private final SeriesKey seriesKey;

    public static final False FALSE = BoolExpr.FALSE;
    public static final True TRUE = BoolExpr.TRUE;

    private final VarsDb vars;

    private SubSeries subSeries;

    private boolean sorted;

    public FeatureModel(SeriesKey seriesKey, String displayName) {
        this.seriesKey = seriesKey;
        this.displayName = displayName;
        vars = new VarsDb();
    }

    public FeatureModel(BrandKey brandKey, int year, String name, String displayName) {
        this.seriesKey = new SeriesKey(brandKey, year, name);
        this.displayName = displayName;
        vars = new VarsDb();
    }

    public Set<Var> getAllXorParentVars() {
        TreeSet<Var> a = new TreeSet<Var>(new Comparator<Var>() {
            @Override
            public int compare(Var v1, Var v2) {
                Integer i1 = v1.getChildCount();
                Integer i2 = v2.getChildCount();
                return i2.compareTo(i1);
            }
        });
        for (Var var : vars) {
            if (var.isXorParent()) {
                a.add(var);
            }
        }
        return a;
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public ImContextKey getContextKey() {
        return seriesKey;
    }

    public int getDisplayYear() {
        if (subSeries != null && subSeries.getYear() != null) {
            return subSeries.getYear();
        } else {
            return seriesKey.getYear();
        }
    }

    public String getDisplayName() {
        if (subSeries != null && subSeries.getLabel() != null && subSeries.getLabel().trim().length() != 0) {
            return subSeries.getLabel();
        } else {
            return displayName;
        }
    }

    public Var addVar(String code, String name) {
        return getRootVar().addChild(code, name);
    }

    public Var addVar(String code) {
        return getRootVar().addChild(code);
    }

    public And getCardinalityConstraint() {
        throw new UnsupportedOperationException();
    }


    public BoolExpr getTreeConstraintsForVar(Var var) {
        LinkedHashSet<BoolExpr> set = new LinkedHashSet<BoolExpr>();
        getTreeConstraintsForVar(var, set);
        return and(set);
    }

    private void getTreeConstraintsForVar(Var var, LinkedHashSet<BoolExpr> set) {

        if (var.isRoot()) {
            set.add(var);
        }

        if (var.getMandatory()) {
            set.add(var);
        }

        if (var.isZeroOrMoreGroup()) {
            set.add(var);
        }

        if (var.isAllGroup()) {
            set.add(var);
            for (Var ch : var.getChildVars()) {
                set.add(ch);
            }
        }

        if (var.isXorParent()) {
            set.add(var);
            Xor childNodesAsXor = var.getChildNodesAsXor();

            set.add(childNodesAsXor);
        }

        if (var.hasChildVars()) {
            for (Var childVar : var.getChildVars()) {
                getTreeConstraintsForVar(childVar, set);
            }
        }


    }

    public Var get(int varIndex) throws UnknownVarIndexException {
        return vars.get(varIndex);
    }

    public Var get(String varCode) throws UnknownVarCodeException {
        return vars.get(varCode);

    }

    public Var resolveVar(String varCode) {
        try {
            return get(varCode);
        } catch (UnknownVarCodeException e) {
            return null;
        }
    }

    public boolean containsCode(String code) {
        return vars.containsCode(code);
    }

    @Override
    public boolean containsIndex(int varIndex) {
        return vars.containsIndex(varIndex);
    }

    public int size() {
        return vars.size();
    }

    public Set<Var> getVars(Collection<String> varCodes) {
        Set<Var> set = new HashSet<Var>();
        for (String code : varCodes) {
            Var var = resolveVar(code);
            if (var == null) {
                System.out.println("[" + code + "] not in FeatureModel");
            } else {
                set.add(var);
            }
        }
        return set;
    }

    public BoolExpr getRootConstraint() {
        return getRootVar();
    }

    public BoolExpr getTreeConstraint() {
        return getTreeConstraintsForVar(getRootVar());
    }

    public BoolExpr getExtraConstraint() {
        if (extraConstraints.size() == 0) return TRUE;
        return and(extraConstraints);
    }

    public int getExtraConstraintCount() {
        return extraConstraints.size();
    }

    public int getRootConstraintCount() {
        return 1;
    }

    public int getTreeConstraintCount() {
        return getTreeConstraint().getExprCount();
    }

    public int getCardinalityConstraintCount() {
        return getCardinalityConstraint().getExprCount();
    }

    public int getAllConstraintCount() {
        return getRootConstraintCount() + getTreeConstraintCount() + getCardinalityConstraintCount() + getExtraConstraintCount();
    }

    public Var getRootVar() {
        return vars.getRootVar();
    }

    /**
     * Add extra-constraint
     *
     * @param expr must not reference (directly or indirectly) any non-leaf vars
     */
    public void addConstraint(BoolExpr expr) {
        extraConstraints.add(expr);
    }

    public And and(LinkedHashSet<BoolExpr> expressions) {
        return BoolExpr.and(expressions);
    }

    public And and(BoolExpr... expressions) {
        return BoolExpr.and(expressions);
    }

    public Or or(LinkedHashSet<BoolExpr> expressions) {
        return BoolExpr.or(expressions);
    }

    public Or or(BoolExpr... expressions) {
        return BoolExpr.or(expressions);
    }

    public Not not(BoolExpr expr) {
        return BoolExpr.not(expr);
    }

    public Xor xor(LinkedHashSet<BoolExpr> expressions) {
        return BoolExpr.xor(expressions);
    }

    public Xor xor(BoolExpr... expressions) {
        return BoolExpr.xor(expressions);
    }

    public Conflict conflict(BoolExpr e1, BoolExpr e2) {
        return BoolExpr.conflict(e1, e2);
    }

    public Iff iff(BoolExpr e1, BoolExpr e2) {
        return BoolExpr.iff(e1, e2);
    }

    public Imp imply(BoolExpr e1, BoolExpr e2) {
        return BoolExpr.imp(e1, e2);
    }

    public void printSummary() {
        System.out.println("Var Count: " + size());
        System.out.println("ExtraConstraint Count: " + getExtraConstraintCount());
        System.out.println("TreeConstraint Count: " + getTreeConstraintCount());
        System.out.println("CardinalityConstraint Count: " + getCardinalityConstraintCount());
        System.out.println();
    }

    public void printDetails() {
        System.out.println("Var Count: " + size());
        System.out.println("Tree Constraint Count: " + getTreeConstraintCount());
        System.out.println("Extra Constraint Count: " + getExtraConstraintCount());

        System.out.println("Features:");
        getRootVar().printVarTree();

        System.out.println("Constraints:");
        for (BoolExpr expr : extraConstraints) {
            System.out.println("\t" + expr);
        }
    }

    public void printRootConstraint() {
        System.out.println("RootConstraint: ");
        System.out.println("\t" + getRootConstraint());
    }

    public void printAllConstraints() {
        printRootConstraint();
        printTreeConstraints();
        printExtraConstraints();
    }

    public void printExtraConstraints() {
        System.out.println("ExtraConstraints: ");
        Collection<BoolExpr> list = getExtraConstraint().getExpressions();
        for (BoolExpr boolExpr : list) {
            System.out.println("\t" + boolExpr);
        }
    }

    public void printTreeConstraints() {
        System.out.println("TreeConstraints: ");
        Collection<BoolExpr> list = getTreeConstraint().getExpressions();
        for (BoolExpr boolExpr : list) {
            System.out.println("\t" + boolExpr);
        }
    }

    public FeatureModel copy() {
        return this;  //todo
    }

    public String getSeriesName() {
        return getSeriesKey().getShortName();
    }

    private boolean isBaseGrade(String featureCode) {
        if (featureCode.equalsIgnoreCase(getSeriesName())) return true;
        if (featureCode.equalsIgnoreCase("base")) return true;
        return false;
    }

    public Picks createPicks() {
        return new Picks(new PicksContextFm(this));
    }

    public Picks createPicks(Set<String> picks) {
        Picks p = new Picks(new PicksContextFm(this));
        p.pick(picks);
        return p;
    }

    public static boolean containsWhitespace(String s) {
        return Strings.containsWhitespace(s);
    }

    public List<Var> getAccessories() {
        Var accessoriesVar = getAccessoriesVar();
        return accessoriesVar.getDescendantLeafs();
    }

    public Var getAccessoriesVar() {
        return get(IVarGuesser.Accessories);
    }

    public MasterConstraint getConstraint() {
        LinkedHashSet<BoolExpr> a = new LinkedHashSet<BoolExpr>();
        BoolExpr ec = getExtraConstraint();
        BoolExpr tc = getTreeConstraint();
        a.addAll(tc.getExpressions());
        a.addAll(ec.getExpressions());
        return new MasterConstraint(a);
    }

    public Collection<Var> getXorExclusions(String... xorPicks) {
        HashSet<Var> a = new HashSet<Var>();
        for (String varCode : xorPicks) {
            Var var = get(varCode);
            a.addAll(getExclusions(var));
        }
        return a;
    }

    public Collection<Var> getXorExclusions(Collection<Var> varsToKeep) {
        HashSet<Var> a = new HashSet<Var>();
        for (Var var : varsToKeep) {
            a.addAll(getExclusions(var));
        }
        return a;
    }

    public Collection<Var> getExclusions(Var xorPick) {
        assert xorPick.isXorChild();
        HashSet<Var> a = new HashSet<Var>();
        List<Var> childVars = xorPick.getParent().getChildVars();
        for (Var childVar : childVars) {
            if (!childVar.equals(xorPick)) {
                a.add(childVar);
            }
        }
        return a;
    }

//    public Xor getModelCodeXor() {
//        Var modelCodes = get(IVarGuesser.ModelCode);
//        return modelCodes.getChildNodesAsXor();
//    }

    public Collection<Var> getIffVarsForVar(Var var) {
        Collection<BoolExpr> expressions = getExtraConstraint().getExpressions();
        for (BoolExpr expression : expressions) {
            if (expression instanceof Iff) {
                Iff iff = (Iff) expression;
                if (iff.getExpr1().equals(var)) {
                    HashSet<Var> vars = new HashSet<Var>();
                    Collection<BoolExpr> varExpressions = iff.getExpr2().getExpressions();
                    for (BoolExpr varExpression : varExpressions) {
                        vars.add((Var) varExpression);
                    }
                    return vars;
                }
            }
        }
        throw new IllegalStateException();
    }

    public Set<Var> getPickableVars() {
        LinkedHashSet<Var> a = new LinkedHashSet<Var>();
        for (Var var : vars) {
            if (var.isDerived()) continue;
            if (var.isMandatory()) continue;
            if (var.hasChildVars()) {
                continue;
//                throw new IllegalStateException("Parent vars should always be derived: " + var);
            }
            a.add(var);
        }
        return a;
    }

    public ImmutableSet<Var> getInitiallyTruePickableVars() {
        HashSet<Var> a = new HashSet<Var>();
        for (Var var : getPickableVars()) {
            if (var.isInitiallyPicked()) {
                a.add(var);
            }
        }
        return ImmutableSet.copyOf(a);
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != FeatureModel.class) return false;

        FeatureModel that = (FeatureModel) obj;

        boolean varsEqual = vars.equals(that.vars);


        String ecThis = this.extraConstraints.toString();
        String ecThat = that.extraConstraints.toString();
        boolean ecEqual = ecThis.equals(ecThat);


        return varsEqual && ecEqual;
    }

    public void setSubSeries(SubSeries subSeries) {
        this.subSeries = subSeries;
    }

    public ImmutableSet<Var> varCodesToVars(Iterable<String> picksRaw) {
        ImmutableSet.Builder<Var> builder = ImmutableSet.builder();
        for (String varCode : picksRaw) {
            try {
                Var var = get(varCode);
                builder.add(var);
            } catch (UnknownVarCodeException e) {
                //ignore
            }
        }
        return builder.build();
    }

    public Assignments fixRaw(Set<String> picksRaw) throws AssignmentException {
        ImmutableSet<Var> picks = varCodesToVars(picksRaw);
        return fix(picks);
    }

    public Assignments fix(Set<Var> picks) throws AssignmentException {
        Csp csp = createCsp(picks);
        csp.propagate();
        if (!csp.isSolved()) {
            csp.fillInInitialPicks();
        }
        AssignmentsSimple assignments = (AssignmentsSimple) csp.getAssignments();
        AssignmentsSimple copy = new AssignmentsSimple(assignments);
        return copy;
    }

    public FixedPicks fixup(Set<Var> picks) {
        try {
            Assignments assignments = this.fix(picks);
            return new FixedPicks(picks, assignments, null);
        } catch (AssignmentException e) {
            return new FixedPicks(picks, null, e);
        }
    }

    public FixedPicks fixupRaw(Iterable<String> picksRaw) {
        ImmutableSet<Var> picks = varCodesToVars(picksRaw);
        try {
            Assignments assignments = fix(picks);
            return new FixedPicks(picks, assignments, null);
        } catch (AssignmentException e) {
            return new FixedPicks(picks, null, e);
        }
    }

    public CspSimple createCsp(Set<Var> trueVars) {
        Preconditions.checkNotNull(trueVars);
        CspSimple csp = createCsp();
        for (Var trueVar : trueVars) {
            csp.assignTrue(trueVar);
        }
        return csp;
    }

    public CspSimple createCsp() {
        return new CspSimple(this, getConstraint());
    }

    public CspForTreeSearch createCspForTreeSearch(Collection<Var> outputVars) {
        return new CspForTreeSearch(this, getConstraint(), outputVars);
    }

    public CspForTreeSearch createCspForTreeSearch() {
        return new CspForTreeSearch(this, getConstraint());
    }

    public <R> void forEach(FmSearchRequest<R> searchRequest) {
        ImmutableSet<Var> outVars = searchRequest.getOutVars();
        CspForTreeSearch csp;
        if (outVars != null) {
            csp = createCspForTreeSearch(outVars);
        } else {
            csp = createCspForTreeSearch();
        }
        csp.propagateSimplify();
        csp.forEach(searchRequest);
    }

}

