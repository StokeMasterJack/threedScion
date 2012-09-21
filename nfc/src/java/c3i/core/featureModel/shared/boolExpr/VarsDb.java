package c3i.core.featureModel.shared.boolExpr;

import c3i.core.featureModel.shared.UnknownVarCodeException;
import c3i.core.featureModel.shared.UnknownVarIndexException;
import c3i.core.featureModel.shared.Vars;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class VarsDb implements Vars, Iterable<Var> {

    private final ArrayList<Var> list;
    private final HashMap<String, Var> map;

    public VarsDb() {
        this.list = new ArrayList<Var>();
        this.map = new HashMap<String, Var>();

        RootVar rootVar = new RootVar();
        list.add(rootVar);
        map.put(rootVar.getCode(), rootVar);
    }

    @Override
    public Iterator<Var> iterator() {
        return list.iterator();
    }

    @Override
    public int size() {
        return list.size();
    }

    public RootVar getRootVar() {
        return (RootVar) list.get(0);
    }


    @Override
    public Var get(int varIndex) throws UnknownVarIndexException {
        try {
            return list.get(varIndex);
        } catch (Exception e) {
            throw new UnknownVarIndexException(varIndex);
        }
    }

    @Override
    public Var get(String varCode) throws UnknownVarCodeException {
        Var var = map.get(varCode);
        if (var == null) throw new UnknownVarCodeException(varCode);
        return var;
    }

    @Override
    public boolean containsCode(String varCode) {
        return map.containsKey(varCode);
    }

    @Override
    public boolean containsIndex(int varIndex) {
        return varIndex < list.size();
    }


    public boolean isEmpty() {
        return list.isEmpty();
    }


    Var newVar(Var parent, String code, String name) {
        assert parent != null;
        assert !containsCode(code);

        assert !list.isEmpty();
        assert !map.isEmpty();

        int nextIndex;
        synchronized (this) {
            nextIndex = size();
            Var v = new Var(parent, nextIndex, code, name);

            boolean a1 = list.add(v);
            Var a2 = map.put(v.getCode(), v);

            assert a1 && a2 == null;

            return v;
        }
    }


    public void addLeanVar(Var var) {
        boolean a1 = list.add(var);
        Var a2 = map.put(var.getCode(), var);
        assert a1 && (a2 == null);
        assert var.index == list.size() - 1;
    }


    public class RootVar extends Var {

        public RootVar() {
            super(null, 0, "Root", "Root");
        }

        Var newVar(Var parent, String code, String name) {
            return VarsDb.this.newVar(parent, code, name);
        }

        boolean containsCode(String varCode) {
            return VarsDb.this.containsCode(varCode);
        }

        boolean containsIndex(int varIndex) {
            return VarsDb.this.containsIndex(varIndex);
        }

        public Var getVar(int varIndex) throws UnknownVarIndexException {
            return VarsDb.this.get(varIndex);
        }

        public Var getVar(String varCode) throws UnknownVarIndexException {
            return VarsDb.this.get(varCode);
        }


    }

    @Override public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj.getClass() != VarsDb.class) return false;

        VarsDb that = (VarsDb) obj;

        return this.list.equals(that.list);
    }
}
