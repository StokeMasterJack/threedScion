package com.tms.threed.threedCore.featureModel.shared.boolExpr;

import com.tms.threed.threedCore.featureModel.shared.AutoAssignContext;

/**
 * Attempted to assign the value [value] to expr
 */
public class AssignmentException extends RuntimeException {

    private final BoolExpr expr;
    private final boolean value;

    /**
     * @param expr  the expression on which autoAssign was called
     * @param value the autoAssign value.
     *              If true, then autoAssignTrue was called
     *              If false, then autoAssignFalse was called
     */
    public AssignmentException(BoolExpr expr, boolean value) {
        this.expr = expr;
        this.value = value;
    }

    public BoolExpr getExpr() {
        return expr;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String getMessage() {
        return "Expression " + expr + " could not be auto-assigned " + value;
    }


    public String getMessage(AutoAssignContext ctx) {
        return "Expression " + expr + " [which evaluates to " + expr.simplify(ctx) + "] could not be auto-assigned " + value;
    }
}
