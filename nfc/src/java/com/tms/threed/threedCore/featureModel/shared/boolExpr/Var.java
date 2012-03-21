package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.*;
import com.tms.threed.threedCore.featureModel.shared.picks.Picks;
import smartsoft.util.lang.shared.Strings;

import java.util.*;

import static smartsoft.util.lang.shared.Strings.notEmpty;

public class Var extends NonConstant {

    public static final Type TYPE = Type.Var;

    public final int index;

    private final String name;


    private final String code;
    private final Var parent;
    private List<Var> childVars;
    private Cardinality cardinality;
    private Boolean mandatory;
    private Boolean derived;
    private Boolean defaultValue;


    private Integer leafRelationCount;

    private final int hash;


    Var(Var parent, int index, String code, String name) {
        assert notEmpty(code);

        this.index = index;
        this.parent = parent;

        this.code = code;
        this.name = name;

        hash = 31 * TYPE.id + index;
    }

    public boolean isInitiallyPicked() {
        if (isFirstPickOneChild() && !anySiblingsDefaultedToTrue()) {
            return true;
        }
        Boolean defaultValue = getDefaultValue();
        return defaultValue != null && defaultValue;
    }

    public Type getType() {
        return TYPE;
    }

    //    private Var(Var parent, int index, Var that) {
//
//        if (parent.isRoot()) {
//            this.parent = parent;
//        } else if (parent.isXorParent()) {
//            this.parent = parent;
//            parent.addChild(this);
//
//
//        } else {
//            throw new IllegalStateException();
//        }
//
//        if (that.hasChildVars()) {
//            assert that.isXorParent() || that.isRoot();
//        }
//
//        this.index = index;
//
//        this.code = that.code;
//        this.name = that.name;
//        this.cardinality = that.cardinality;
//        this.mandatory = that.mandatory;
//        this.derived = that.derived;
//        this.defaultValue = that.defaultValue;
//    }
//
//    Var createLeanCopy(Var newParent, int newIndex) {
//        return new Var(newParent, newIndex, this);
//    }


    public boolean isTrimChild() {
        if (parent == null) return false;
        return parent.isTrimGroup();
    }

    @Override
    public boolean containsShallow(Constant c) {
        return false;
    }

    @Override
    public boolean containsShallow(Var v) {
        return this.equals(v);
    }

    @Override
    public boolean containsDeep(Var v) {
        return this.equals(v);
    }

    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }

    public String getComputedName() {
        if (Strings.isEmpty(name)) return code;
        else return name;
    }

    private VarsDb.RootVar getRootVar() {
        if (isRoot()) return (VarsDb.RootVar) this;
        else return getParent().getRootVar();
    }

    public Var addChild(String code, String name) {
        Var newChildVar = getRootVar().newVar(this, code, name);
        if (childVars == null) childVars = new ArrayList<Var>();
        childVars.add(newChildVar);
        return newChildVar;
    }

    public Var addChild(String code) {
        return addChild(code, code);
    }

