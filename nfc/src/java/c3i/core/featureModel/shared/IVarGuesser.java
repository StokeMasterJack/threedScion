package c3i.core.featureModel.shared;

import java.io.Serializable;

public interface IVarGuesser extends Serializable {


    String Trim = "Trim";
    public final static String ModelCode = "ModelCode";
    String Grade = "Grade";
    String Cab = "Cab";
    String Bed = "Bed";
    String Drive = "Drive";
    String Engine = "Engine";
    String Transmission = "Transmission";

    String Base = "Base";

    String Color = "Color";
    String ExteriorColor = "ExteriorColor";
    String InteriorColor = "InteriorColor";
    String InteriorColorCode = "InteriorColorCode";
    String FabricPlusColor = "FabricPlusColor";
    String InteriorMaterial = "InteriorMaterial";
    String InteriorMaterialCode = "InteriorMaterialCode";


    String Options = "Options";

    String Accessories = "Accessories";
    String InteriorAccessories = "InteriorAccessories";
    String ExteriorAccessories = "ExteriorAccessories";
    String PerformanceAccessories = "PerformanceAccessories";



    String[] StandardTrimChildVars = {
            ModelCode,
            Grade,
            Transmission,
            Engine,
            Drive,
            Cab,
            Bed};

    public static final String[] StrictlyGroupingVars = {Options, Accessories, InteriorAccessories, ExteriorAccessories, PerformanceAccessories};


}
