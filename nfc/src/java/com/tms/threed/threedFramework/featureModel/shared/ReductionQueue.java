package com.tms.threed.threedFramework.featureModel.shared;

import com.tms.threed.threedFramework.featureModel.shared.boolExpr.BoolExpr;

import java.util.LinkedList;
import java.util.Queue;

public class ReductionQueue {

    public static Queue<Reduction> queue = new LinkedList<Reduction>();

    public static void add(Reduction r) {
        queue.add(r);
    }

    public static void add(BoolExpr expr,boolean value) {
        add(new Reduction(expr,value));
    }

}
