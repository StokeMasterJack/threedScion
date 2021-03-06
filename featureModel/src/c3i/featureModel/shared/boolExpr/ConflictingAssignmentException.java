package c3i.featureModel.shared.boolExpr;

import c3i.featureModel.shared.AutoAssignContext;

public class ConflictingAssignmentException extends AssignmentException {

    /**
     * @param expr  the expression on which autoAssign was called
     * @param value the autoAssign value.
     *              If true, then autoAssignTrue was called
     *              If false, then autoAssignFalse was called
     */
    public ConflictingAssignmentException(BoolExpr expr, boolean value, AutoAssignContext context) {
        super(expr, value, context);
        System.out.println("ReassignmentException.ReassignmentException");
    }

    public void test1() throws Exception {
        //get the failed path (i.e. the failed assignment)
    }

}
