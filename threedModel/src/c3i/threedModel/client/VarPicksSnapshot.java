package c3i.threedModel.client;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.UnknownVarCodeException;
import c3i.featureModel.shared.boolExpr.Var;
import com.google.common.collect.ImmutableSet;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import static smartsoft.util.shared.Strings.isEmpty;

public class VarPicksSnapshot {

    @Nonnull
    public final Var modelCode;
    @Nonnull
    public final Var exteriorColor;
    @Nonnull
    public final Var interiorColor;

    @Nonnull
    public final Set<Var> packageVars;
    @Nonnull
    public final Set<Var> accessoryVars;

    public final int packageCount;
    public final int accessoryCount;

    public static VarPicksSnapshot createVarPicksSnapshot(@Nonnull RawPicksSnapshot picksRaw, FeatureModel fm) throws UnknownVarCodeFromLeftSideException {
        Var modelCode = parseVar(fm, picksRaw.modelCode, VarCodeType.MODEL_CODE);
        Var exteriorColor = parseVar(fm, picksRaw.exteriorColorFixed, VarCodeType.EXTERIOR_COLOR);
        Var interiorColor = parseVar(fm, picksRaw.interiorColor, VarCodeType.INTERIOR_COLOR);
        Set<Var> packageVars = Collections.unmodifiableSet(parseVarCodes(fm, picksRaw.packageCodes, VarCodeType.OPTION));
        Set<Var> accessoryVars = Collections.unmodifiableSet(parseVarCodes(fm, picksRaw.accessoryCodes, VarCodeType.ACCESSORY));
        return new VarPicksSnapshot(modelCode, exteriorColor, interiorColor, packageVars, accessoryVars);
    }

    private static Var parseVar(FeatureModel fm, String varCode, VarCodeType varCodeType) throws UnknownVarCodeFromLeftSideException {
        try {
            return fm.getVar(varCode);
        } catch (UnknownVarCodeException e) {
            throw new UnknownVarCodeFromLeftSideException(e.getBadVarCode(), varCodeType);
        }
    }

    public VarPicksSnapshot(@Nonnull Var modelCode, @Nonnull Var exteriorColor, @Nonnull Var interiorColor, Set<Var> packageVars, Set<Var> accessoryVars) {
        this.modelCode = modelCode;
        this.exteriorColor = exteriorColor;
        this.interiorColor = interiorColor;

        if (packageVars == null) this.packageVars = new HashSet<Var>();
        else this.packageVars = packageVars;

        if (accessoryVars == null) this.accessoryVars = new HashSet<Var>();
        else this.accessoryVars = accessoryVars;

        this.packageCount = packageVars.size();
        this.accessoryCount = accessoryVars.size();
    }


    public ImmutableSet<Var> toVarSet() {
        ImmutableSet.Builder<Var> vars = ImmutableSet.builder();
        vars.add(modelCode);
        vars.add(exteriorColor);
        vars.add(interiorColor);
        vars.addAll(packageVars);
        vars.addAll(accessoryVars);
        return vars.build();
    }


//    public LinkedHashSet<Var> getFeatureSet() {
//        LinkedHashSet<Var> s = new LinkedHashSet<String>();
//        s.add(getModelCode());
//        s.add(preProcessExteriorColorCode());
//        s.add(getInteriorColor());
//        s.addAll(getPackageCodes());
//        s.addAll(getAccessoryCodes());
//        return s;
//    }


    @Override
    public String toString() {
        return "modelCode[" + modelCode + "]  exteriorColor[" + exteriorColor + "]  interiorColor[" + interiorColor + "]  packageCodes[" + packageVars + "]  accessoryCodes[" + accessoryVars + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VarPicksSnapshot that = (VarPicksSnapshot) o;

        if (!accessoryVars.equals(that.accessoryVars)) return false;
        if (!exteriorColor.equals(that.exteriorColor)) return false;
        if (!interiorColor.equals(that.interiorColor)) return false;
        if (!modelCode.equals(that.modelCode)) return false;
        if (!packageVars.equals(that.packageVars)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = modelCode.hashCode();
        result = 31 * result + exteriorColor.hashCode();
        result = 31 * result + interiorColor.hashCode();
        result = 31 * result + packageVars.hashCode();
        result = 31 * result + accessoryVars.hashCode();
        return result;
    }

    /**
     * Unknown var codes are ignored
     *
     * @param commaDelimitedList
     */
    private static Set<Var> parseVarCodes(FeatureModel fm, String commaDelimitedList, VarCodeType varCodeType) {

        final HashSet<Var> set = new HashSet<Var>();
        if (isEmpty(commaDelimitedList)) return set;
        commaDelimitedList = commaDelimitedList.trim();

        final String[] varCodes = commaDelimitedList.split(",");
        if (varCodes == null || varCodes.length == 0) return set;

        for (String varCode : varCodes) {
            if (isEmpty(varCode)) continue;
            Var var = fm.getVar(varCode.trim());
            if (var != null) {
                set.add(var);
            } else {
                log.log(Level.INFO, "Ignoring unknown " + varCodeType + " varCode[" + varCode + "]");
            }
        }
        return set;
    }


    enum VarCodeType {
        MODEL_CODE, EXTERIOR_COLOR, INTERIOR_COLOR, OPTION, ACCESSORY
    }

    public static class UnknownVarCodeFromLeftSideException extends UnknownVarCodeException {

        private final VarCodeType varCodeType;

        public UnknownVarCodeFromLeftSideException(String badVarCode, VarCodeType varCodeType) {
            super(badVarCode);
            this.varCodeType = varCodeType;
        }

        public VarCodeType getVarCodeType() {
            return varCodeType;
        }

        @Override
        public String getMessage() {
            return varCodeType + " " + super.getMessage();
        }
    }

    private static Logger log = Logger.getLogger(VarPicksSnapshot.class.getName());

}
