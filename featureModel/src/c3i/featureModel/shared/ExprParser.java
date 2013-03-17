package c3i.featureModel.shared;

import c3i.featureModel.shared.boolExpr.BoolExpr;
import c3i.featureModel.shared.boolExpr.Conflict;
import c3i.featureModel.shared.boolExpr.Iff;
import c3i.featureModel.shared.boolExpr.Imp;
import c3i.featureModel.shared.boolExpr.Var;

import java.util.LinkedHashSet;

import static smartsoft.util.shared.Strings.isEmpty;

/**
 *
 * For imply or bi-imply, replace spaces with +
 * For conflict, replace spaces with .
 *
 *
 *
 * X+Y+Z
 * X*Y*Z
 *
 *
 * implies="FF GG HH+XX
 *
 *
 */
public class ExprParser {

    private FeatureModel fm;

    public ExprParser(FeatureModel fm) {
        this.fm = fm;
    }

    public BoolExpr parseExpression(BoolExprString exprString) {
        return parseOrs(exprString);
    }

    public BoolExpr parseExpression(String exprString, String source, BoolExprString.Op defaultOperator) {
        return parseOrs(new BoolExprString(exprString, source, defaultOperator));
    }

    public BoolExpr parseExpression(String exprString, String source) {
        return parseExpression(new BoolExprString(exprString, source, BoolExprString.Op.AND));
    }

    public BoolExpr parseExpression(String exprString) {
        return parseExpression(new BoolExprString(exprString, "No source provided", BoolExprString.Op.AND));
    }

    public BoolExpr parseExpression(String exprString, BoolExprString.Op defaultOperator) {
        return parseExpression(new BoolExprString(exprString, "No source provided", defaultOperator));
    }


    /**
     *
     * @param exprString may contain and of the following:
     *      letters
     *      digits
     *      +
     *      *
     *      !
     * @return
     */
    private BoolExpr parseOrs(BoolExprString exprString) {
        exprString.checkValidCharsForOrExpression();

        if (!exprString.containsOrSymbol()) {
            return parseAnds(exprString);
        } else {
            LinkedHashSet<BoolExpr> list = new LinkedHashSet<BoolExpr>();
            for (String expr : exprString.stringValue().split("\\+")) {
                BoolExprString ee = new BoolExprString(expr, exprString.getSource());
                list.add(parseAnds(ee));
            }
            return BoolExpr.or(list);
        }

    }

    /**
     *
     * @param exprString may contain any of the following:
     *      letters
     *      digits
     *      .
     *      !
     */
    private BoolExpr parseAnds(BoolExprString exprString) {
        exprString.checkValidCharsForAndExpression();

        if (!exprString.containsAndSymbol()) {
            return parseLiteral(exprString);
        } else {
            LinkedHashSet<BoolExpr> list = new LinkedHashSet<BoolExpr>();

            for (String e : exprString.stringValue().split("\\*")) {
                BoolExprString ee = new BoolExprString(e, exprString.getSource());
                list.add(parseLiteral(ee));
            }
            return BoolExpr.and(list);
        }

    }

    /**
     *
     * @param exprString  may contain a varCode or a varCode prefixed by a bang (!)
     */
    private BoolExpr parseLiteral(BoolExprString exprString) {
        BoolExprString.Literal literal = exprString.parseLiteral();
        Var var = fm.getVar(literal.getVarCode());
        if (literal.isNegated()) {
            return BoolExpr.not(var);
        } else {
            return var;
        }
    }

    public Iff createIff(String varCode, BoolExprString boolExprString, VarSpace varSpace) {
        if (isEmpty(varCode)) throw new IllegalArgumentException("varCode cannot be empty");
        Var expr1 = varSpace.getVar(varCode);
        BoolExpr expr2 = this.parseExpression(boolExprString);
        return BoolExpr.iff(expr1, expr2);
    }


    public Imp createImplication(String varCode, BoolExprString exprString, VarSpace varSpace) {
        if (isEmpty(varCode)) throw new IllegalArgumentException("varCode cannot be empty");
        Var expr1 = varSpace.getVar(varCode);
        BoolExpr expr2 = this.parseExpression(exprString);
        return BoolExpr.imp(expr1, expr2);
    }

    public Conflict createConflict(String varCode, BoolExprString exprString, VarSpace varSpace) {
        if (isEmpty(varCode)) throw new IllegalArgumentException("varCode cannot be empty");
        Var expr1 = varSpace.getVar(varCode);
        BoolExpr expr2 = this.parseExpression(exprString);
        return BoolExpr.conflict(expr1, expr2);
    }


}
