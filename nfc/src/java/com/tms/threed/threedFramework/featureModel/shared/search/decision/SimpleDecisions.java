package com.tms.threed.threedFramework.featureModel.shared.search.decision;

import com.tms.threed.threedFramework.featureModel.shared.PeekingIterator;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;

import java.util.Iterator;

public class SimpleDecisions implements Decisions {

    private Var var;

    private boolean f;
    private boolean t;

    public static Decisions create(PeekingIterator<Var> openVars) {
        if (openVars.hasNext()) {
            return new SimpleDecisions(openVars.next());
        } else {
            return null;
        }
    }

    public SimpleDecisions(Var var) {
        this.var = var;
    }

    @Override
    public Iterator<Decision> iterator() {

        return new Iterator<Decision>() {

            @Override
            public boolean hasNext() {
                if (var == null) return false;
                return !f;
            }

            @Override
            public Decision next() {
                Decision ret;
                if (!t) {
                    ret = new TrueDecision(var);
                    t = true;
                } else if (!f) {
                    ret = new FalseDecision(var);
                    f = true;
                } else {
                    throw new IllegalStateException();
                }
                return ret;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };

    }

    @Override
    public String toString() {
        return  "SimpleDecisions[" + var + "]";
    }


}
