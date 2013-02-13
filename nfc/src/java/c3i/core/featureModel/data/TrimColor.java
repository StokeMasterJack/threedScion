package c3i.core.featureModel.data;

import c3i.core.featureModel.shared.Cardinality;
import c3i.core.featureModel.shared.boolExpr.Var;

public class TrimColor extends Trim {

    public Var color = addVar(Color);

    //exteriorColor
    public Var exteriorColor = color.addChild(ExteriorColor, "Exterior Color");

    public Var white = exteriorColor.addChild("040", "White");
    public Var silver = exteriorColor.addChild("1F7", "Silver");
    public Var gray = exteriorColor.addChild("1G3", "Gray");
    public Var black = exteriorColor.addChild("202", "Black");
    public Var red = exteriorColor.addChild("3R3", "Red");
    public Var beach = exteriorColor.addChild("4T8", "Beach");
    public Var spruce = exteriorColor.addChild("6V4", "Spruce");
    public Var green = exteriorColor.addChild("776", "Green");
    public Var blueRibbon = exteriorColor.addChild("8T5", "BlueRibbon");
    public Var blueWhisper = exteriorColor.addChild("8U8", "BlueWhisper");

    //interiorColor
    public Var interiorColor = color.addChild(InteriorColor, "Interior Color");
    public Var ash = interiorColor.addChild("Ash");
    public Var charcoal = interiorColor.addChild("Charcoal");
    public Var bisque = interiorColor.addChild("Bisque");

    //interiorColorCode
    public Var interiorColorCode = color.addChild(InteriorColorCode, "Interior Color Code");
    public Var ash13 = interiorColorCode.addChild("13");
    public Var ash14 = interiorColorCode.addChild("14");
    public Var charcoal15 = interiorColorCode.addChild("15");
    public Var bisque40 = interiorColorCode.addChild("40");

    //interiorMaterial
    public Var interiorMaterial = color.addChild(InteriorMaterial, "Interior Material");
    public Var fabric = interiorMaterial.addChild("Fabric");
    public Var leather = interiorMaterial.addChild("Leather");

    //fabricPlusColor
    public Var fabricPlusColor = color.addChild(FabricPlusColor);
    public Var fb13 = fabricPlusColor.addChild("FB13");
    public Var fb40 = fabricPlusColor.addChild("FB40");
    public Var fa13 = fabricPlusColor.addChild("FA13");
    public Var fa40 = fabricPlusColor.addChild("FA40");
    public Var fc14 = fabricPlusColor.addChild("FC14");
    public Var fc15 = fabricPlusColor.addChild("FC15");
    public Var la13 = fabricPlusColor.addChild("LA13");
    public Var la40 = fabricPlusColor.addChild("LA40");
    public Var lb14 = fabricPlusColor.addChild("LB14");
    public Var lb15 = fabricPlusColor.addChild("LB15");

    public TrimColor() {
        super();

        color.setMandatory(true);
        color.setCardinality(Cardinality.AllGroup);

        fabricPlusColor.setCardinality(Cardinality.PickOneGroup);
        exteriorColor.setCardinality(Cardinality.PickOneGroup);
        interiorColor.setCardinality(Cardinality.PickOneGroup);
        interiorColorCode.setCardinality(Cardinality.PickOneGroup);
        interiorMaterial.setCardinality(Cardinality.PickOneGroup);

        interiorFabricColorGrade();
        exteriorColor();
        interiorColor();

        fabricPlusColor.setDerived(true);
    }

    protected void interiorFabricColorGrade() {
        addConstraint(iff(fa13, and(ash, ash13, fabric, or(hybrid, xle))));
        addConstraint(iff(fb13, and(ash, ash13, fabric, or(base, le))));
        addConstraint(iff(fc14, and(ash, ash14, fabric, se)));
        addConstraint(iff(la13, and(ash, ash13, leather, or(hybrid, xle))));
        addConstraint(iff(lb14, and(ash, ash14, leather, se)));

        addConstraint(iff(fa40, and(bisque, bisque40, fabric, or(hybrid, xle))));
        addConstraint(iff(fb40, and(bisque, bisque40, fabric, or(base, le))));
        addConstraint(iff(la40, and(bisque, bisque40, leather, or(hybrid, xle))));

        addConstraint(iff(fc15, and(charcoal, charcoal15, fabric, se)));
        addConstraint(iff(lb15, and(charcoal, charcoal15, leather, se)));
    }

    protected void interiorColor() {
        addConstraint(conflict(ash, beach));
        addConstraint(conflict(ash, green));
        addConstraint(conflict(bisque, silver));

        addConstraint(imply(ash13, ash));
        addConstraint(imply(ash14, ash));
        addConstraint(imply(ash, or(ash13, ash14)));
        addConstraint(iff(charcoal15, charcoal));
        addConstraint(iff(bisque40, bisque));
    }

    protected void exteriorColor() {
        addConstraint(conflict(base, gray));
        addConstraint(conflict(base, red));
        addConstraint(conflict(base, spruce));
        addConstraint(conflict(base, green));
        addConstraint(conflict(base, blueWhisper));

        addConstraint(conflict(le, blueWhisper));
        addConstraint(conflict(se, or(beach, spruce, green, blueWhisper)));
        addConstraint(conflict(xle, blueWhisper));
    }


}
