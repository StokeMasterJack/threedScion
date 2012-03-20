package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VarGuesser implements IVarGuesser {

    private static final Set<String> MandatoryVars = initMandatoryVars();

    public static Cardinality guessCardinality(Var var) {

        if (var.getCode().equalsIgnoreCase(Trim)) return Cardinality.AllGroup;
        if (var.getCode().equalsIgnoreCase(Color)) return Cardinality.AllGroup;

        if (isVarStrictlyForGroupingPurposes(var)) {
            return Cardinality.ZeroOrMoreGroup;
        }

        if (isStandardTrimVar(var)) {
            return Cardinality.PickOneGroup;
        }

        return null;
    }

    public static boolean isStandardTrimVar(Var var) {
        for (String code : StandardTrimChildVars) {
            if (var.getCode().equalsIgnoreCase(code)) return true;
        }

        return false;
    }

    public static Boolean guessMandatory(Var var) {
        if(var.isRoot()) return true;
        for (String code : MandatoryVars) {
            if (var.getCode().equalsIgnoreCase(code)) return true;
        }

        return null;
    }

    private static HashSet<String> initMandatoryVars() {
        HashSet<String> a = new HashSet<String>();

        a.add(Trim);
        a.addAll(Arrays.asList(StandardTrimChildVars));
        a.addAll(Arrays.asList(StrictlyGroupingVars));

        a.add(Color);
        a.add(ExteriorColor);
        a.add(InteriorColor);
        a.add(InteriorColorCode);
        a.add(FabricPlusColor);
        a.add(InteriorMaterial);
        a.add(InteriorMaterialCode);
        return a;
    }

    public static boolean isVarStrictlyForGroupingPurposes(Var var) {
        for (String code : StrictlyGroupingVars) {
            if (var.getCode().equals(code)) return true;
        }
        return false;
    }
}
