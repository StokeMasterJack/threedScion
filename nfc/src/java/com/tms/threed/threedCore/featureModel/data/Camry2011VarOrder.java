package com.tms.threed.threedCore.featureModel.data;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

import java.util.Set;

public class Camry2011VarOrder extends TrimColorOption {

    public Var acc = addVar(Accessories, "Accessories Group");

    public Var eAcc = acc.addChild(ExteriorAccessories, "Exterior Accessories Group");
    public Var iAcc = acc.addChild(InteriorAccessories, "Interior Accessories Group");

    public Var a28 = iAcc.addChild("28", "Shift Knob");
    public Var cf = iAcc.addChild("CF", "Floormats");
    public Var a2q = iAcc.addChild("2Q", "All Weather Mats");

    public Var e5 = eAcc.addChild("E5", "Exhaust Tip");
    public Var wb = eAcc.addChild("WB", "Alloy Wheel");
    public Var bm = eAcc.addChild("BM", "Body Side Molding");
    public Var dio3 = eAcc.addChild("DIO3", "Mudguards");
    public Var r7 = eAcc.addChild("R7");

    public Camry2011VarOrder() {

        addConstraint(imply(up, wb));
        addConstraint(imply(ut, wb));
        addConstraint(imply(t2540, e5));
        addConstraint(conflict(a28, mt6));
        addConstraint(xor(a2q, cf));

        addConstraint(imply(qd, and(r7, sr, se)));
        addConstraint(imply(r7, se));

//        addPickOneGroupXorsIfNeeded();

    }

    public Set<Var> getSamplePicks() {
        SampleFeatureSet picks = new SampleFeatureSet();
        return getVars(picks);
    }


}