//    public Var addVar(Var var) {
//        Var newChildVar = var.copy(parent);
//        if (childVars == null) childVars = new ArrayList<Var>();
//        childVars.add(newChildVar);
//        return newChildVar;
//    }
//
//    private Var copy(Var newParent) {
//        System.out.println("Var.copy(..)");
//        if (this.index == -1) throw new IllegalStateException();
//        Var cp = new Var(newParent, this.code, this.name);
//        cp.index = index;
//
//        if (hasChildVars()) {
//            for (Var childVar : childVars) {
//                cp.addVar(childVar);
//            }
//        }
//        return cp;
//    }

    public Var getParent() {
        return parent;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        if (notEmpty(name)) {
            return name;
        } else {
            return code;
        }
    }

    @Override
    public String toString() {
        return code;
    }

    @Override
    public String toString(AutoAssignContext ctx) {
        return simplify(ctx).toString();
    }

    @Override
    public Var asVar() {
        return this;
    }


    public boolean isRoot() {
        return parent == null;
    }

    public int getDepth() {
        if (isRoot()) return 0;
        else return parent.getDepth() + 1;
    }

    public String indent() {
        return indent(getDepth());
    }

    protected static void indent(int tabCount, Object thingToPrint) {
        System.out.println(indent(tabCount) + thingToPrint);
    }

    protected static String indent(int tabCount) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < tabCount; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

    public void print() {
        int depth = getDepth();
        indent(depth, getName());
        printChildNodes();
    }

    public void printChildNodes() {
        if (childVars != null) {
            for (Var child : childVars) {
                child.print();
            }
        }
    }

    public List<Var> getChildVars() {
        return childVars;
    }

    public List<Var> getLeafChildNodes() {
        ArrayList<Var> leafs = new ArrayList<Var>();
        for (Var child : childVars) {
            if (child.isLeaf()) leafs.add(child);
        }
        return leafs;
    }

    public int getChildCount() {
        return childVars == null ? 0 : childVars.size();
    }

    public int getVarCount() {
        int count = 1;
        if (childVars != null) {
            for (Var childVar : childVars) {
                count += childVar.getVarCount();
            }
        }
        return count;
    }

    public boolean isDerived() {
        if (this.derived != null) return this.derived;
        if (isRoot()) return false;
        return getParent().isDerived();
    }

    public boolean isVisible() {
        return !isDerived();
    }


    public boolean isZeroOrMoreGroup() {
        return cardinality != null && cardinality.equals(Cardinality.ZeroOrMoreGroup);
    }

    public Cardinality getCardinality() {

        if (cardinality == null && (isTrimGroup() | isColorGroup())) return Cardinality.AllGroup;
        return cardinality;
    }

    public boolean isXorParent() {
        if (cardinality == null) return false;
        if (!cardinality.equals(Cardinality.PickOneGroup)) return false;
        if (childVars == null || childVars.size() == 0) {
            throw new IllegalStateException("Var [" + this.getLabel() + "] has Cardinality=PickOneGroup but has childCount=[" + childVars.size() + "]");
        }
        return true;
    }

    public boolean isAllGroup() {
        if (cardinality == null) return false;
        return cardinality.equals(Cardinality.AllGroup);
    }

    public boolean isOptional() {
        return !getMandatory();
    }

    public boolean isXorChild() {
        if (parent == null) return false;
        return parent.isXorParent();
    }

    public boolean isAllGroupChild() {
        if (parent == null) return false;
        return parent.isAllGroup();
    }

    public void setDerived(Boolean newValue) {
        this.derived = newValue;
    }

//    public BDDVarSet getChildNodesAsVarSet() {
//        And a = fm.and();
//        a.add(getChildNodes());
//        return a.getBdd().toVarSet();
//    }

    public boolean isLeaf() {
        return childVars == null || childVars.size() == 0;
    }

    public boolean hasChildVars() {
        return childVars != null && childVars.size() > 0;
    }

    public void printVarTree() {
        printVarTree(0);
    }

    public String getLabel() {
        if (Strings.isEmpty(name)) return code;
        if (Strings.isEmpty(code)) throw new IllegalStateException();
        if (code.equalsIgnoreCase(name)) return name;
        else return code + ":" + name;
    }

    public void printVarTree(int depth) {
        printVarTree(depth, null);
    }


    public void printVarTree(int depth, Picks picks) {
        String atts = "[" + ((mandatory != null && mandatory) ? "mandatory" : "") + ((defaultValue != null && defaultValue) ? "defaultValue[true]" : "") + ((derived != null && derived) ? "derived" : "") + ((cardinality != null) ? cardinality : "") + "]";

        System.out.println(getIndent(depth) + getDepth() + ":" + getLabel() + (picks != null ? ": " + picks.getValue(this) : "") + "  " + atts);
        final List<Var> childVars = getChildVars();
        if (childVars != null) {
            for (Var childVar : childVars) {
                childVar.printVarTree(depth + 1, picks);
            }
        }
    }

    private static String getIndent(int depth) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < depth; i++) {
            sb.append("\t");
        }
        return sb.toString();
    }

