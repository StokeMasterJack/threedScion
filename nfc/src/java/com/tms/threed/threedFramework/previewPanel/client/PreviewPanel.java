package com.tms.threed.threedFramework.previewPanel.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.exterior.ExteriorButtonHandler;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.ClearGif;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.DragToSpin;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.List;

public class PreviewPanel extends AbsolutePanel {


    private final ThreedImagePanel threedImagePanel;
    private final DragToSpin<ClearGif> dragToSpin;
    private final ClearGif dragDiv;
    private final BlinkOverlay blinkOverlay;


    public PreviewPanel(int panelIndex, ImageSize imageSize) {
        this(panelIndex, imageSize, false);
    }

    public PreviewPanel(int panelIndex, ImageSize imageSize, boolean dragEnabled) {

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

    public void addLoadingCompleteHandler(ThreedImagePanel.LoadingCompleteHandler loadingCompleteHandler) {
        threedImagePanel.addLoadingCompleteHandler(loadingCompleteHandler);
    }

    public void showMessage(String shortMessage, final String longMessage, String color) {
        threedImagePanel.showMessage(shortMessage, longMessage, color);
    }

    public void setImageUrls(@Nonnull List<Path> urls) {

        threedImagePanel.setImageUrls(urls);
    }
}

