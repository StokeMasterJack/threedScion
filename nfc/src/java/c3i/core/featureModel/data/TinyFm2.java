package c3i.core.featureModel.data;

import c3i.core.featureModel.shared.Cardinality;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.IVarGuesser;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.common.shared.BrandKey;

public class TinyFm2 extends FeatureModel implements IVarGuesser {

    public Var trim = addVar(Trim);

    public Var grade = trim.addChild(Grade);
    public Var le = grade.addChild("LE");
    public Var se = grade.addChild("SE");
    public Var xe = grade.addChild("XE");

    public Var engine = trim.addChild(Engine);
    public Var l4 = engine.addChild("L4");
    public Var v6 = engine.addChild("V6");
    public Var v8 = engine.addChild("V8");

    public Var modelCode = trim.addChild(ModelCode);

    public Var t2513 = modelCode.addChild("2513");
    public Var t2514 = modelCode.addChild("2514");
    public Var t2515 = modelCode.addChild("2515");

    public Var t2531 = modelCode.addChild("2531");
    public Var t2532 = modelCode.addChild("2532");
    public Var t2533 = modelCode.addChild("2533");

    public Var t2631 = modelCode.addChild("2631");
    public Var t2632 = modelCode.addChild("2632");
    public Var t2633 = modelCode.addChild("2633");


    public TinyFm2() {

        super(BrandKey.TOYOTA, 2011, "tiny","Tiny");

        grade.setCardinality(Cardinality.PickOneGroup);
        engine.setCardinality(Cardinality.PickOneGroup);
        modelCode.setCardinality(Cardinality.PickOneGroup);

        trim.setCardinality(Cardinality.AllGroup);
        trim.setMandatory(true);

        grade.setMandatory(true);
        engine.setMandatory(true);
        modelCode.setMandatory(true);

        addConstraint(iff(t2513, and(le, l4)));
        addConstraint(iff(t2514, and(se, l4)));
        addConstraint(iff(t2515, and(xe, l4)));

        addConstraint(iff(t2531, and(le, v6)));
        addConstraint(iff(t2532, and(se, v6)));
        addConstraint(iff(t2533, and(xe, v6)));

        addConstraint(iff(t2631, and(le, v8)));
        addConstraint(iff(t2632, and(se, v8)));
        addConstraint(iff(t2633, and(xe, v8)));

        //addConstraint(imply(l4, tempVar));

    }


    public void printTree() {
        getRootVar().print();
    }


}