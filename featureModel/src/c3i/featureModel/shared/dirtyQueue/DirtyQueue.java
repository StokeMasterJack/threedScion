package c3i.featureModel.shared.dirtyQueue;

import c3i.featureModel.shared.boolExpr.Var;

import java.util.PriorityQueue;

public class DirtyQueue {

    private final PriorityQueue<CspChangeEvent> pq = new PriorityQueue<CspChangeEvent>();

    public boolean isEmpty() {
        return pq.isEmpty();
    }

    public boolean afterNewAssignment(Var var, boolean value) {
        return pq.add(new NewAssignment(var, value));
    }

    public boolean isDirty() {
        return pq.isEmpty();
    }

    public void test1() throws Exception{

    }
}
