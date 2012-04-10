package threed.core.featureModel.data;

import threed.core.featureModel.shared.Cardinality;
import threed.core.featureModel.shared.FeatureModel;
import threed.core.featureModel.shared.IVarGuesser;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.threedModel.shared.BrandKey;

public class Avalon2011 extends FeatureModel implements IVarGuesser {

    public Var trim = addVar(Trim);

    public Var grade = trim.addChild(Grade);

    public Var base = grade.addChild(Base);
    public Var ltd = grade.addChild("LTD");

    public Var engine = trim.addChild(Engine);
    public Var v6 = engine.addChild("V6");

    public Var transmission = trim.addChild(Transmission);
    public Var at6 = transmission.addChild("6AT");

    public Var modelCode = trim.addChild(ModelCode);


    public Var t3544 = modelCode.addChild("3544");
    public Var t3554 = modelCode.addChild("3554");

    //start color

    public Var color = addVar(Color);

    public Var fabricPlusColor = color.addChild(FabricPlusColor);

    //interior fabric+color
    public Var LH02 = fabricPlusColor.addChild("LH02");
    public Var LH17 = fabricPlusColor.addChild("LH17");
    public Var LH20 = fabricPlusColor.addChild("LH20");

    public Var FE02 = fabricPlusColor.addChild("FE02");
    public Var FE17 = fabricPlusColor.addChild("FE17");
    public Var LL02 = fabricPlusColor.addChild("LL02");

    public Var LL17 = fabricPlusColor.addChild("LL17");
    public Var LL20 = fabricPlusColor.addChild("LL20");
    public Var LG20 = fabricPlusColor.addChild("LG20");

    public Var LN02 = fabricPlusColor.addChild("LN02");
    public Var LN17 = fabricPlusColor.addChild("LN17");
    public Var LN20 = fabricPlusColor.addChild("LN20");


    public Var exteriorColor = color.addChild(ExteriorColor, "Exterior Color");


    //exterior color
    public Var blizzardPearl_070 = exteriorColor.addChild("070", "Blizzard Pearl");
    public Var silver = exteriorColor.addChild("1F7", "Silver");
    public Var gray1G3 = exteriorColor.addChild("1G3", "Gray");
    public Var black_202 = exteriorColor.addChild("202", "Black");
    public Var red = exteriorColor.addChild("3R0", "Sizzling Crimson Mica");
    public Var beach_4T8 = exteriorColor.addChild("4T8", "Beach");
    public Var bean_4U5 = exteriorColor.addChild("4U5", "Cocoa Bean Metallic");
    public Var cypressPearl = exteriorColor.addChild("6T7", "Cypress Pearl");
    public Var blue = exteriorColor.addChild("8N0", "Zephyr Blue Metallic");

    //interior color
    public Var interiorColor = color.addChild(InteriorColor, "Interior Color");
    public Var Ivory = interiorColor.addChild("Ivory");
    public Var Gray = interiorColor.addChild("Gray");
    public Var Black = interiorColor.addChild("Black");

    //interior color code
    public Var interiorColorCode = color.addChild(InteriorColorCode, "Interior Color Code");
    public Var Ivory02 = interiorColorCode.addChild("02");
    public Var Gray17 = interiorColorCode.addChild("17");
    public Var Black20 = interiorColorCode.addChild("20");

//    public Var ash13 = interiorColor.addVar("13");
//    public Var ash14 = interiorColor.addVar("14");
//    public Var charcoal15 = interiorColor.addVar("15");
//    public Var bisque40 = interiorColor.addVar("40");

    //interior material
    public Var interiorMaterial = color.addChild(InteriorMaterial, "Interior Material");
    public Var fabric = interiorMaterial.addChild("Fabric");
    public Var leather = interiorMaterial.addChild("Leather");

    //interior material code
//    public Var interiorMaterialCode = color.addVar(InteriorMaterialCode, "Interior Material Code");
//    public Var LH = interiorMaterialCode.addVar("LH");
//    public Var LL = interiorMaterialCode.addVar("LL");
//    public Var LG = interiorMaterialCode.addVar("LG");
//    public Var LN = interiorMaterialCode.addVar("LN");
//    public Var FE = interiorMaterialCode.addVar("FE");

    public Var options = addVar(Options);

    public Var HD = options.addChild("HD");
    public Var HM = options.addChild("HM");
    public Var EJ = options.addChild("EJ");
    public Var NV = options.addChild("NV");
    public Var AF = options.addChild("AF");
    public Var LM = options.addChild("LM");

