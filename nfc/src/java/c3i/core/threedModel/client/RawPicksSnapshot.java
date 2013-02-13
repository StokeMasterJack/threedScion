package c3i.core.threedModel.client;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static smartsoft.util.shared.Strings.isEmpty;

public class RawPicksSnapshot {

    @Nonnull
    public final String modelCode;
    @Nonnull
    public final String exteriorColor;
    @Nonnull
    public final String exteriorColorFixed; //todo, handle this more elegently
    @Nonnull
    public final String interiorColor;

    @Nullable
    public final String packageCodes;
    @Nullable
    public final String accessoryCodes;

    public RawPicksSnapshot(@Nonnull String modelCode, @Nonnull String exteriorColor, @Nonnull String interiorColor, @Nullable String packageCodes, @Nullable String accessoryCodes) {

        if (isEmpty(modelCode)) throw new IllegalArgumentException("modelCode cannot be empty");
        if (isEmpty(exteriorColor)) throw new IllegalArgumentException("exteriorColorCode cannot be empty");
        if (isEmpty(interiorColor)) throw new IllegalArgumentException("interiorColor cannot be empty");

        this.modelCode = modelCode.trim();
        this.exteriorColor = exteriorColor.trim();
        this.exteriorColorFixed = convertFourDigitColorCodeToThreeDigit(exteriorColor);
        this.interiorColor = interiorColor.trim();

        this.packageCodes = isEmpty(packageCodes) ? null : packageCodes;
        this.accessoryCodes = isEmpty(accessoryCodes) ? null : accessoryCodes;

    }

    public static String convertFourDigitColorCodeToThreeDigit(String exteriorColor) {
        if (exteriorColor.length() == 4 && exteriorColor.startsWith("0")) {
            return exteriorColor.substring(1);
        } else {
            return exteriorColor;
        }
    }


    @Override
    public String toString() {
        return "modelCode[" + modelCode + "]  exteriorColor[" + exteriorColor + "]  interiorColor[" + interiorColor + "]  packageCodes[" + packageCodes + "]  accessoryCodes[" + accessoryCodes + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RawPicksSnapshot that = (RawPicksSnapshot) o;

        if (accessoryCodes != null ? !accessoryCodes.equals(that.accessoryCodes) : that.accessoryCodes != null)
            return false;
        if (packageCodes != null ? !packageCodes.equals(that.packageCodes) : that.packageCodes != null) return false;

        if (!exteriorColor.equals(that.exteriorColor)) return false;
        if (!interiorColor.equals(that.interiorColor)) return false;
        if (!modelCode.equals(that.modelCode)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = modelCode.hashCode();
        result = 31 * result + exteriorColor.hashCode();
        result = 31 * result + interiorColor.hashCode();
        result = 31 * result + (packageCodes != null ? packageCodes.hashCode() : 0);
        result = 31 * result + (accessoryCodes != null ? accessoryCodes.hashCode() : 0);
        return result;
    }

}