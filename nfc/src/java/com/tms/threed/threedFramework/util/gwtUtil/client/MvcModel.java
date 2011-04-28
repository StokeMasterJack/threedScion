package com.tms.threed.threedFramework.util.gwtUtil.client;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedFramework.util.lang.shared.Objects;

public class MvcModel {

    private final HandlerManager bus = new HandlerManager(this);
    protected boolean eventsEnabled = true;

    public void fireEvent(GwtEvent<?> event) {
        if (isEventsEnabled()) {
            bus.fireEvent(event);
        }
    }

    public boolean isEventsEnabled() {
        return eventsEnabled;
    }

    public static boolean eq(Object o1, Object o2) {
        return Objects.eq(o1, o2);
    }

    public static boolean ne(Object o1, Object o2) {
        return !eq(o1, o2);
    }

    public void disableEvents() {
        this.eventsEnabled = false;
    }

    protected <H extends EventHandler> HandlerRegistration addHandler(GwtEvent.Type<H> type, final H handler) {
        return bus.addHandler(type, handler);
    }

}