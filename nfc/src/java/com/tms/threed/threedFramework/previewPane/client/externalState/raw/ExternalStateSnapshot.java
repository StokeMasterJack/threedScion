package com.tms.threed.threedFramework.previewPane.client.externalState.raw;

import com.tms.threed.threedFramework.previewPanel.client.ChatInfo;
import com.tms.threed.threedFramework.threedModel.client.RawPicksSnapshot;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.lang.shared.Objects;
import com.tms.threed.threedFramework.util.lang.shared.Path;
import com.tms.threed.threedFramework.util.lang.shared.Strings;

import javax.annotation.Nonnull;

//import static com.tms.threed.util.lang.shared.Objects.ne;
//import static com.tms.threed.util.lang.shared.Strings.isEmpty;

/**
 * This represents a state-snapshot, essentially notification from the left half of the
 * app that something changed that the right half of the app (us) might care about. 
 */
public final class ExternalStateSnapshot {

//    private final String seriesName;
//    private final String modelYear;

    private final String modelCode; //4 digit
    private final String exteriorColor;
    private final String interiorColor;
    private final String optionCodes;
    private final String accessoryCodes;

    private final String msrp;

    private final String chatVehicleIconMediaId;
    private final String chatActionUrl;

    private final String flashKey;  //??
    private final String flashDescription; //??

    public ExternalStateSnapshot(String modelCode, String exteriorColor, String interiorColor, String optionCodes, String accessoryCodes, String msrp, String chatVehicleIconMediaId, String chatActionUrl, String flashKey, String flashDescription) {
//        this.seriesName = nullZeroNormalize(seriesName);
//        this.modelYear = modelYear;
        this.modelCode = modelCode;
        this.exteriorColor = exteriorColor;
        this.interiorColor = interiorColor;
        this.optionCodes = optionCodes;
        this.accessoryCodes = accessoryCodes;
        this.msrp = msrp;
        this.chatVehicleIconMediaId = nullZeroNormalize(chatVehicleIconMediaId);
        this.chatActionUrl = nullZeroNormalize(chatActionUrl);
        this.flashKey = flashKey;
        this.flashDescription = flashDescription;

        if (Strings.isEmpty(modelCode)) throw new IllegalArgumentException("\"modelCode\" is required");
    }

    public String nullZeroNormalize(String s) {
        if (s == null) return null;
        s = " " + s + " ";
        s = s.trim();
        if (s.length() == 0) return null;
        if (s.equals("0")) return null;
        return s;
    }

//    public ExternalStateSnapshot() {
//        this.seriesName = null;
//        this.modelYear = null;
//        this.modelCode = null;
//        this.exteriorColor = null;
//        this.interiorColor = null;
//        this.optionCodes = null;
//        this.accessoryCodes = null;
//        this.msrp = null;
//        this.chatVehicleIconMediaId = null;
//        this.chatActionUrl = null;
//        this.flashKey = null;
//        this.flashDescription = null;
//    }

    @Override
    public boolean equals(Object that) {
        if (this == that) return true;
        if (that == null) return false;
        if (this.getClass() != that.getClass()) return false;
        ExternalStateSnapshot thatExternalStateSnapshot = (ExternalStateSnapshot) that;
        return this.same(thatExternalStateSnapshot);
    }

    public boolean same(@Nonnull ExternalStateSnapshot that) {
        assert that != null;
        return modelCodeChanged(that) &&
                exteriorColorChanged(that) &&
                interiorColorChanged(that) &&
                optionCodesChanged(that) &&
                accessoryCodesChanged(that) &&
                msrpChanged(that) &&
                chatVehicleIconUrlChanged(that) &&
                chatActionUrlChanged(that) &&
                flashKeyChanged(that) &&
                flashDescriptionChanged(that);
    }

//    @Override
//    public int hashCode() {
//        throw new UnsupportedOperationException("ExternalStateSnapshot does not support hashCode method");
//    }


