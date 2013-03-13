package c3i.featureModel.data;

import c3i.core.common.shared.BrandKey;
import c3i.featureModel.shared.Cardinality;
import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.IVarGuesser;
import c3i.featureModel.shared.boolExpr.Var;

public class TinyFm extends FeatureModel implements IVarGuesser {


    public Var grade = addVar(Grade);
    public Var le = grade.addChild("LE");
    public Var se = grade.addChild("SE");

    public Var engine = addVar(Engine);
    public Var l4 = engine.addChild("L4");
    public Var v6 = engine.addChild("V6");

    public Var modelCode = addVar(ModelCode);

    public Var t2513 = modelCode.addChild("2513");
    public Var t2514 = modelCode.addChild("2514");

    public Var t2531 = modelCode.addChild("2531");


    public TinyFm() {

        super(BrandKey.TOYOTA, 2011, "tiny", "Tiny");

        grade.setCardinality(Cardinality.PickOneGroup);
        engine.setCardinality(Cardinality.PickOneGroup);
        modelCode.setCardinality(Cardinality.PickOneGroup);


        grade.setMandatory(true);
        engine.setMandatory(true);
        modelCode.setMandatory(true);

        addConstraint(iff(t2513, and(le, l4)));
        addConstraint(iff(t2514, and(se, l4)));

        addConstraint(iff(t2531, and(le, v6)));


    }


    public void printTree() {
        getRootVar().print();
    }


}