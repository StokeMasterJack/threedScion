package com.tms.threed.threedFramework.featureModel.shared.boolExpr;

import com.tms.threed.threedFramework.featureModel.shared.AutoAssignContext;
import com.tms.threed.threedFramework.featureModel.shared.Bit;
import com.tms.threed.threedFramework.featureModel.shared.EvalContext;
import com.tms.threed.threedFramework.featureModel.shared.Tri;

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
        throw new ReassignmentException(this,true);
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