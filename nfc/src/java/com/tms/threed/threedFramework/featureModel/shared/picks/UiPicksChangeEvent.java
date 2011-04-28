package com.tms.threed.threedFramework.featureModel.shared.picks;

import com.google.gwt.event.shared.GwtEvent;

public class UiPicksChangeEvent extends GwtEvent<UiPicksChangeHandler> {

    public static final Type<UiPicksChangeHandler> TYPE = new Type<UiPicksChangeHandler>();

    @Override
    public Type<UiPicksChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(UiPicksChangeHandler handler) {
       handler.onPicksChange(this);
    }
}