package com.tms.threed.threedFramework.util.gwtUtil.client.events2;

import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

public class SelectionHandlers<I> extends HandlerManager implements HasSelectionHandlers<I> {

    public SelectionHandlers(Object source) {
        super(source);
    }

    @Override public HandlerRegistration addSelectionHandler(SelectionHandler<I> handler) {
        return addHandler(SelectionEvent.getType(), handler);
    }

    public void fire(I selectedItem) {
        SelectionEvent.fire(this, selectedItem);
    }


}
