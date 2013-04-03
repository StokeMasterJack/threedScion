package c3i.featureModel.shared.node;

import c3i.featureModel.shared.boolExpr.Var;

public class Simple {

    Var var;
    boolean sign;

    public Simple(Var var, boolean sign) {
        this.var = var;
        this.sign = sign;
    }

    public Var getVar() {
        return var;
    }

    public boolean isSign() {
        return sign;
    }

    @Override
    public String toString() {
        return (sign ? "" : "!") + var.toString();
    }
}
