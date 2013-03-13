package c3i.featureModel.shared.boolExpr;

public final class VarKey {

    public final Class type = Var.class;
    public final String varCode;

    public VarKey(String varCode) {
        assert varCode != null;
        assert varCode.length() != 0;
        this.varCode = varCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (o.getClass() != VarKey.class) return false;

        VarKey key = (VarKey) o;

        if (!type.equals(key.type)) return false;
        if (!varCode.equals(key.varCode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + varCode.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Var[" + varCode + "]";
    }
}
