package threed.core.featureModel.shared.search.decision;

import threed.core.featureModel.shared.PeekingIterator;
import threed.core.featureModel.shared.boolExpr.Var;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class XorDecisions implements Decisions {

    private final Var parent;
    private final List<Decision> decisions;

//    public static Decisions create(List<Var> openVars) {
//
//        if(!openVars.isEmpty()) return null;
//
//
//        Var peek = openVars.get(0);
//        if(!peek.isXorChild()) return null;
//
//        Var parent = peek.getParent();
//        List<Decision> decisions = new ArrayList<Decision>();
//
//        while (openVars.hasNext() && openVars.peek().isXorChild() && openVars.peek().childOf(parent)) {
//
//            Var nextVar = openVars.next();
//
//
//            assert parent != null;
//            assert nextVar.isXorChild();
//            assert nextVar.childOf(parent);
//
//            decisions.add(new XorDecision(nextVar));
//        }
//
//        if (parent == null) return null;
//        else return new XorDecisions(parent, decisions);
//
//    }

    public static Decisions create(PeekingIterator<Var> openVars) {

        if (!openVars.hasNext()) return null;


        Var peek = openVars.peek();
        if (!peek.isXorChild()) return null;

        Var parent = peek.getParent();
        List<Decision> decisions = new ArrayList<Decision>();

        List<Var> xorChildVars = parent.getChildVars();
        for (Var xorChildVar : xorChildVars) {
            decisions.add(new XorDecision(xorChildVar));
        }

//        while (openVars.hasNext() && openVars.peek().isXorChild() && openVars.peek().childOf(parent)) {
//
//            Var nextVar = openVars.next();
//
//
//            assert parent != null;
//            assert nextVar.isXorChild();
//            assert nextVar.childOf(parent);
//
//
//        }

        assert parent != null;
        return new XorDecisions(parent, decisions);

    }


    public XorDecisions(Var parent, List<Decision> decisions) {
        this.parent = parent;
        this.decisions = decisions;
    }

    @Override
    public Iterator<Decision> iterator() {
        return decisions.iterator();
    }

    @Override
    public String toString() {
        return "XorDecisions[" + parent + "] decisions.size:[" + decisions.size() + "]";
    }


}