    @Override
    public int hashCode() {
        Console.log("ExternalStateSnapshot.hashCode(..)");
        int result =  (modelCode != null ? modelCode.hashCode() : 0);
        result = 31 * result + (exteriorColor != null ? exteriorColor.hashCode() : 0);
        result = 31 * result + (interiorColor != null ? interiorColor.hashCode() : 0);
        result = 31 * result + (optionCodes != null ? optionCodes.hashCode() : 0);
        result = 31 * result + (accessoryCodes != null ? accessoryCodes.hashCode() : 0);
        result = 31 * result + (msrp != null ? msrp.hashCode() : 0);
        result = 31 * result + (chatVehicleIconMediaId != null ? chatVehicleIconMediaId.hashCode() : 0);
        result = 31 * result + (chatActionUrl != null ? chatActionUrl.hashCode() : 0);
        result = 31 * result + (flashKey != null ? flashKey.hashCode() : 0);
        result = 31 * result + (flashDescription != null ? flashDescription.hashCode() : 0);
        result = 31 * result + (mediaIdUrl != null ? mediaIdUrl.hashCode() : 0);
        return result;
    }

//    public boolean seriesNameChanged(ExternalStateSnapshot that) {
//        return Objects.ne(seriesName, that.seriesName);
//    }
//
//    public boolean modelYearChanged(ExternalStateSnapshot that) {
//        return Objects.ne(modelYear, that.modelYear);
//    }


    public boolean modelCodeChanged(ExternalStateSnapshot that) {
        return Objects.ne(modelCode, that.modelCode);
    }

    public boolean exteriorColorChanged(ExternalStateSnapshot that) {
        return Objects.ne(exteriorColor, that.exteriorColor);
    }

    public boolean interiorColorChanged(ExternalStateSnapshot that) {
        return Objects.ne(interiorColor, that.interiorColor);
    }

    public boolean optionCodesChanged(ExternalStateSnapshot that) {
        return Objects.ne(optionCodes, that.optionCodes);
    }

    public boolean accessoryCodesChanged(ExternalStateSnapshot that) {
        return Objects.ne(accessoryCodes, that.accessoryCodes);
    }

    public boolean msrpChanged(ExternalStateSnapshot that) {
        return Objects.ne(msrp, that.msrp);
    }

    public boolean chatVehicleIconUrlChanged(ExternalStateSnapshot that) {
        return Objects.ne(chatVehicleIconMediaId, that.chatVehicleIconMediaId);
    }

    public boolean chatActionUrlChanged(ExternalStateSnapshot that) {
        return Objects.ne(chatActionUrl, that.chatActionUrl);
    }


    public boolean flashKeyChanged(ExternalStateSnapshot that) {
        return Objects.ne(flashKey, that.flashKey);
    }

    public boolean flashDescriptionChanged(ExternalStateSnapshot that) {
        return Objects.ne(flashDescription, that.flashDescription);
    }

//    public String getSeriesName() {
//        return seriesName;
//    }
//
//    public String getModelYear() {
//        return modelYear;
//    }

    public String getModelCode() {
        return modelCode;
    }

    public String getExteriorColor() {
        return exteriorColor;
    }

    public String getInteriorColor() {
        return interiorColor;
    }

    public String getOptionCodes() {
        return optionCodes;
    }

    public String getAccessoryCodes() {
        return accessoryCodes;
    }

    public String getMsrp() {
        return msrp;
    }

    public String getChatVehicleIconMediaId() {
        return chatVehicleIconMediaId;
    }

    public String getChatActionUrl() {
        return chatActionUrl;
    }

    public String getFlashKey() {
        return flashKey;
    }

    public String getFlashDescription() {
        return flashDescription;
    }

    String mediaIdUrl = "/byt/pub/media?id=";

    public ChatInfo getChatInfo() {
        if (Strings.isEmpty(chatActionUrl) || Strings.isEmpty(chatVehicleIconMediaId)) return null;
        else return new ChatInfo(new Path(mediaIdUrl + chatVehicleIconMediaId), new Path(chatActionUrl));
    }

//    public SeriesInfo getSeriesInfo() {
//        if (Strings.isEmpty(seriesName) || Strings.isEmpty(modelYear)) return null;
//        SeriesKey seriesKey = new SeriesKey(modelYear, seriesName);
//        return SeriesInfoBuilder.createSeriesInfo(seriesKey);
//    }

    public RawPicksSnapshot getPicksRaw() {
        if (Strings.isEmpty(modelCode)) throw new IllegalStateException("modelCode should be non null");
        return new RawPicksSnapshot(modelCode, exteriorColor, interiorColor, optionCodes, accessoryCodes);
    }

    public void logToConsole(String prefix) {
//        Console.log(prefix + "SeriesInfo[" + this.getSeriesInfo() + "]");
        Console.log(prefix + "PicksRaw[" + this.getPicksRaw() + "]");
        Console.log(prefix + "Msrp[" + this.getMsrp() + "]");
        Console.log(prefix + "ChatInfo[" + this.getChatInfo() + "]");
    }

    @Override public String toString() {
        return "PicksRaw[" + this.getPicksRaw() + "] \t  Msrp[" + this.getMsrp() + "] \t  ChatInfo[" + this.getChatInfo() + "]";
    }
}
