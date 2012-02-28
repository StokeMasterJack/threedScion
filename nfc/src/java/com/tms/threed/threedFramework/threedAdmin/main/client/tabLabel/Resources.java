package com.tms.threed.threedFramework.threedAdmin.main.client.tabLabel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface Resources extends ClientBundle {

    Resources INSTANCE = GWT.create(Resources.class);

    ImageResource closeButton();

    ImageResource closeButtonMouseOver();


}