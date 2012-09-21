package c3i.core.featureModel.shared.boolExpr;

import com.google.common.base.Preconditions;
import c3i.core.featureModel.shared.AutoAssignContext;

/**
 * Attempted to assign the value [value] to expr
 */
public class AssignmentException extends RuntimeException {

    private final BoolExpr expr;
    private final boolean value;
    private final AutoAssignContext context;

    /**
     * @param expr  the expression on which autoAssign was called
     * @param value the autoAssign value.
     *              If true, then autoAssignTrue was called
     *              If false, then autoAssignFalse was called
     */
    public AssignmentException(BoolExpr expr, boolean value, AutoAssignContext context) {
        Preconditions.checkNotNull(expr);
        Preconditions.checkNotNull(context);
        this.expr = expr;
        this.value = value;
        this.context = context.copy();
    }

//    public AssignmentException(BoolExpr expr, boolean value) {
//        this(expr, value, null);
//    }

    public BoolExpr getExpr() {
        return expr;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public String getMessage() {
        if (context == null) {
            return "Expression " + expr + " could not be auto-assigned " + value;
        } else {
            return getMessage(context);
        }
    }


    public String getMessage(AutoAssignContext ctx) {
        return "Expression " + expr + " [which evaluates to " + expr.simplify(ctx) + "] could not be auto-assigned " + value;
    }

}
