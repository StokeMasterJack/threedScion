package com.tms.threed.previewPanel.client.main.chatPanel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

    Resources INSTANCE = GWT.create(Resources.class);
    ImageResource chatIconOut();
    ImageResource chatIconOver();
}