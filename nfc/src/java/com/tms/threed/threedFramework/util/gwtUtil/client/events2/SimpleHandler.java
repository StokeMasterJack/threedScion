package com.tms.threed.threedFramework.util.gwtUtil.client.events2;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;

public interface SimpleHandler<ET extends GwtEvent<SimpleHandler>> extends EventHandler {

    void onEvent(ET event);

}