package com.tms.threed.threedFramework.previewPanel.client.dragToSpin;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.Image;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;

public class ClearGif extends Image {

    public ClearGif(ImageSize imageSize) {
        super(GWT.getModuleBaseURL() + "clear.cache.gif");
        setPixelSize(imageSize.getWidth(), imageSize.getHeight());
    }

    public void setImageSize(ImageSize imageSize) {
        setPixelSize(imageSize.getWidth(), imageSize.getHeight());
    }


}
