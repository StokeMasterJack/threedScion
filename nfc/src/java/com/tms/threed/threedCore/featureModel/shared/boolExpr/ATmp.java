package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public class ATmp {


    private final int a;
    private final int b;

    public ATmp(int a, int b) {
        this.a = a;
        this.b = b;
    }


    public int getA() {
        return a;
    }

    public int getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ATmp aTmp = (ATmp) o;

        if (a != aTmp.a) return false;
        if (b != aTmp.b) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return 31 * a + b;
    }
}
