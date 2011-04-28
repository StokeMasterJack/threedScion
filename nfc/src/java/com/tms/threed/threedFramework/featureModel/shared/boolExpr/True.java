package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

import com.tms.threed.threedFramework.featureModel.shared.AutoAssignContext;
import com.tms.threed.threedFramework.featureModel.shared.Bit;
import com.tms.threed.threedFramework.featureModel.shared.EvalContext;
import com.tms.threed.threedFramework.featureModel.shared.Tri;

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
    public void autoAssignFalse(AutoAssignContext ctx,int depth) throws AssignmentException {
        throw new AssignmentException(this,false);
    }

    @Override
    public void autoAssignTrue(AutoAssignContext ctx,int depth) throws AssignmentException {
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
