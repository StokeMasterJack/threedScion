package c3i.imgGen.search;

public abstract class VarSet<VAR_TYPE> implements Iterable<VAR_TYPE> {

    public abstract boolean contains(VAR_TYPE var);

    public abstract boolean isEmpty(VAR_TYPE var);

    public abstract VarSet<VAR_TYPE> union(VarSet<VAR_TYPE> that);
}