//    public boolean fixup2(BDDFactory f) {
//        if (isAssigned()) return false;
//
//        final BDD bdd = fm.bdd(f);
//
//        final BDD v = bdd(f);
//        final BDD nv = nbdd(f);
//
//        final long pSatCount = (long) bdd.restrict(v).satCount();
//        final long npSatCount = (long) bdd.restrict(nv).satCount();
//
//        if (pSatCount == 0 && npSatCount > 0) {
//            fm.assign(this, Bit.NOT_PICKED);
//            return true;
//        } else if (pSatCount > 0 && npSatCount == 0) {
//            fm.assign(this, Bit.PICKED);
//            return true;
//        } else if (pSatCount > 0 && npSatCount > 0) {
//            return false;
//        } else if (pSatCount == 0 && npSatCount == 0) {
//            throw new IllegalStateException("WARN: NO POSSIBLE SOLUTIONS FOR VAR[" + getCode() + "]");
//        } else {
//            throw new IllegalStateException();
//        }
//
//    }


    public boolean getMandatory() {
        return mandatory != null && mandatory;
    }

    public boolean hasMandatory() {
        return mandatory != null;
    }

    public void setMandatory(Boolean mandatory) {
        this.mandatory = mandatory;
    }

    public void setCardinality(Cardinality cardinality) {
        this.cardinality = cardinality;
    }

    public boolean hasCardinality() {
        return cardinality != null;
    }

    public boolean isDescendantOf(Var var) {
        if (isRoot()) return false;
        if (var == parent) return true;
        return parent.isDescendantOf(var);
    }


    public Set<Var> getRelatedVars(FeatureModel fm) {
        Set<BoolExpr> extraConstraints = getExtraConstraints(fm);
        GetCareVarsVisitor getVars = new GetCareVarsVisitor();
        for (BoolExpr expr : extraConstraints) {
            expr.accept(getVars);
        }
        return getVars.getCareSet();
    }

    public void initLeafRelationCount(FeatureModel fm) {
        if (leafRelationCount == null) {
            leafRelationCount = getRelatedLeafVars(fm).size();
            if (hasChildVars()) {
                for (Var childVar : childVars) {
                    childVar.initLeafRelationCount(fm);
                }
            }
        }
    }

    public Set<Var> getRelatedLeafVars(FeatureModel fm) {
        Set<BoolExpr> extraConstraints = getExtraConstraints(fm);

        GetLeafVarsVisitor getVars = new GetLeafVarsVisitor();

        for (BoolExpr expr : extraConstraints) {
            expr.accept(getVars);
        }

        return getVars.getLeafVars();
    }

    public Set<BoolExpr> getTreeConstraints(FeatureModel fm) {
        Collection<BoolExpr> treeConstraints = fm.getTreeConstraint().getExpressions();
        Set<BoolExpr> set = new HashSet<BoolExpr>();
        for (BoolExpr constraint : treeConstraints) {
            if (constraint.dependsOn(this)) {
                set.add(constraint);
            }
        }
        return set;
    }

    public Set<BoolExpr> getExtraConstraints(FeatureModel fm) {
        Collection<BoolExpr> extraConstraints = fm.getExtraConstraint().getExpressions();
        Set<BoolExpr> set = new HashSet<BoolExpr>();
        for (BoolExpr constraint : extraConstraints) {
            if (constraint.dependsOn(this)) {
                set.add(constraint);
            }
        }
        return set;
    }

    public int getExtraConstraintCount(FeatureModel fm) {
        return this.getExtraConstraints(fm).size();
    }

    public boolean referencesAnyNonLeafVars() {
        if (!isLeaf()) {
//            System.out.println(this + " references a [IS A] non-leaf var");
            return true;
        }
        return false;
    }

    public boolean hasAnyNonLeafChildNodes() {
        if (childVars == null) return false;
        for (Var child : childVars) {
            if (!child.isLeaf()) return true;
        }
        return false;
    }

    public Xor getChildNodesAsXor() {
        assert isXorParent();
        assert childVars != null;
        assert childVars.size() != 0;
        LinkedHashSet<BoolExpr> set = new LinkedHashSet<BoolExpr>();
        for (Var childVar : getChildVars()) {
            set.add(childVar);
        }
        return xor(set);
    }

    public List<Var> getDescendantLeafs() {
        List<Var> list = new ArrayList<Var>();
        getDescendantLeafs(list);
        return list;
    }

    private void getDescendantLeafs(List<Var> list) {
        if (childVars == null) return;
        for (Var child : childVars) {
            if (child.isLeaf()) {
                list.add(child);
            } else {
                child.getDescendantLeafs(list);
            }
        }
    }

    public List<Var> getSelfPlusDescendants(boolean leafOnly) {
        List<Var> list = new ArrayList<Var>();
        getSelfPlusDescendantVars(list, leafOnly);
        return list;
    }

    private void getSelfPlusDescendantVars(List<Var> list, boolean leafOnly) {
        if (!leafOnly || isLeaf()) list.add(this);
        if (childVars == null) return;
        for (Var child : childVars) {
            child.getSelfPlusDescendantVars(list, leafOnly);
        }
    }

    private void sortChildVarsByLeafRelationCount() {
        if (childVars != null) {
            Collections.sort(childVars, VAR_COMPARATOR);
        }
    }

    public void sortDescendantVarsByLeafRelationCount() {
        sortChildVarsByLeafRelationCount();
        if (hasChildVars()) {
            for (Var child : childVars) {
                child.sortDescendantVarsByLeafRelationCount();
            }
        }
    }

    public Not getCompliment() {
        return BoolExpr.not(this);
    }

    public boolean isUnconstrained(FeatureModel fm) {
        return getExtraConstraintCount(fm) == 0;
    }

    public void fixupAssignDefaultForUi(AutoAssignContext ctx) {
        System.out.println("Var.fixupAssignDefaultForUi");
        boolean defVal = getDefaultValueForUi();
        if (defVal) {
            System.out.println("TRUE assignTrue - default value for var[" + this + "] ");
            ctx.assignTrue(this);
        } else {
            System.out.println("FALSE assignFalse - default value for var[" + this + "] ");
            ctx.assignFalse(this);
        }
    }

    public void initialAssignDefault(Picks picks) {
        boolean defVal = getDefaultValueForUi();
        if (defVal) initialAssign(picks, defVal);
    }

    public Boolean getDefaultValue() {
        return defaultValue;
    }

    public boolean getDefaultValueForUi() {
        if (defaultValue == null) {
            boolean firstPickOneChild = isFirstPickOneChild();
//            System.out.println(this + " [  firstPickOneChild:" + firstPickOneChild + ", derived:"+ derived + ", defaultValue:"+ defaultValue + "]");
            return firstPickOneChild;
        } else {
            return defaultValue;
        }
    }

    public boolean isFirstPickOneChild() {
        return isXorChild() && isFirstChild();
    }

    public boolean anySiblingsDefaultedToTrue() {
        List<Var> siblings = getSiblings();
        for (Var sibling : siblings) {
            Boolean defaultValue = sibling.getDefaultValue();
            if (defaultValue != null && defaultValue) {
                return true;
            }
        }
        return false;
    }

    private boolean isFirstChild() {
        return parent.childVars.indexOf(this) == 0;
    }

    public void setDefaultValue(Boolean defaultValue) {
        this.defaultValue = defaultValue;
    }

    public boolean isAssigned(Picks picks) {
        return picks.isAssigned(this);
    }

    public Tri getValue(Picks picks) {
        return picks.getValue(this);
    }

    public boolean isAccessory(FeatureModel fm) {
        Var accessoriesVar = fm.getAccessoriesVar();
        return isDescendantOf(accessoriesVar);
    }

    public boolean isAccessoriesGroup() {
        return code.equalsIgnoreCase(IVarGuesser.Accessories);
    }

    public boolean isTrimGroup() {
        return code.equalsIgnoreCase(IVarGuesser.Trim);
    }

    public boolean isColorGroup() {
        return code.equalsIgnoreCase(IVarGuesser.Color);
    }

    public Var findVar(int varIndex) {
        if (this.index == -1) throw new IllegalStateException();
        if (this.index == varIndex) return this;
        if (childVars != null) {
            for (Var var : childVars) {
                Var v = var.findVar(varIndex);
                if (v != null) return v;
            }
        }
        return null;
    }

    public Var findVar(String code) {
        assert notEmpty(code);
        if (this.code.equals(code)) return this;
        if (childVars != null) {
            for (Var var : childVars) {
                Var d = var.findVar(code);
                if (d != null) return d;
            }
        }
        throw new UnknownVarCodeException(code);
    }

    public void removeFromParent() {
        parent.childVars.remove(this);
    }

    public List<Var> getSiblings() {
        if (isRoot()) throw new IllegalStateException("Root has no siblings");

        Var parent = getParent();
        List<Var> allChildren = parent.getChildVars();

        int siblingCount = allChildren.size() - 1;

        List<Var> siblings = new ArrayList<Var>(siblingCount);

        for (Var child : allChildren) {
            if (child == this) continue; //i am not my own sibling
            siblings.add(child);
        }
        return siblings;
    }

    public final Not compliment = new Not(this);

    public boolean isCompliment(BoolExpr e) {
        return e.equals(compliment);
    }

    public boolean childOf(Var maybeParent) {
        return this.parent != null && this.parent.equals(maybeParent);
    }

    public boolean hasSameParentAs(Var that) {
        if (this.parent == null && that.parent == null) return true;
        if (this.parent == null || that.parent == null) return false;
        return this.parent.equals(that.parent);
    }

    public boolean isModelCodeXorChild() {
        if (parent == null) return false;
        return (parent.isModelCodeXorParent());
    }

    public boolean isExteriorColorXorChild() {
        if (parent == null) return false;
        return (parent.isExteriorColorXorParent());
    }

    public boolean isInteriorColorXorChild() {
        if (parent == null) return false;
        return (parent.isInteriorColorXorParent());
    }

