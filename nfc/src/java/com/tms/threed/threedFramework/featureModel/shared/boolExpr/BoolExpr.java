package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

import com.tms.threed.threedFramework.featureModel.shared.AutoAssignContext;
import com.tms.threed.threedFramework.featureModel.shared.EvalContext;
import com.tms.threed.threedFramework.featureModel.shared.Tri;
import com.tms.threed.threedFramework.util.lang.shared.Strings;

import javax.annotation.Nonnull;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.prindent;


/**
 * All BoolExpr subtypes are immutable with a few exceptions:
 * var.extraId
 * var.childVars
 * var.cardinality
 * <p/>
 * Use flyweight pattern
 */
public abstract class BoolExpr implements Tri {

    public static final True TRUE = True.getInstance();
    public static final False FALSE = False.getInstance();

    //debugging stuff
    public static boolean debugMode;

    abstract public void accept(BoolExprVisitor visitor);


    public Map<Var, Tri> getVarState(AutoAssignContext ctx) {
        HashMap<Var, Tri> map = new HashMap<Var, Tri>();
        Set<Var> careVars = getCareVars();
        for (Var var : careVars) {
            map.put(var, ctx.getValue(var));
        }
        return map;
    }

    public static Map<Var, Tri> getVarState(AutoAssignContext ctx, BoolExpr e) {
        HashMap<Var, Tri> map = new HashMap<Var, Tri>();
        Set<Var> careVars = e.getCareVars();
        for (Var var : careVars) {
            map.put(var, ctx.getValue(var));
        }
        return map;
    }



    public Var asVar() {
        return null;
    }

    public boolean isVar() {
        return this instanceof Var;
    }

    public boolean isVar(String varCode) {
        return isVar() && asVar().getCode().equals(varCode);
    }

    public boolean isNot() {
        return this instanceof Not;
    }

    public List<Var> isConjunctionOfVars() {
        return null;
    }

    public abstract BoolExpr simplify(AutoAssignContext ctx);

    public abstract String toString(AutoAssignContext ctx);

    abstract public BoolExpr toCnf(AutoAssignContext ctx);
//
//    public static boolean isJunction(String type) {
//        return type.equals(TYPE_JUNCTION);
//    }
//
//    public static boolean isPair(String type) {
//        return type.equals(TYPE_PAIR);
//    }
//
//    public static boolean isVar(String type) {
//        return type.equals(TYPE_VAR);
//    }
//
//    public static boolean isNot(String type) {
//        return type.equals(TYPE_NOT);
//    }
//
//    public static boolean isAnd(String subtype) {
//        return subtype.equals(JUNCTION_SUBTYPE_AND);
//    }
//
//    public static boolean isOr(String subtype) {
//        return subtype.equals(JUNCTION_SUBTYPE_OR);
//    }
//
//    public static boolean isXor(String subtype) {
//        return subtype.equals(JUNCTION_SUBTYPE_XOR);
//    }
//
//    public static boolean isImplication(String subtype) {
//        return subtype.equals(PAIR_SUBTYPE_IMPLICATION);
//    }
//
//    public static boolean isIff(String subtype) {
//        return subtype.equals(PAIR_SUBTYPE_IFF);
//    }
//
//    public static boolean isConflict(String subtype) {
//        return subtype.equals(PAIR_SUBTYPE_CONFLICT);
//    }

    public Set<Var> getCareVars() {
        return GetCareVarsVisitor.getCareVars(this);
    }

    public Set<Var> getLeafVars() {
        GetLeafVarsVisitor visitor = new GetLeafVarsVisitor();
        accept(visitor);
        return visitor.getLeafVars();
    }


    public boolean referencesAnyNonLeafVars() {
        CheckForNonLeafReferenceVisitor visitor = new CheckForNonLeafReferenceVisitor();
        accept(visitor);
        return visitor.getResponse();
    }

    public boolean dependsOn(Var var) {
        DependsOnVisitor visitor = new DependsOnVisitor(var);
        accept(visitor);
        return visitor.getResponse();
    }

//    private static final String TYPE_JUNCTION = getSimpleName(Junction.class);
//    private static final String TYPE_PAIR = getSimpleName(Pair.class);
//    private static final String TYPE_VAR = getSimpleName(Var.class);
//    private static final String TYPE_NOT = getSimpleName(Not.class);
//
//    private static final String JUNCTION_SUBTYPE_AND = getSimpleName(And.class);
//    private static final String JUNCTION_SUBTYPE_OR = getSimpleName(Or.class);
//    private static final String JUNCTION_SUBTYPE_XOR = getSimpleName(Xor.class);
//
//    private static final String PAIR_SUBTYPE_IMPLICATION = getSimpleName(Implication.class);
//    private static final String PAIR_SUBTYPE_IFF = getSimpleName(Iff.class);
//    private static final String PAIR_SUBTYPE_CONFLICT = getSimpleName(Conflict.class);

