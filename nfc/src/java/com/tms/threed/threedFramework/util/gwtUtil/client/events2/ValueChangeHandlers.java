package com.tms.threed.threedFramework.util.gwtUtil.client.events2;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class ValueChangeHandlers<I> extends HandlerManager implements HasValueChangeHandlers<I> {

    public ValueChangeHandlers(Object source) {
        super(source);
    }

    @Override public HandlerRegistration addValueChangeHandler(ValueChangeHandler<I> handler) {
        return addHandler(ValueChangeEvent.getType(), handler);
    }

    public void fire(I newValue) {
        ValueChangeEvent.fire(this, newValue);
    }


}