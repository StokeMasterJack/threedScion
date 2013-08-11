package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.AutoAssignContext;
import c3i.core.featureModel.shared.Bit;
import c3i.core.featureModel.shared.EvalContext;
import c3i.core.featureModel.shared.Tri;

public class False extends Constant {

    private static final Type TYPE = Type.False;
    private static final int HASH = 31 * TYPE.id;
    private static False instance = new False();


    private False() {
    }

    public Type getType() {
        return TYPE;
    }

    public static False getInstance() {
        return instance;
    }

    @Override
    public String toString() {
        return "FALSE";
    }

    @Override
    public BoolExpr simplify(AutoAssignContext ctx) {
        return this;
    }

    @Override
    public void autoAssignFalse(AutoAssignContext ctx,int depth) throws AssignmentException {
        //ignore
    }

    @Override
    public void autoAssignTrue(AutoAssignContext ctx,int depth) throws AssignmentException {
        throw new ReassignmentException(this,true,ctx);
    }

    @Override
    public Tri eval(EvalContext ctx) {
        return Bit.FALSE;
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