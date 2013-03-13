package c3i.featureModel.data;

import c3i.core.common.shared.BrandKey;
import c3i.featureModel.shared.Cardinality;
import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.IVarGuesser;
import c3i.featureModel.shared.boolExpr.Var;

public class Trim extends FeatureModel implements IVarGuesser {

    public Var trim = addVar(Trim);

    public Var grade = trim.addChild(Grade);

    public Var base = grade.addChild(Base);
    public Var le = grade.addChild("LE");
    public Var se = grade.addChild("SE");
    public Var xle = grade.addChild("XLE");
    public Var hybrid = grade.addChild("Hyb");

    public Var engine = trim.addChild(Engine);
    public Var l4 = engine.addChild("L4");
    public Var v6 = engine.addChild("V6");
    public Var hybridEngine = engine.addChild("Hybrid");

    public Var transmission = trim.addChild(Transmission);
    public Var mt6 = transmission.addChild("6MT");
    public Var at6 = transmission.addChild("6AT");
    public Var ecvt = transmission.addChild("ECVT");

    public Var modelCode = trim.addChild(ModelCode);

    public Var t2513 = modelCode.addChild("2513");
    public Var t2514 = modelCode.addChild("2514");

    public Var t2531 = modelCode.addChild("2531");
    public Var t2532 = modelCode.addChild("2532");
    public Var t2552 = modelCode.addChild("2552");

    public Var t2540 = modelCode.addChild("2540");
    public Var t2554 = modelCode.addChild("2554");

    public Var t2545 = modelCode.addChild("2545");
    public Var t2546 = modelCode.addChild("2546");
    public Var t2550 = modelCode.addChild("2550");

    public Var t2560 = modelCode.addChild("2560");


    public Trim() {

        super(BrandKey.TOYOTA, 2011, "camry", "Camry");

        grade.setCardinality(Cardinality.PickOneGroup);
        engine.setCardinality(Cardinality.PickOneGroup);
        transmission.setCardinality(Cardinality.PickOneGroup);
        modelCode.setCardinality(Cardinality.PickOneGroup);

        trim.setCardinality(Cardinality.AllGroup);
        trim.setMandatory(true);

        grade.setMandatory(true);
        engine.setMandatory(true);
        transmission.setMandatory(true);
        modelCode.setMandatory(true);

        addConstraint(iff(t2513, and(base, l4, mt6)));
        addConstraint(iff(t2514, and(base, l4, at6)));

        addConstraint(iff(t2531, and(le, l4, mt6)));
        addConstraint(iff(t2532, and(le, l4, at6)));
        addConstraint(iff(t2552, and(le, v6, at6)));

        addConstraint(iff(t2540, and(xle, l4, at6)));
        addConstraint(iff(t2554, and(xle, v6, at6)));

        addConstraint(iff(t2545, and(se, l4, mt6)));
        addConstraint(iff(t2546, and(se, l4, at6)));
        addConstraint(iff(t2550, and(se, v6, at6)));

        addConstraint(iff(t2560, and(hybrid, hybridEngine, ecvt)));

//

        modelCode.setDerived(true);
    }


    public void printTree() {
        getRootVar().print();
    }


}