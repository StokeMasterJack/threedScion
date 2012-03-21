package com.tms.threed.threedCore.featureModel.shared;

//import com.tms.threed.featureModel.server.BoolExprString;
//import com.tms.threed.featureModel.server.ExprParser;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.*;
import com.tms.threed.threedCore.featureModel.shared.picks.Picks;
import com.tms.threed.threedCore.featureModel.shared.picks.PicksContextFm;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.SubSeries;
import smartsoft.util.lang.shared.Path;
import smartsoft.util.lang.shared.Strings;

import java.util.*;

import static smartsoft.util.lang.shared.Strings.isEmpty;

public class FeatureModel implements Vars {

    public final LinkedHashSet<BoolExpr> extraConstraints = new LinkedHashSet<BoolExpr>();

    private final String displayName;

    private final SeriesKey seriesKey;

    public static final False FALSE = BoolExpr.FALSE;
    public static final True TRUE = BoolExpr.TRUE;

    private final VarsDb vars;

    private SubSeries subSeries;

    public FeatureModel(SeriesKey seriesKey, String displayName) {
        this.seriesKey = seriesKey;
        this.displayName = displayName;
        vars = new VarsDb();
    }

    public FeatureModel(int year, String name, String displayName) {
        this.seriesKey = new SeriesKey(year, name);
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

    public Set<Var> getAllXorChildVars() {
        HashSet<Var> a = new HashSet<Var>();
        Set<Var> xorParentVars = getAllXorParentVars();

        for (Var xorParentVar : xorParentVars) {
            for (Var xorChildVar : xorParentVar.getChildVars()) {
                a.add(xorChildVar);
            }
        }
        return a;
    }


    public Set<Var> getModelCodeVars() {
        HashSet<Var> set = new HashSet<Var>();
        Xor modelCodeXor = getModelCodeXor();
        LinkedHashSet<BoolExpr> expressions = modelCodeXor.getExpressions();
        for (BoolExpr expr : expressions) {
            Var var = expr.asVar();
            set.add(var);
        }
        return set;
    }

    public Set<Var> getExteriorColorVars() {
        HashSet<Var> set = new HashSet<Var>();
        Xor exteriorXor = getExteriorColorXor();
        LinkedHashSet<BoolExpr> expressions = exteriorXor.getExpressions();
        for (BoolExpr expr : expressions) {
            Var var = expr.asVar();
            set.add(var);
        }
        return set;
    }

    public Set<Var> getInteriorColorVars() {
        HashSet<Var> set = new HashSet<Var>();
        Xor interiorXor = getInteriorColorXor();
        LinkedHashSet<BoolExpr> expressions = interiorXor.getExpressions();
        for (BoolExpr expr : expressions) {
            Var var = expr.asVar();
            set.add(var);
        }
        return set;
    }


    public void performSemiHumanFixup() {
        for (Var var : vars) {
            if (!var.hasCardinality()) {
                Cardinality card = VarGuesser.guessCardinality(var);
                var.setCardinality(card);
            }

            if (!var.hasMandatory()) {
                Boolean b = VarGuesser.guessMandatory(var);
                if (b != null) var.setMandatory(b);
            }
        }
    }


    public SeriesKey getSeriesKey() {
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

    //    public SeriesId getSeriesId() {
//        return seriesId;
//    }
//
//    public SeriesKey getSeriesKey() {
//        return seriesId.getSeriesKey();
//    }

    public Var addVar(String code, String name) {
        return getRootVar().addChild(code, name);
    }

    public Var addVar(String code) {
        return getRootVar().addChild(code);
    }

//    public Var addVar(Var var) {
//        return rootVar.addVar(var);
//    }

    public And getCardinalityConstraint() {
        throw new UnsupportedOperationException();
    }


    public BoolExpr getTreeConstraintsForVar(Var var) {
        LinkedHashSet<BoolExpr> set = new LinkedHashSet<BoolExpr>();
        getTreeConstraintsForVar(var, set);
        return and(set);
    }

    private void getTreeConstraintsForVar(Var var, LinkedHashSet<BoolExpr> set) {

//        Var p = var.getParent();
//        boolean addChildImpliesParentRule = p != null && !(p.isZeroOrMoreGroup() && p.isRoot() && p.isAllGroup());
//
//        if (addChildImpliesParentRule) {
//            set.add(imply(var, p));
//        }

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


    private boolean sorted;

    public void sortChildVarsByLeafRelationCount() {
        if (!sorted) {
            initRelationCount();
            getRootVar().sortDescendantVarsByLeafRelationCount();
            sorted = true;
        }
    }

    private void initRelationCount() {
        getRootVar().initLeafRelationCount(this);
    }


    public Var get(int varIndex) throws UnknownVarIndexException {
        return vars.get(varIndex);
    }

    public Var get(String varCode) throws UnknownVarCodeException {
        return vars.get(varCode);

    }

    public Var getVarOrNull(String varCode) {
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
            Var var = getVarOrNull(code);
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

    public boolean isPickValid(Var var) {
        return isAssignmentValid(var, Bit.TRUE);
    }

    public boolean isAssignmentValid(Var var, Tri value) {
        throw new UnsupportedOperationException();
//        BDD testBdd = restrict(f, var, value);
//        return !testBdd.isZero();
//        return true; //todo
    }

    public Set<String> varsToCodes(Set<Var> picks) {
        HashSet<String> set = new HashSet<String>();
        for (Var var : picks) {
            set.add(var.getCode());
        }
        return set;
    }

    public boolean isLeaf(String varCode) {
        final Var var = getVarOrNull(varCode);
        return var.isLeaf();
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

    public static Path createFeaturePath(List<Var> featureList) {
        if (featureList == null || featureList.size() == 0) return new Path();
        Path p = new Path();
        for (Var var : featureList) {
            p = p.append(var.getCode());
        }
        return p;
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

    /**
     * Not needed for NonFlash Config
     * Maybe needed for eBro
     */
    public void preFixup(Set<String> userPicks) {

        HashSet<String> toBeAdded = new HashSet<String>();
        Iterator<String> it = userPicks.iterator();

        while (it.hasNext()) {
            String featureCode = it.next();

            if (featureCode == null) it.remove();

            int L = featureCode.length();
            featureCode = featureCode.trim();
            if (featureCode.equals("")) it.remove();

            if (featureCode.length() != L) {
                it.remove();
                toBeAdded.add(featureCode);
            }

            if (isBaseGrade(featureCode)) {
                it.remove();
                toBeAdded.add(featureCode);
            }

        }

        userPicks.addAll(toBeAdded);

    }


    public Picks createPicks() {
        return new Picks(new PicksContextFm(this));
    }

    public Picks createPicks(Set<String> picks) {
        Picks p = new Picks(new PicksContextFm(this));
        p.pick(picks);
        return p;
    }

    public Picks getInitialVisiblePicksOld() {
        Picks p = createPicks();
        p.initVisibleDefaults();
        return p;
    }


    public Set<Var> getInitialVisiblePicksForTestHarness() {
//        System.out.println("Picks.initVisibleDefaults [map.length: " + map.length + "]");

        HashSet<Var> a = new HashSet<Var>();

        for (Var var : getPickableVars()) {

            boolean leaf = var.isLeaf();
            boolean firstXorChild = var.isFirstPickOneChild();
            Boolean defaultValue = var.getDefaultValue();

            if (!leaf) continue;
            if (defaultValue != null && defaultValue == false) continue;


            if (defaultValue != null && defaultValue || firstXorChild) {
                a.add(var);
            }


        }
        return a;
    }

    public void basicFixup() {

    }

    public void basicFixupMandatory() {
        for (Var var : vars) {
            if (var.isRoot()) continue;
            boolean isTrimChild = var.isTrimChild();
        }
    }


    public static Set<String> parse(String commaDelimitedList) {

        final HashSet<String> set = new HashSet<String>();
        if (isEmpty(commaDelimitedList)) return set;
        commaDelimitedList = commaDelimitedList.trim();

        final String[] a = commaDelimitedList.split(",");
        if (a == null || a.length == 0) return set;

        for (String code : a) {
            if (isEmpty(code)) continue;
            String varCode = code.trim();

            set.add(varCode);

        }
        return set;
    }


//    public And createConjunctionFromSpaceDelimitedList(String spaceDelimitedVarList) {
//        final List<Var> varList = parseVarList(spaceDelimitedVarList);
//        final List<BoolExpr> exprList = FeatureModel.upCastList(varList);
//        return this.and(exprList);
//    }

//    public Or createDisjunctionFromSpaceDelimitedList(String spaceDelimitedList) {
//        final List<Var> varList = parseVarList(spaceDelimitedList);
//        final List<BoolExpr> exprList = FeatureModel.upCastList(varList);
//        return this.or(exprList);
//    }

    public List<Var> parseVarList(String spaceDelimitedList) {
        final ArrayList<Var> list = new ArrayList<Var>();
        if (isEmpty(spaceDelimitedList)) throw new IllegalArgumentException();
        spaceDelimitedList = spaceDelimitedList.trim();
        final String[] a = spaceDelimitedList.split("\\s+");
        if (a == null || a.length == 0) throw new IllegalArgumentException();

        if (!containsWhitespace(spaceDelimitedList)) {
            //spaceDelimitedList must be a single code
            String code = spaceDelimitedList;
            Var var = getVarOrNull(code);
            if (var == null) throw new IllegalArgumentException("Invalid Feature Code: [" + code + "]");
            list.add(var);
        } else {
            for (String code : a) {
                if (isEmpty(code)) continue;
                code = code.trim();
                Var var = getVarOrNull(code);
                if (var == null) throw new IllegalArgumentException("Invalid Feature Code: [" + code + "]");
                list.add(var);
            }
        }
        return list;
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

    public Collection<Var> getXorExclusions(Var xorPick) {
        HashSet<Var> a = new HashSet<Var>();
        a.add(xorPick);
        return getXorExclusions(a);
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

    public Xor getModelCodeXor() {
        Var modelCodes = get(IVarGuesser.ModelCode);
        return modelCodes.getChildNodesAsXor();
    }

    public Xor getExteriorColorXor() {
        Var exteriorColors = get(IVarGuesser.ExteriorColor);
        return exteriorColors.getChildNodesAsXor();
    }

    public Xor getInteriorColorXor() {
        Var interiorColors = get(IVarGuesser.InteriorColor);
        return interiorColors.getChildNodesAsXor();
    }


    public Set<Iff> getModelCodeIffs() {
        Set<Iff> iffs = new HashSet<Iff>();
        Var modelCodes = get(IVarGuesser.ModelCode);
        for (Var modelCode : modelCodes.getChildVars()) {
            for (BoolExpr ec : extraConstraints) {
                if (ec.isIff()) {
                    if (ec.getExpr1().equals(modelCode)) {
                        iffs.add(ec.asIff());
                    } else if (ec.getExpr2().equals(modelCode)) {
                        iffs.add(new Iff(ec.getExpr2(), ec.getExpr()));
                    }
                }
            }
        }
        return iffs;
    }

    public Collection<Var> expandModelCode(Var modelCode) {
        return getIffVarsForVar(modelCode);
    }

    public Collection<Var> expandModelCode(String modelCode) {
        return getIffVarsForVar(get(modelCode));
    }

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

//    public Csp createLeafOnlyCopy() {
//
//        Expressions xors = new Expressions();
//
//        List<Var> xorParents = this.getAllXorParents();
//        List<Var> leafVars = this.getAllLeafsNotIncludingXorChildVars();
//
//        VarsDb newVars = new VarsDb();
//        Var newRoot = newVars.getRootVar();
//
//        int i = 0;
//        //first process xorParents and xorChild
//        for (Var oldXorParent : xorParents) {
//            Var newXorParent = oldXorParent.createLeanCopy(newRoot, i);
//
//            Expressions xorChildren = new Expressions();
//            for (Var oldChild : oldXorParent.getChildVars()) {
//                i++;
//                Var newXorChild = oldChild.createLeanCopy(newXorParent, i);
//                newVars.addLeanVar(newXorChild);
//
//                xorChildren.add(newXorChild);
//            }
//
//
//            xors.add(new Xor(xorChildren));
//
//        }
//
//        //next process non xor leaf vars
//        for (Var oldLeaf : leafVars) {
//            i++;
//            Var newLeaf = oldLeaf.createLeanCopy(newRoot, i);
//            newVars.addLeanVar(newLeaf);
//        }
//
//
////        new Csp(newVars, );
//
//
//        return null;
//    }
//
//    private List<Var> getAllLeafsNotIncludingXorChildVars() {
//        ArrayList<Var> a = new ArrayList<Var>();
//        for (Var var : vars) {
//            if (var.isLeaf() && !var.isXorChild()) {
//                a.add(var);
//            }
//        }
//        return a;
//    }
//
//    private List<Var> getAllXorParents() {
//        ArrayList<Var> a = new ArrayList<Var>();
//        for (Var var : vars) {
//            if (var.isXorParent()) {
//                a.add(var);
//            }
//        }
//        return a;
//    }

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

    public Set<Var> getInitiallyTruePickableVars() {
        HashSet<Var> a = new HashSet<Var>();
        for (Var var : getPickableVars()) {
            if (var.isInitiallyPicked()) {
                a.add(var);
            }
        }
        return a;
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

    public ImmutableSet<Var> rawToPicks(ImmutableSet<String> picksRaw) {
        ImmutableSet.Builder<Var> builder = ImmutableSet.builder();
        for (String varCode : picksRaw) {
            Var var = getVarOrNull(varCode);
            if (var != null) {
                builder.add(var);
            }
        }
        return builder.build();
    }

    public Assignments fixRaw(ImmutableSet<String> picksRaw) throws AssignmentException {
        ImmutableSet<Var> picks = rawToPicks(picksRaw);
        return fix(picks);
    }

    public Assignments fix(ImmutableSet<Var> picks) throws AssignmentException {
        Csp csp = createCsp(picks);
        csp.propagate();
        if (!csp.isSolved()) {
            csp.fillInInitialPicks();
        }
        AssignmentsSimple assignments = (AssignmentsSimple) csp.getAssignments();
        AssignmentsSimple copy = new AssignmentsSimple(assignments);
        return copy;
    }

    public FixResult fixup(ImmutableSet<Var> picks) {
        try {
            Assignments assignments = this.fix(picks);
            return new FixResult(null, picks, assignments, null);
        } catch (AssignmentException e) {
            return new FixResult(null, picks, null, e);
        }
    }

    public FixResult fixupRaw(ImmutableSet<String> picksRaw) {
        ImmutableSet<Var> picks = rawToPicks(picksRaw);
        try {
            Assignments assignments = fix(picks);
            return new FixResult(picksRaw, picks, assignments, null);
        } catch (AssignmentException e) {
            return new FixResult(picksRaw, picks, null, e);
        }
    }

    public CspSimple createCsp(ImmutableSet<Var> trueVars) {

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


}

