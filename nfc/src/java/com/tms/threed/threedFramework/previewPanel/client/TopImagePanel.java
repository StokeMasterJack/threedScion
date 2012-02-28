package com.tms.threed.threedFramework.previewPanel.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.ClearGif;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.DragToSpin;
import com.tms.threed.threedFramework.previewPanel.client.headerPanel.HeaderPanel;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.getSimpleName;

public class TopImagePanel extends AbsolutePanel {

    public ImageSize mainImageSize;

    private final HeaderPanel headerPanel;
    private final ThreedImagePanel mainImagePanel;
    private final FooterPanel footerPanel;

    private final BlinkOverlay blinkOverlay;
    private final DragToSpin<ClearGif> dragToSpin;

    public TopImagePanel(ImageSize mainImageSize, ThreedImagePanel mainImagePanel, BlinkOverlay blinkOverlay, DragToSpin<ClearGif> dragToSpin, HeaderPanel headerPanel, FooterPanel footerPanel) {
        this.mainImageSize = mainImageSize;

        this.headerPanel = headerPanel;
        this.mainImagePanel = mainImagePanel;
        this.footerPanel = footerPanel;
        this.blinkOverlay = blinkOverlay;
        this.dragToSpin = dragToSpin;

        setPixelSize(mainImageSize.getWidth(), mainImageSize.getHeight());

        ClearGif dragDiv = new ClearGif(mainImageSize);

        footerPanel.setPixelSize(mainImageSize.getWidth(), footerPanel.getPreferredHeightPx());

        add(this.mainImagePanel, 0, 0);
        add(this.blinkOverlay, 0, 0);
        add(this.headerPanel, 0, 0);
        add(dragDiv, 0, 0);


        add(this.footerPanel, 0, getFooterPanelTop());

//        image.addLoadHandler(new LoadHandler() {
//            @Override public void onLoad(LoadEvent event) {
//                refreshButtonPanels();
//            }
//        });

        PreviewPanelStyles.set(this);

        dragToSpin.attachToTarget(dragDiv);

    }

    public int getPanelIndex() {
        return 0;
    }

    public void setMainImageSize(ImageSize mainImageSize) {
        this.mainImageSize = mainImageSize;
        setPixelSize(mainImageSize.getWidth(), mainImageSize.getHeight());



        mainImagePanel.setImageSize(mainImageSize);

        footerPanel.setPixelSize(mainImageSize.getWidth(), footerPanel.getPreferredHeightPx());
        this.setWidgetPosition(footerPanel, 0, getFooterPanelTop());

    }

    private int getFooterPanelTop() {
        return mainImageSize.getHeight() - footerPanel.getPreferredHeightPx();
    }


}

