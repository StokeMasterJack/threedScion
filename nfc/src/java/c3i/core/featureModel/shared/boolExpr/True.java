package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;
import c3i.core.featureModel.shared.Bit;
import c3i.core.featureModel.shared.EvalContext;
import c3i.core.featureModel.shared.Tri;

public class True extends Constant {

    private static final Type TYPE = Type.True;

    private static final int HASH = 31 * True.class.hashCode();

    private static True instance = new True();

    private True() {
    }

    public Type getType() {
        return TYPE;
    }

    public static True getInstance() {
        return instance;
    }

    @Override
    public final String toString() {
        return "TRUE";
    }


    @Override
    public void autoAssignFalse(AutoAssignContext ctx, int depth) throws AssignmentException {
        throw new AssignmentException(this, false, ctx);
    }

    @Override
    public void autoAssignTrue(AutoAssignContext ctx, int depth) throws AssignmentException {
    }

    @Override
    public Tri eval(EvalContext ctx) {
        return Bit.TRUE;
    }

    @Override
    public boolean equals(Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return HASH;
    }
}
