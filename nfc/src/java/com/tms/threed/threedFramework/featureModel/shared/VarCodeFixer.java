package com.tms.threed.threedFramework.featureModel.shared;

import java.util.HashSet;
import java.util.Set;

public class VarCodeFixer implements IVarGuesser {

    /**
     * TODO use reflection for this
     */
    public static Set<String> getCommonVarNames() {
        final HashSet<String> s = new HashSet<String>();

        s.add(ModelCode);
        s.add(Trim);
        s.add(Base);
        s.add(Color);
        s.add(ExteriorColor);
        s.add(ExteriorColor);
        s.add(InteriorColor);
        s.add(FabricPlusColor);
        s.add(InteriorMaterial);
        s.add(Grade);
        s.add(Engine);
        s.add(Transmission);

        s.add(Options);

        s.add(Accessories);
        s.add(InteriorAccessories);
        s.add(ExteriorAccessories);

        return s;

    }

    public static String fixupVarCode(String proposedVarName, String parent) {
        String varCode = fixupVarCodeCase(proposedVarName);
        varCode = fixupShortCodes(varCode, parent);
        return varCode;
    }

    private static String fixupShortCodes(String proposedVarName, String parent) {
        final String Interior = "Interior";
        final String Exterior = "Exterior";
        final String Color = "Color";
        final String Accessories = "Accessories";

        String retVal = null;
        if (proposedVarName.equalsIgnoreCase(Interior)) {
            if (parent.equalsIgnoreCase(Color)) retVal = InteriorColor;
            else if (parent.equalsIgnoreCase(Accessories)) retVal = InteriorAccessories;
        }
        if (proposedVarName.equalsIgnoreCase(Exterior)) {
            if (parent.equalsIgnoreCase(Color)) retVal = ExteriorColor;
            else if (parent.equalsIgnoreCase(Accessories)) retVal = ExteriorAccessories;
        }

        if (retVal == null) {
            retVal = proposedVarName;
        } else {
            //System.out.println("Fixing [" + proposedVarName + "] to [" + retVal + "]");
        }

        return retVal;
    }

    private static String fixupVarCodeCase(String proposedVarName) {
        final Set<String> commonVarNames = getCommonVarNames();
        for (String preferredVarName : commonVarNames) {
            String s1 = preferredVarName.toLowerCase();
            String s2 = proposedVarName.toLowerCase();
            if (s1.equals(s2)) {
                //System.out.println("Fixing [" + proposedVarName + "] to [" + preferredVarName + "]");
                return preferredVarName;
            }
        }
        return proposedVarName;
    }


}