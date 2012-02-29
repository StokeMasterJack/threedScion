package com.tms.threed.threedFramework.previewPanel.client.main;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tms.threed.threedFramework.previewPanel.client.PreviewPanel;
import com.tms.threed.threedFramework.previewPanel.client.PreviewPanelStyles;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.ClearGif;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;

public class TopImagePanel extends AbsolutePanel {

    public ImageSize mainImageSize;

    private final HeaderPanel headerPanel;
    private final PreviewPanel previewPanel;
    private final FooterPanel footerPanel;

    public TopImagePanel(ImageSize mainImageSize, PreviewPanel previewPanel, HeaderPanel headerPanel, FooterPanel footerPanel) {
        this.mainImageSize = mainImageSize;

        this.headerPanel = headerPanel;
        this.previewPanel = previewPanel;
        this.footerPanel = footerPanel;

        setPixelSize(mainImageSize.getWidth(), mainImageSize.getHeight());

        footerPanel.setPixelSize(mainImageSize.getWidth(), footerPanel.getPreferredHeightPx());

        add(this.previewPanel, 0, 0);
        add(this.headerPanel, 0, 0);
        add(this.footerPanel, 0, getFooterPanelTop());


        PreviewPanelStyles.set(this);


    }

    public int getPanelIndex() {
        int retVal = previewPanel.getPanelIndex();
        assert retVal == 0;
        return retVal;
    }

    public void setMainImageSize(ImageSize mainImageSize) {
        this.mainImageSize = mainImageSize;
        setPixelSize(mainImageSize.getWidth(), mainImageSize.getHeight());


        previewPanel.setImageSize(mainImageSize);

        footerPanel.setPixelSize(mainImageSize.getWidth(), footerPanel.getPreferredHeightPx());
        this.setWidgetPosition(footerPanel, 0, getFooterPanelTop());

    }

    private int getFooterPanelTop() {
        return mainImageSize.getHeight() - footerPanel.getPreferredHeightPx();
    }


}

