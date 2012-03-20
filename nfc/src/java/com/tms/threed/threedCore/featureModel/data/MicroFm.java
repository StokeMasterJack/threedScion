package com.tms.threed.threedCore.featureModel.data;

import com.tms.threed.threedCore.featureModel.shared.Cardinality;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.featureModel.shared.IVarGuesser;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

public class MicroFm extends FeatureModel implements IVarGuesser {


    public Var grade = addVar(Grade);
    public Var le = grade.addChild("LE");
    public Var se = grade.addChild("SE");

    public Var engine = addVar(Engine);
    public Var l4 = engine.addChild("L4");
    public Var v6 = engine.addChild("V6");


    public MicroFm() {

        super(2011,"MicroFm", "MicroFm");

        grade.setCardinality(Cardinality.PickOneGroup);
        engine.setCardinality(Cardinality.PickOneGroup);

        grade.setMandatory(true);
        engine.setMandatory(true);
    }


    public void printTree() {
        getRootVar().print();
    }


}