    public abstract int getExprCount();

    public abstract int getDeepExpressionCount();

    public abstract Collection<BoolExpr> getExpressions();

    public BoolExpr getExpr1() {
        throw new UnsupportedOperationException();
    }

    public BoolExpr getExpr2() {
        throw new UnsupportedOperationException();
    }

    public BoolExpr getExpr() {
        throw new UnsupportedOperationException();
    }

    public boolean isConstant() {
        return this instanceof Constant;
    }

    public boolean isNonConstant() {
        return !isConstant();
    }

    public boolean isTrue() {
        return this instanceof True;
    }

    public boolean isFalse() {
        return this instanceof False;
    }


    public abstract boolean containsShallow(Constant c);

    public abstract boolean containsShallow(Var v);

    public abstract boolean containsDeep(Var v);

    public abstract boolean containsVarCodeDeep(String varCode);


    public boolean isAnd() {
        return this instanceof And;
    }

    public boolean isOr() {
        return this instanceof Or;
    }

    public boolean isXor() {
        return this instanceof Xor;
    }

    public static Conflict conflict(BoolExpr e1, BoolExpr e2) {
        return new Conflict(e1, e2);
    }

    public static Imp imp(BoolExpr e1, BoolExpr e2) {
        return new Imp(e1, e2);
    }

    public static Iff iff(BoolExpr e1, BoolExpr e2) {
        return new Iff(e1, e2);
    }

    public static LinkedHashSet<BoolExpr> toLinkedHashSet(BoolExpr[] a) {
        LinkedHashSet<BoolExpr> set = new LinkedHashSet<BoolExpr>();
        for (BoolExpr expr : a) {
            set.add(expr);
        }
        return set;
    }

    public static And and(LinkedHashSet<BoolExpr> expressions) {
        return new And(expressions);
    }

    public static And and(BoolExpr... expressions) {
        return new And(toLinkedHashSet(expressions));
    }

    public static Or or(LinkedHashSet<BoolExpr> expressions) {
        return new Or(expressions);
    }

    public static Or or(BoolExpr... expressions) {
        return new Or(toLinkedHashSet(expressions));
    }

    public static Xor xor(LinkedHashSet<BoolExpr> expressions) {
        return new Xor(expressions);
    }

    public static Xor xor(BoolExpr... expressions) {
        return new Xor(toLinkedHashSet(expressions));
    }

    public static Not not(BoolExpr expr) {
        return new Not(expr);
    }

