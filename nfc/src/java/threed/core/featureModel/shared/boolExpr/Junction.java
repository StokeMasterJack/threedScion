package threed.core.featureModel.shared.boolExpr;

import threed.core.featureModel.shared.AutoAssignContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;

public abstract class Junction extends HasChildContent<Expressions> {

    protected final LinkedHashSet<BoolExpr> expressions;

    Junction(LinkedHashSet<BoolExpr> expressions) {
        this.expressions = expressions;
    }

    public static boolean containsShallow(Collection<BoolExpr> a, Constant constant) {
        return a.contains(constant);
    }

    public static boolean containsShallow(Collection<BoolExpr> a, Var var) {
        return a.contains(var);
    }

    public boolean containsShallow(Constant e) {
        return containsShallow(getExpressions(), e);
    }

    public boolean containsShallow(Var var) {
        return containsShallow(getExpressions(), var);
    }

    public void accept(BoolExprVisitor visitor) {
        visitor.visit(this);
    }

    abstract public String getSymbol();

    public int getExprCount() {
        return expressions.size();
    }


    public final String toString() {
        if (expressions.size() == 0) {
            return getSimpleName() + "()";
        } else if (expressions.size() == 1) {
            return getSimpleName() + "(" + expressions.iterator().next() + ")";
        } else {
            ArrayList<String> set = new ArrayList<String>();
            for (BoolExpr e : expressions) {
                set.add(e.toString());
            }

            StringBuilder a = new StringBuilder();
            int i = 0;
            for (String e : set) {
                a.append(e);
                if (i != set.size() - 1) {
                    a.append(' ');
                    a.append(getSymbol());
                    a.append(' ');
                }
                i++;
            }
            return "(" + a.toString() + ")";
        }
    }

    public final String toString(AutoAssignContext ctx) {
        if (expressions.size() == 0) {
            return getSimpleName() + "()";
        } else if (expressions.size() == 1) {
            BoolExpr ee = expressions.iterator().next();
            BoolExpr vv = ee.simplify(ctx);
            return getSimpleName() + "(" + vv + ")";
        } else {
            ArrayList<String> set = new ArrayList<String>();
            for (BoolExpr ee : expressions) {
                BoolExpr vv = ee.simplify(ctx);
                set.add(vv.toString());
            }

            StringBuilder a = new StringBuilder();
            int i = 0;
            for (String svv : set) {
                a.append(svv);
                if (i != set.size() - 1) {
                    a.append(' ');
                    a.append(getSymbol());
                    a.append(' ');
                }
                i++;
            }
            return "(" + a.toString() + ")";
        }
    }

    public LinkedHashSet<BoolExpr> getConflicts() {
        return getConflicts(expressions);
    }

    public static LinkedHashSet<BoolExpr> getConflicts(Collection<BoolExpr> expressions) {
        LinkedHashSet<BoolExpr> set = new LinkedHashSet<BoolExpr>();
        for (BoolExpr e1 : expressions) {
            for (BoolExpr e2 : expressions) {
                if (e1 != e2) {
                    Conflict pair = conflict(e1, e2);
                    set.add(pair);
                }
            }
        }
        return set;
    }

    @Override
    public boolean containsDeep(Var var) {
        if (containsShallow(var)) return true;
        for (BoolExpr e : expressions) {
            if (e.containsDeep(var)) return true;
        }
        return false;
    }

    @Override
    public boolean containsVarCodeDeep(String varCode) {
        for (BoolExpr expr : expressions) {
            if (expr.containsVarCodeDeep(varCode)) return true;
        }
        return false;
    }


    @Override
    public int occurranceCount(Var var) {
        int t = 0;
        for (BoolExpr expression : expressions) {
            t += expression.occurranceCount(var);
        }
        return t;
    }

    protected <T extends Junction> T create(Class<T> cls, LinkedHashSet<BoolExpr> a) {
        if (cls == And.class) return (T) new And(a);
        else if (cls == Or.class) return (T) new Or(a);
        else if (cls == Xor.class) return (T) new Xor(a);
        else throw new IllegalStateException();
    }


    public boolean allTermsAre(BoolExpr expr) {
        for (BoolExpr e : expressions) {
            if (!e.equals(expr)) return false;
        }
        return true;
    }


    public boolean containsOnlyVars() {
        for (BoolExpr expr : expressions) {
            Var var = expr.asVar();
            if (var == null) return false;
        }
        return true;
    }

    @Override
    public int getDeepExpressionCount() {
        int c = getExprCount();
        for (BoolExpr boolExpr : expressions) {
            c += boolExpr.getDeepExpressionCount();
        }
        return c;
    }


    @Override
    public BoolExpr getExpr() {
        return expressions.iterator().next();
    }

    public BoolExpr getExpr1() {
        Iterator<BoolExpr> it = expressions.iterator();
        return it.next();
    }

    public BoolExpr getExpr2() {
        Iterator<BoolExpr> it = expressions.iterator();
        it.next();
        return it.next();
    }

    public static boolean containNegatingLiterals(BoolExpr[] a) {
        for (int i = 0; a[i].isConstant() || a[i].isLiteral() && i < a.length - 1; i++) {
            if (BoolExpr.negatingLiterals(a[i], a[i + 1])) return true;
        }
        return false;
    }

    public static boolean containsNegatingLiterals(Collection<BoolExpr> aa) {
        for (BoolExpr e : aa) {
            if (e.isVar() && aa.contains(new Not(e))) return true;
        }
        return false;
    }

    public boolean containNegatingLiterals() {
        return containsNegatingLiterals(this.expressions);
    }


    @Override
    public LinkedHashSet<BoolExpr> getExpressions() {
        return expressions;
    }

    public LinkedHashSet<BoolExpr> getContent() {
        return expressions;
    }

    public BoolExpr cleanOutIffVars(IffContext ctx) {
        LinkedHashSet<BoolExpr> a = new LinkedHashSet<BoolExpr>();
        for (BoolExpr e : expressions) {
            BoolExpr ee = ctx.getReplacement(e);
            a.add(ee.cleanOutIffVars(ctx));
        }
        return create(getClass(), a);
    }


    @Override
    public BoolExpr copy() {
        Expressions newExpressions = new Expressions();
        for (BoolExpr e : this.getExpressions()) {
            BoolExpr eCopy = e.copy();
            newExpressions.add(eCopy);
        }
        return create(getClass(), newExpressions);
    }
}