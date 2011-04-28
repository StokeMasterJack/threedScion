package com.tms.threed.threedFramework.previewPanel.client.buttonBars.interior;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

    Resources INSTANCE = GWT.create(Resources.class);

    ImageResource sideViewButtonUp();

    ImageResource sideViewButtonDown();

    ImageResource dashViewButtonUp();

    ImageResource dashViewButtonDown();

    ImageResource topViewButtonUp();

    ImageResource topViewButtonDown();
}