//    public short getBddVar() {
//        return bddVar;
//    }
//
//    public boolean isBddVarInitialized() {
//        return bddVar > 0;
//    }
//
//    public void setBddVar(short bddVar) {
//        assert this.bddVar == -1;
//        assert bddVar != -1;
//        this.bddVar = bddVar;
//    }
//
//    public void bddReset() {
//        bddVar = -1;
//        if (childVars != null) {
//            for (Var childVar : childVars) {
//                childVar.bddReset();
//            }
//        }
//    }

    private static class VarComparator implements Comparator<Var> {
        @Override
        public int compare(Var v1, Var v2) {
            Integer c1 = v1.leafRelationCount;
            Integer c2 = v2.leafRelationCount;
            return c2.compareTo(c1);
        }
    }

    public void initialAssign(Picks picks, boolean newValue) {
        picks.initialAssign(this, newValue);
    }


    @Override
    public void autoAssignTrue(AutoAssignContext ctx, int depth) {
        if (ctx instanceof Assignments) {

        }
        ctx.assignTrue(this, depth);
    }

    @Override
    public void autoAssignFalse(AutoAssignContext ctx, int depth) {
        ctx.assignFalse(this, depth);
    }


    //eval_9
    @Override
    public Tri eval(EvalContext ctx) {
        return ctx.getValue(this);
    }

    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {

        BoolExpr retVal;
        Tri v = ctx.getValue(this);
        if (v.isTrue()) {
            retVal = TRUE;
        } else if (v.isFalse()) {
            retVal = FALSE;
        } else {
            retVal = this;
        }


        return retVal;
    }