    public Var acc = addVar(Accessories, "Accessories Group");

    public Var eAcc = acc.addChild(ExteriorAccessories, "Exterior Accessories Group");
    public Var iAcc = acc.addChild(InteriorAccessories, "Interior Accessories Group");

    public Var CF = iAcc.addChild("CF", "Carpet Floor Mats");

    public Var WL = eAcc.addChild("WL", "Wheel locks (alloy)");

    public Var i9G = iAcc.addChild("9G", "Cargo Tote");

    public Var EF = eAcc.addChild("EF", "Rear Bumper Applique");


    /*
The current picks are not valid: [1F7, LH17, 3544]
     */
    public Avalon2011() {
        //trim

        super(BrandKey.TOYOTA, 2011,"avalon","Avalon");
        trim.setMandatory(true);
        trim.setCardinality(Cardinality.AllGroup);

        grade.setCardinality(Cardinality.PickOneGroup);
        engine.setCardinality(Cardinality.PickOneGroup);
        transmission.setCardinality(Cardinality.PickOneGroup);
        modelCode.setCardinality(Cardinality.PickOneGroup);


        //colors
        color.setMandatory(true);
        color.setCardinality(Cardinality.AllGroup);

        fabricPlusColor.setCardinality(Cardinality.PickOneGroup);
        exteriorColor.setCardinality(Cardinality.PickOneGroup);
        interiorColor.setCardinality(Cardinality.PickOneGroup);
        interiorColorCode.setCardinality(Cardinality.PickOneGroup);
        interiorMaterial.setCardinality(Cardinality.PickOneGroup);


        

        addConstraint(v6);
        addConstraint(at6);

        addConstraint(iff(Ivory02, Ivory));
        addConstraint(iff(Gray17, Gray));
        addConstraint(iff(Black20, Black));

        addConstraint(iff(t3544, and(base, v6, at6)));
        addConstraint(iff(t3554, and(ltd, v6, at6)));

        addConstraint(imply(ltd, and(HM, HD)));


        addConstraint(imply(LH02, and(leather, Ivory, base)));
        addConstraint(conflict(LH02, or(LM, AF, HM, silver, blue)));

        addConstraint(imply(LH17, and(leather, Gray, base)));
        addConstraint(conflict(LH17, or(LM, AF, HM, beach_4T8, bean_4U5)));

        addConstraint(imply(LH20, and(leather, Black, base)));
        addConstraint(conflict(LH20, or(LM, AF, HM, beach_4T8, cypressPearl)));

        addConstraint(iff(FE02, and(fabric, Ivory, base, AF)));
        addConstraint(conflict(FE02, or(blizzardPearl_070, silver, blue)));

        addConstraint(iff(FE17, and(fabric, Gray, base, AF)));
        addConstraint(conflict(FE17, or(blizzardPearl_070, this.beach_4T8, bean_4U5)));

        addConstraint(iff(LL02, and(leather, Ivory, base, HM)));
        addConstraint(conflict(LL02, or(silver, blue)));

        addConstraint(iff(LL17, and(leather, Gray, base, HM)));
        addConstraint(conflict(LL17, or(beach_4T8, bean_4U5)));

        addConstraint(iff(LL20, and(leather, Black, base, HM)));
        addConstraint(conflict(LL20, or(cypressPearl, beach_4T8)));

        addConstraint(iff(LG20, and(leather, Black, base, LM)));

        addConstraint(iff(LN02, and(leather, Ivory, ltd)));
        addConstraint(conflict(LN02, or(silver, blue)));

        addConstraint(iff(LN17, and(leather, Gray, ltd)));
        addConstraint(conflict(LN17, or(beach_4T8, bean_4U5)));

        addConstraint(iff(LN20, and(leather, Black, ltd)));
        addConstraint(conflict(LN20, or(beach_4T8, cypressPearl, blue)));

        addConstraint(conflict(fabric, Black));

        addConstraint(imply(HM, HD));

        addConstraint(conflict(NV, or(AF, LM, EJ)));

        addConstraint(iff(AF, fabric));
        addConstraint(conflict(AF, or(HD, HM, LM, blizzardPearl_070, Black, ltd)));

        addConstraint(imply(LM, and(black_202, Black, leather, base)));
        addConstraint(conflict(LM, or(HD, HM, Ivory, Gray, ltd)));

    }


    public void printTree() {
        getRootVar().print();
    }
}