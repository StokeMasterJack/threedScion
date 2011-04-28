package com.tms.threed.threedFramework.previewPanel.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.tms.threed.threedFramework.previewPanel.client.thumbsPanel.ThumbsPanel;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;

public class PreviewPanel extends AbstractPreviewPanel {

    public int preferredWidthPx;
    public int preferredHeightPx;

    private TopImagePanel topImagePanel;
    private BottomPanel bottomPanel;

    private ImageSize mainImageSize;

    public PreviewPanel(ImageSize mainImageSize, TopImagePanel topImagePanel, BottomPanel bottomPanel) {
//        assert previewPanelModel != null;

        this.mainImageSize = mainImageSize;


        DockLayoutPanel dock = new DockLayoutPanel(Style.Unit.PX);
        dock.setPixelSize(preferredWidthPx, preferredHeightPx);

        bottomPanel.setSize("100%", "100%");
        topImagePanel.setSize("100%", "100%");

        this.bottomPanel = bottomPanel;
        dock.addSouth(this.bottomPanel, ThumbsPanel.PREFERRED_HEIGHT_PX);
        this.topImagePanel = topImagePanel;
        dock.add(this.topImagePanel);

        initWidget(dock);

        PreviewPanelStyles.set(this);

        setPixelSize(getPreferredWidthPx(), getPreferredHeightPx());

//        getElement().getStyle().setProperty("border","solid thick blue");

    }

    @Override public int getPreferredWidthPx() {
        return mainImageSize.getWidth();
    }

    @Override public int getPreferredHeightPx() {
        return mainImageSize.getHeight() + ThumbsPanel.PREFERRED_HEIGHT_PX;
    }

//    @Override public void setMainImageSize(ImageSize mainImageSize) {
//        this.mainImageSize = mainImageSize;
//        topImagePanel.setMainImageSize(mainImageSize);
//
//    }


    public void setMainImageSize(ImageSize mainImageSize) {
        this.mainImageSize = mainImageSize;
        setPixelSize(getPreferredWidthPx(), getPreferredHeightPx());
        topImagePanel.setMainImageSize(mainImageSize);
    }
}
