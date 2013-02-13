package c3i.core.featureModel.shared;


import smartsoft.util.shared.Strings;

import java.util.ArrayList;

import static smartsoft.util.shared.Strings.isEmpty;


public class BoolExprString {

    public static enum Op {

        OR('+'),

        AND('*'),

        NOT('!');

        private final char symbol;

        Op(char symbol) {
            this.symbol = symbol;
        }

        public char getSymbol() {
            return symbol;
        }
    }

    private final String source;
    private final Op defaultOperator;

    private final ArrayList<String> history = new ArrayList<String>();

    private final String expr;

    public BoolExprString(String expr, String source, Op defaultOperator) {
        assert Strings.notEmpty(expr);
        assert Strings.notEmpty(source);


        if (defaultOperator == null) {
            this.expr = normalizeWhitespace(expr);
            assert !containsWhitespace();
        } else {
            this.expr = normalizeWhitespace(expr).replace(' ', defaultOperator.getSymbol());
        }

        this.source = source;
        this.defaultOperator = defaultOperator;
    }

    public BoolExprString(String expr, String source) {
        this(expr, source, null);
    }

    public String getExpr() {
        return expr;
    }

    public String getSource() {
        return source;
    }


    public String normalizeWhitespace(String expr) {
        return expr.replaceAll("\\s+", " ")
                .replace(" * ", "*")
                .replace("* ", "*")
                .replace(" *", "*")
                .replace(" + ", "+")
                .replace("+ ", "+")
                .replace(" +", "+");
    }


    private BoolExprString revise(String newExpr) {
        BoolExprString retVal = new BoolExprString(newExpr, source, defaultOperator);
        retVal.history.add(expr);
        return retVal;
    }

    public boolean containsWhitespace() {
        return smartsoft.util.shared.Strings.containsWhitespace(expr);
    }


    public void checkValidCharsForOrExpression() {
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (!isValidOrExprChar(c)) {
                throw new IllegalArgumentException("OR expression [" + expr + "] contains an invalid character: [" + c + "]");
            }
        }
    }

    public boolean containsOrSymbol() {
        return this.contains(Op.OR);
    }

    public boolean containsAndSymbol() {
        return this.contains(Op.AND);
    }

    public boolean contains(Op op) {
        return expr.contains(op.getSymbol() + "");
    }

    public void checkValidCharsForAndExpression() {
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (!isValidAndExprChar(c)) {
                throw new IllegalArgumentException("AND expression [" + expr + "] contains an invalid character: [" + c + "]");
            }
        }
    }

//    public static boolean isValidRawExprChar(char c) {
//        return Character.isLetterOrDigit(c) || isValidOperator(c) || Character.isWhitespace(c);
//    }

    public static boolean isValidOrExprChar(char c) {
        return Character.isLetterOrDigit(c) || isValidOperator(c);
    }

    public static boolean isValidAndExprChar(char c) {
        return isValidOrExprChar(c) && c != Op.OR.symbol;
    }

    public static boolean isValidOperator(char c) {
        return c == Op.OR.getSymbol() || c == Op.AND.getSymbol() || c == Op.NOT.getSymbol();
    }

    public static class Literal {

        private final String varCode;
        private final boolean negated;

        public Literal(String varCode, boolean negated) {
            assert varCode != null;
            assert !Strings.containsNonWordChar(varCode);
            this.varCode = varCode;
            this.negated = negated;
        }

        public String getVarCode() {
            return varCode;
        }

        public boolean isNegated() {
            return negated;
        }
    }


    public Literal parseLiteral() {
        assert !isEmpty(expr);
        assert !Strings.containsWhitespace(expr);

        if (expr.startsWith("!")) {
            if (expr.length() < 2) throw new IllegalArgumentException();
            return new Literal(expr.substring(1), true);
        } else {
            return new Literal(expr, false);
        }

    }


    public String stringValue() {
        return expr;
    }

    @Override
    public String toString() {
        return stringValue();
    }


}
