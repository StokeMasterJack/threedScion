package com.tms.threedToyota.byt.client.externalState.raw;

import com.tms.threed.util.lang.shared.Objects;

import static com.tms.threed.util.lang.shared.Objects.ne;

public class ExternalStateChangeEvent {

    private final ExternalStateSnapshot oldState;
    private final ExternalStateSnapshot newState;

    public ExternalStateChangeEvent(ExternalStateSnapshot oldState, ExternalStateSnapshot newState) {
        this.oldState = oldState;
        this.newState = newState;
    }

    public boolean isFirstUpdate() {
        return oldState == null;
    }

    public ExternalStateSnapshot getOldState() {
        return oldState;
    }

    public ExternalStateSnapshot getNewState() {
        return newState;
    }

    public boolean hasChanged() {
        return !Objects.eq(oldState, newState);
    }

//    public boolean seriesNameChanged() {
//        if (oldState == null) return true;
//        return ne(oldState.getSeriesName(), newState.getSeriesName());
//    }
//
//    public boolean modelYearChanged() {
//        if (oldState == null) return true;
//        return ne(oldState.getModelYear(), newState.getModelYear());
//    }

    boolean modelCodeChanged() {
        if (oldState == null) return true;
        return ne(oldState.getModelCode(), newState.getModelCode());
    }

    boolean exteriorColorChanged() {
        if (oldState == null) return true;
        return ne(oldState.getExteriorColor(), newState.getExteriorColor());
    }

    boolean interiorColorChanged() {
        if (oldState == null) return true;
        return ne(oldState.getInteriorColor(), newState.getInteriorColor());
    }

    boolean optionCodesChanged() {
        if (oldState == null) return true;
        return ne(oldState.getOptionCodes(), newState.getOptionCodes());
    }

    boolean accessoryCodesChanged() {
        if (oldState == null) return true;
        return ne(oldState.getAccessoryCodes(), newState.getAccessoryCodes());
    }


    boolean chatVehicleIconUrlChanged() {
        if (oldState == null) return true;
        return ne(oldState.getChatVehicleIconMediaId(), newState.getChatVehicleIconMediaId());
    }

    boolean chatActionUrlChanged() {
        if (oldState == null) return true;
        return ne(oldState.getChatActionUrl(), newState.getChatActionUrl());
    }


    boolean flashKeyChanged() {
        if (oldState == null) return true;
        return ne(oldState.getFlashKey(), newState.getFlashKey());
    }

    boolean flashDescriptionChanged() {
        if (oldState == null) return true;
        return ne(oldState.getFlashDescription(), newState.getFlashDescription());
    }


    public boolean msrpChanged() {
        if (oldState == null) return true;
        return ne(oldState.getMsrp(), newState.getMsrp());
    }

    public boolean chatInfoChanged() {
        if (oldState == null) return true;
        return chatActionUrlChanged() || chatVehicleIconUrlChanged();
    }

//    public boolean seriesInfoChanged() {
//        if (oldState == null) return true;
//        return seriesNameChanged() || modelYearChanged();
//    }

    public boolean rawPicksChanged() {
        if (oldState == null) return true;
        return modelCodeChanged() || exteriorColorChanged() || interiorColorChanged() || optionCodesChanged() || accessoryCodesChanged();
    }

    @Override public boolean equals(Object obj) {
        throw new UnsupportedOperationException("ExternalStateChangeEvent does not support equals");
    }

    @Override public int hashCode() {
        throw new UnsupportedOperationException("ExternalStateChangeEvent does not support hashCode");
    }
}
