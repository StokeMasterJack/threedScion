package com.tms.threed.threedFramework.util.gwtUtil.client.events2;

import com.google.gwt.event.shared.GwtEvent;

import java.util.HashMap;
import java.util.Map;

abstract public class SimpleEvent extends GwtEvent<SimpleHandler> {

    private final static Map<Class, Type<SimpleHandler>> TYPES = new HashMap<Class, Type<SimpleHandler>>();

    @Override protected void dispatch(SimpleHandler handler) {
        handler.onEvent(this);
    }

    @Override public Type<SimpleHandler> getAssociatedType() {
        Class<? extends SimpleEvent> cls = getClass();
        
        Type<SimpleHandler> t = TYPES.get(cls);
        if (t == null) {
            t = new Type<SimpleHandler>();
            TYPES.put(cls, t);
        }
        return t;
    }

}