    public static MessageDigest getDigestBuilder() {
        try {
            return MessageDigest.getInstance("sha");
//            return MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean containsPngVar() {
        throw new UnsupportedOperationException();
    }

    public boolean isPngVar() {
        return false;
    }

    abstract public int occurranceCount(Var var);

    public boolean isImp() {
        return this instanceof Imp;
    }

    public Imp asImp() {
        return null;
    }

    public Conflict asConflict() {
        return (Conflict) this;
    }

    public boolean isIff() {
        return this instanceof Iff;
    }

    public boolean isConflict() {
        return this instanceof Conflict;
    }

    public Iff asIff() {
        return (Iff) this;
    }

    public And asAnd() {
        return (And) this;
    }

    public Or asOr() {
        return (Or) this;
    }

    public Not asNot() {
        return (Not) this;
    }

    public Constant asConstant() {
        return (Constant) this;
    }

    public NonConstant asNonConstant() {
        return (NonConstant) this;
    }


    public boolean isOpen() {
        return isNonConstant();
    }

    /**
     * Must fail fast
     */
    @Nonnull
    abstract public Tri eval(EvalContext ctx);

    /**
     * Propagate variable assignments. Must fail fast
     */
    public abstract void autoAssignTrue(AutoAssignContext ctx, int depth) throws AssignmentException;


    /**
     * Must fail fast
     */
    public abstract void autoAssignFalse(AutoAssignContext ctx, int depth) throws AssignmentException;

    public abstract BoolExpr cleanOutIffVars(IffContext ctx);

    public boolean isPair() {
        return this instanceof Pair;
    }


    public Pair asPair() {
        return (Pair) this;
    }

    public abstract Type getType();

    public abstract static class Digest {

        /**
         * Assume array is pre-sorted
         */
        public static int createContentDigest(byte type, BoolExpr[] expressions) {
            assert expressions != null;
            MessageDigest md = getDigestBuilder();
            md.reset();
            md.update(type);
            for (int i = 0; i < expressions.length; i++) {
                md.update(expressions[i].getHash());
            }
            return bytesToInt(md.digest());
        }

        /**
         * assume expr1 and expr2 are pre-sorted
         */
        public static int createContentDigest(byte type, BoolExpr e1, BoolExpr e2) {
            MessageDigest md = getDigestBuilder();
            md.reset();
            md.update(type);
            md.update(e1.getHash());
            md.update(e2.getHash());
            return bytesToInt(md.digest());
        }

        public static int createContentDigest(byte type, BoolExpr e) {
            MessageDigest md = getDigestBuilder();
            md.reset();
            md.update(type);
            md.update(e.getHash());
            return bytesToInt(md.digest());
        }

        public static byte[] intToBytes(int index) {
            byte[] a = new byte[4];
            a[0] = (byte) ((index >>> 24) & 0xFF);
            a[1] = (byte) ((index >>> 16) & 0xFF);
            a[2] = (byte) ((index >>> 8) & 0xFF);
            a[3] = (byte) ((index >>> 0) & 0xFF);
            return a;
        }

        /**
         * Only grabs 1st 4 elements. Ignores any elements after that - this is for memory savings
         */
        public static int bytesToInt(byte[] a) {
            int b1, b2, b3, b4;

            b1 = (a[0] << 24) & 0xFF000000;
            b2 = (a[1] << 16) & 0x00FF0000;
            b3 = (a[2] << 8) & 0x0000FF00;
            b4 = (a[3] << 0) & 0x000000FF;

            return (b1 | b2 | b3 | b4);
        }


    }

    public byte[] getHash() {
        throw new UnsupportedOperationException();
    }

    public boolean isLiteral() {
        return false;
    }

    public boolean isNegatedVar() {
        return false;
    }

    public boolean isClause() {
        return false;
    }

    public boolean isCnf() {
        return false;
    }

    public boolean isFixupImplication() {
        if (!isImplyish()) return false;
        Imp implication = asImp();
        return implication.getExpr2().isVar();
    }

    public boolean isFixupImplicationForVar(Var var) {
        if (!isImplyish()) return false;
        Imp implication = asImp();
        return implication.getExpr2().equals(var);
    }


    /**
     * Only applies to And
     *
     * @return
     */
    public BoolExpr replaceComplimentImplicationsWithIff() {
        throw new UnsupportedOperationException();
    }

    public boolean isImplyish() {
        return false;
    }

    public static boolean negatingLiterals(BoolExpr x, BoolExpr y) {
        return x.isVar() && y.isNegatedVar() && y.getExpr().equals(x);
    }

//    public int getSort1() {
//        if (isConstant()) return 1;
//        else if (isLiteral()) return 2;
//        else return 3;
//    }
//
//    public int getSort2() {
//        if (isConstant()) return getType();
//        else if (isLiteral()) return asVar().index;
//        else return 0;
//    }
//
//    public int getSort3() {
//        if (isLiteral()) {
//            if (isVar()) return 1;
//            else if (isNot()) return 2;
//            else throw new IllegalStateException();
//        } else {
//            return 0;
//        }
//    }

//    public abstract void replaceVarsWithConstants(SimplifyContext ctx);

    public String getSimpleName() {
        return Strings.getSimpleName(getClass());
    }

    public static boolean allConstants(Tri... expressions) {
        assert expressions.length > 0;
        for (Tri e : expressions) {
            if (e.isOpen()) return false;
        }
        return true;
    }

    public static boolean allOpen(Tri... expressions) {
        assert expressions.length > 0;
        for (Tri e : expressions) {
            if (e.isConstant()) return false;
        }
        return true;
    }

    @Override
    public boolean isAssigned() {
        return isConstant();
    }

    public boolean boolValue() {
        if (isTrue()) return true;
        if (isFalse()) return false;
        throw new IllegalStateException();
    }

    public void printTree() {
        printTree(0);
    }

    public void printTree(int depth) {
        if (isVar()) {
            prindent(depth, asVar().getCode());
        } else {
            if (depth > 0) {
                prindent(depth, this.toString());
            } else {
                prindent(depth, getSimpleName());
                for (BoolExpr e : getExpressions()) {
                    e.printTree(depth + 1);
                }
            }
        }

    }

    public abstract BoolExpr copy();

    public static boolean logAutoAssignments = false;

    public void logAutoAssignTrue(int depth) {
        if (logAutoAssignments) {
            log(depth, "AutoAssign " + this + " true");
        }
    }

    public void logAutoAssignFalse(int depth) {
        if (logAutoAssignments) {
            log(depth, "AutoAssign " + this + " false");
        }
    }

    public static void log(int depth, String msg) {
        System.err.println(Strings.indent(depth) + msg);
    }

}
