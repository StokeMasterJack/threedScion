package com.tms.threed.threedFramework.previewPanel.client;

import com.google.gwt.user.client.ui.Composite;

public abstract class AbstractPreviewPanel extends Composite {

    public abstract int getPreferredWidthPx();

    public abstract int getPreferredHeightPx();

//    abstract public void setMainImageSize(ImageSize jpgSize);
}
