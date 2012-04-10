package threed.core.featureModel.data;

import threed.core.featureModel.shared.Cardinality;
import threed.core.featureModel.shared.FeatureModel;
import threed.core.featureModel.shared.IVarGuesser;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.threedModel.shared.BrandKey;

public class MicroFm extends FeatureModel implements IVarGuesser {


    public Var grade = addVar(Grade);
    public Var le = grade.addChild("LE");
    public Var se = grade.addChild("SE");

    public Var engine = addVar(Engine);
    public Var l4 = engine.addChild("L4");
    public Var v6 = engine.addChild("V6");


    public MicroFm() {

        super(BrandKey.TOYOTA, 2011,"MicroFm", "MicroFm");

        grade.setCardinality(Cardinality.PickOneGroup);
        engine.setCardinality(Cardinality.PickOneGroup);

        grade.setMandatory(true);
        engine.setMandatory(true);
    }


    public void printTree() {
        getRootVar().print();
    }


}