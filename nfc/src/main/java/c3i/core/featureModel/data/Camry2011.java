package c3i.core.featureModel.data;

import c3i.core.featureModel.shared.Vars;
import c3i.core.featureModel.shared.boolExpr.Var;

import java.util.HashSet;
import java.util.Set;

public class Camry2011 extends TrimColorOption {

    public Var acc = addVar(Accessories, "Accessories");

    public Var iAcc = acc.addChild(InteriorAccessories, "Interior Accessories");
    public Var a28 = iAcc.addChild("28", "Shift Knob");
    public Var cf = iAcc.addChild("CF", "Floormats");
    public Var a2q = iAcc.addChild("2Q", "All Weather Mats");

    public Var eAcc = acc.addChild(ExteriorAccessories, "Exterior Accessories");
    public Var e5 = eAcc.addChild("E5", "Exhaust Tip");
    public Var wb = eAcc.addChild("WB", "Alloy Wheel");
    public Var r7 = eAcc.addChild("R7");
    public Var bm = eAcc.addChild("BM", "Body Side Molding");
    public Var dio3 = eAcc.addChild("DIO3", "Mudguards");

    public Camry2011() {
        addConstraint(imply(up, wb));
        addConstraint(imply(ut, wb));
        addConstraint(imply(t2540, e5));
        addConstraint(conflict(a28, mt6));
        addConstraint(xor(a2q, cf));
        a2q.setDefaultValue(true);
        addConstraint(imply(qd, and(r7, sr, se)));
        addConstraint(imply(r7, se));


    }

    public Set<Var> getSamplePicks() {
        SampleFeatureSet picks = new SampleFeatureSet();
        return getVars(picks);
    }

    public Set<Var> getOutputVars() {
        HashSet<Var> a = new HashSet<Var>();

        Vars vars = this;

        for (int i = 0; i < vars.size(); i++) {
            Var var = vars.get(i);

            Var p = var.getParent();
            boolean even = (i % 2) == 0;

            boolean intAcc = p != null && p.getCode().equals(InteriorAccessories);
            if (intAcc) continue;

            boolean evenOption = p != null && p.getCode().equals(Options) && even;
            if (evenOption) continue;

            if (var.isZeroOrMoreGroup()) continue;
//            if (var.isZeroOrMoreGroup()) continue;

            a.add(var);
        }
        return a;

    }


}