//    @Override public int compareTo(Var that) {
//        return this.code.compareTo(that.code);
//    }

    private static class VarCodeComparator implements Comparator<Var> {

        @Override
        public int compare(Var v1, Var v2) {
            return v1.code.compareTo(v2.code);
        }

    }


    public static final Comparator<Var> VAR_COMPARATOR = new Comparator<Var>() {

        @Override
        public int compare(Var v1, Var v2) {
            return v1.getBddSortFactor().compareTo(v2.getBddSortFactor());
        }

    };

    private String getBddSortFactor0(PngVarFilter filter) {
        if (filter.isPngVar(this)) return "aPngVar";
        else return "bPngVar";
    }

    private String getBddSortFactor1() {
        boolean xChild = isXorChild();
        if (xChild) return "aXor";
        else return "bXor";
    }

    private String getBddSortFactor2() {
        if (isXorChild()) {
            int cc = 100 - parent.getChildCount();
            return Strings.lpad(cc + "", '0', 3);
        } else {
            return "---";
        }
    }

    private String getBddSortFactor3() {
        if (isXorChild()) {
            return code;
//            return Strings.lpad(index + "", '0', 3);
        } else {
            return code;
        }
    }


    public String getBddSortFactor() {
        return getBddSortFactor(null);
    }

    public String getBddSortFactor(PngVarFilter filter) {
        if (filter == null) {
            return getBddSortFactor1() + "-" + getBddSortFactor2() + "-" + getBddSortFactor3();
        } else {
            return getBddSortFactor0(filter) + "-" + getBddSortFactor1() + "-" + getBddSortFactor2() + "-" + getBddSortFactor3();
        }
    }

    public boolean isModelCodeXorParent() {
        if (!isXorParent()) return false;
        return code.equalsIgnoreCase("modelcode");
    }

    private boolean isGradeXorParent() {
        if (!isXorParent()) return false;
        return code.equalsIgnoreCase("grade");
    }

    private boolean isBedXorParent() {
        if (!isXorParent()) return false;
        return code.equalsIgnoreCase("bed");
    }

    private boolean isCabXorParent() {
        if (!isXorParent()) return false;
        return code.equalsIgnoreCase("cab");
    }

    public boolean isExteriorColorXorParent() {
        if (!isXorParent()) return false;
        return code.equalsIgnoreCase("exteriorcolor");
    }

    public boolean isInteriorColorXorParent() {
        if (!isXorParent()) return false;
        return code.equalsIgnoreCase("interiorcolor");
    }

    public boolean isMandatory() {
        if (mandatory != null) return mandatory;
        else if (isTrimGroup() || isColorGroup()) return true;
        return false;
    }

    public Boolean getDerived() {
        return derived;
    }

    @Override
    public BoolExpr toCnf(AutoAssignContext ctx) {
        return this;
    }

    @Override
    public int getExprCount() {
        return 1;
    }

    @Override
    public int getDeepExpressionCount() {
        return 1;
    }


    @Override
    public boolean containsVarCodeDeep(String varCode) {
        return this.getCode().equals(varCode);
    }

    @Override
    public boolean isLiteral() {
        return true;
    }

    public boolean isPngVar() {
        return code.endsWith(".png");
    }

    @Override
    public int occurranceCount(Var var) {
        if (var.equals(this)) return 1;
        else return 0;
    }


//    @Override
//    public void replaceVarsWithConstants(SimplifyContext ctx) {
//        throw new IllegalStateException();
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Var that = (Var) o;
        assert (index == that.index) == (code.equals(that.code));
        return (index == that.index);
    }

    @Override
    public int hashCode() {
        return hash;
    }

    @Override
    public Collection<BoolExpr> getExpressions() {
        return Collections.emptySet();
    }


    @Override
    public BoolExpr cleanOutIffVars(IffContext ctx) {
        return ctx.getReplacement(this);
    }

    @Override
    public BoolExpr copy() {
        return this;
    }
}
