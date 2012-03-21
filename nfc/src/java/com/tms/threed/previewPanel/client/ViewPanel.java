package com.tms.threed.previewPanel.client;

import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tms.threed.previewPanel.client.buttonBars.exterior.ExteriorButtonHandler;
import com.tms.threed.previewPanel.client.dragToSpin.ClearGif;
import com.tms.threed.previewPanel.client.dragToSpin.DragToSpin;
import smartsoft.util.lang.shared.ImageSize;
import smartsoft.util.lang.shared.Path;

import javax.annotation.Nonnull;

public class ViewPanel extends AbsolutePanel {

    private final ThreedImagePanel threedImagePanel;
    private final DragToSpin<ClearGif> dragToSpin;
    private final ClearGif dragDiv;
    private final BlinkOverlay blinkOverlay;

    public ViewPanel(int panelIndex, ImageSize imageSize) {
        this(panelIndex, imageSize, false);
    }

    public ViewPanel(int panelIndex, ImageSize imageSize, boolean dragEnabled) {

        this.threedImagePanel = new ThreedImagePanel(panelIndex, imageSize);
        add(this.threedImagePanel, 0, 0);

        this.blinkOverlay = new BlinkOverlay();
        add(this.blinkOverlay, 0, 0);

        this.dragDiv = new ClearGif(imageSize);
        this.dragToSpin = new DragToSpin<ClearGif>();

        add(this.dragDiv, 0, 0);

        setImageSize(imageSize);

        this.dragToSpin.attachToTarget(dragDiv);

        setDragEnabled(dragEnabled);

    }

    public int getPanelIndex() {
        return threedImagePanel.getPanelIndex();
    }

    public void setImageSize(ImageSize imageSize) {
        setPixelSize(imageSize.getWidth(), imageSize.getHeight());

        if (blinkOverlay != null) {
            blinkOverlay.setPixelSize(imageSize.getWidth(), imageSize.getHeight());
        }
        if (dragDiv != null) {
            dragDiv.setImageSize(imageSize);
        }

        threedImagePanel.setImageSize(imageSize);
    }

    public void setExteriorButtonHandler(ExteriorButtonHandler angleButtonHandler) {
        dragToSpin.setExteriorButtonHandler(angleButtonHandler);
    }

    public void setDragEnabled(boolean dragEnabled) {
        dragToSpin.setEnabled(dragEnabled);
    }

    public void doFeatureBlink(Path pngToBlink) {
        blinkOverlay.doFeatureBlink(pngToBlink);
    }

    public void setThreedImagePanelListener(ThreedImagePanel.ThreedImagePanelListener threedImagePanelListener) {
        threedImagePanel.setListener(threedImagePanelListener);
    }

    public void showMessage(String shortMessage, final String longMessage, String color) {
        threedImagePanel.showMessage(shortMessage, longMessage, color);
    }

    public void setImageUrls(@Nonnull ImmutableList<Path> urls) {
        threedImagePanel.setImageUrls(urls);
    }
}

