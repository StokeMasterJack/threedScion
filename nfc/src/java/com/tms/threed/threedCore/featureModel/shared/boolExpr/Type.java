package com.tms.threed.threedCore.featureModel.shared.boolExpr;

public enum Type {


    AllFalse(0), And(1), Or(2), Xor(3), Imp(4), Conflict(5), Iff(6), Not(7), Var(8), True(9), False(10);


    public final int id;

    Type(int id) {
        this.id = id;
    }

    public static Type getType(int id) {
        for (Type b : values()) {
            if (b.id == id) return b;
        }
        throw new IllegalArgumentException("Bad typeId: [" + id + "]");
    }


    public String getSimpleName() {
        return this.toString();
    }

    public boolean isConstant() {
        return isTrue() || isFalse();
    }

    public boolean isTrue() {
        return id == True.id;
    }

    private boolean isFalse() {
        return id == False.id;
    }

    public boolean isVar() {
        return id == Var.id;
    }

    public boolean isNot() {
        return id == Not.id;
    }

    public boolean isPair() {
        return isImp() || isConflict() || isIff();
    }

    public boolean isImp() {
        return id == Imp.id;
    }

    public boolean isConflict() {
        return id == Conflict.id;
    }

    public boolean isIff() {
        return id == Iff.id;
    }


    public boolean isJunction() {
        return isAnd() || isOr() || isXor() || isAllFalse();
    }

    public boolean isAllFalse() {
        return id == AllFalse.id;
    }

    public boolean isAnd() {
        return id == And.id;
    }

    public boolean isOr() {
        return id == Or.id;
    }

    public boolean isXor() {
        return id == Xor.id;
    }
}
