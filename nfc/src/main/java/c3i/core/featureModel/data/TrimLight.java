package c3i.core.featureModel.data;

import c3i.core.featureModel.shared.Cardinality;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.IVarGuesser;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.common.shared.BrandKey;

public class TrimLight extends FeatureModel implements IVarGuesser {

    public Var camry = addVar("camry");

    public Var trim = camry.addChild(Trim);

    public Var grade = trim.addChild(Grade);
    public Var base = grade.addChild(Base);     //done
    public Var le = grade.addChild("LE");         //done
//    public Var se = grade.addVar("SE");
//    public Var xle = grade.addVar("XLE");
//    public Var hybrid = grade.addVar("Hyb");

    public Var engine = trim.addChild(Engine);
    public Var l4 = engine.addChild("L4");
    public Var v6 = engine.addChild("V6");
//    public Var hybridEngine = engine.addVar("Hybrid");

//    public Var transmission = trim.addVar(Transmission);
//    public Var mt6 = transmission.addVar("6MT");
    //    public Var at6 = transmission.addVar("6AT");
//    public Var ecvt = transmission.addVar("ECVT");
//
    public Var modelCode = trim.addChild(ModelCode);
    //
    public Var t2513 = modelCode.addChild("2513");
    public Var t2514 = modelCode.addChild("2514");
    //
    public Var t2531 = modelCode.addChild("2531");
    public Var t2532 = modelCode.addChild("2532");
//    public Var t2552 = modelCode.addVar("2552");
//
//    public Var t2540 = modelCode.addVar("2540");
//    public Var t2554 = modelCode.addVar("2554");
//
//    public Var t2545 = modelCode.addVar("2545");
//    public Var t2546 = modelCode.addVar("2546");
//    public Var t2550 = modelCode.addVar("2550");
//
//    public Var t2560 = modelCode.addVar("2560");

    public TrimLight() {
        super(BrandKey.TOYOTA, 2011,"camry","Camry");

        grade.setCardinality(Cardinality.PickOneGroup);
        engine.setCardinality(Cardinality.PickOneGroup);
//        transmission.setCardinality(Cardinality.PickOneGroup);
        modelCode.setCardinality(Cardinality.PickOneGroup);

        trim.setCardinality(Cardinality.AllGroup);
        trim.setMandatory(true);

//        addExtraConstraint(iff(t2513, and(base, l4, mt6)));
//        addExtraConstraint(iff(t2513, and(base, l4, mt6)));
        addConstraint(iff(t2513, and(base, l4)));
        addConstraint(iff(t2514, and(base, v6)));
//
        addConstraint(iff(t2531, and(le, l4)));
        addConstraint(iff(t2532, and(le, v6)));
//        addExtraConstraint(iff(t2552, and(le, v6, at6)));
//
//        addExtraConstraint(iff(t2540, and(xle, l4, at6)));
//        addExtraConstraint(iff(t2554, and(xle, v6, at6)));
//
//        addExtraConstraint(iff(t2545, and(se, l4, mt6)));
//        addExtraConstraint(iff(t2546, and(se, l4, at6)));
//        addExtraConstraint(iff(t2550, and(se, v6, at6)));
//
//        addExtraConstraint(iff(t2560, and(hybrid, hybridEngine, ecvt)));


    }


    public void printTree() {
        getRootVar().print();
    }
}