package c3i.featureModel.shared.dirtyQueue;

import c3i.featureModel.shared.boolExpr.Var;

/**
 * This a non-dup var assignment
 */
public class NewAssignment extends CspChangeEvent {

    private final Var var;
    private final boolean value;

    public NewAssignment(Var var, boolean value) {
        this.var = var;
        this.value = value;
    }

    public Var getVar() {
        return var;
    }

    public boolean isValue() {
        return value;
    }
}
