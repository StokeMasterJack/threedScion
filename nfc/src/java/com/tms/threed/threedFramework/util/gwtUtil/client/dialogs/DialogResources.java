package com.tms.threed.threedFramework.util.gwtUtil.client.dialogs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface DialogResources extends ClientBundle {

    DialogResources INSTANCE = GWT.create(DialogResources.class);

    ImageResource closeButton();
}
