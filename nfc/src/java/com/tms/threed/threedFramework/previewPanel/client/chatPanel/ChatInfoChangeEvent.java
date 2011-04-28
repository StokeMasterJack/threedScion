package com.tms.threed.threedFramework.previewPanel.client.chatPanel;

import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.GwtEvent;

public class ChatInfoChangeEvent extends GwtEvent<ChatInfoChangeHandler> {

    public static final Type<ChatInfoChangeHandler> TYPE = new Type<ChatInfoChangeHandler>();

    @Override public Type<ChatInfoChangeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override protected void dispatch(ChatInfoChangeHandler handler) {
        handler.onChange(this);
    }

    public void test() throws Exception {

        ValueChangeHandler e;

        HasValueChangeHandlers hasValueChangeHandlers;
    }

}