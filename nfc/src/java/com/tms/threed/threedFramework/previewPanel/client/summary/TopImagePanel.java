package com.tms.threed.threedFramework.previewPanel.client.summary;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.tms.threed.threedFramework.previewPanel.client.PreviewPanelStyles;
import com.tms.threed.threedFramework.previewPanel.client.ThreedImagePanel;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.ClearGif;
import com.tms.threed.threedFramework.previewPanel.client.dragToSpin.DragToSpin;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;

public class TopImagePanel extends AbsolutePanel {

    public static final int PREFERRED_WIDTH_PX = 374;
    public static final int PREFERRED_HEIGHT_PX = 229;

    public TopImagePanel(ThreedImagePanel threedImagePanel, DragToSpin<ClearGif> dragToSpin, FooterPanel footerPanel) {
        this.setPixelSize(PREFERRED_WIDTH_PX, PREFERRED_HEIGHT_PX);

        ClearGif dragDiv = new ClearGif(new ImageSize(PREFERRED_WIDTH_PX, PREFERRED_HEIGHT_PX));
        dragToSpin.attachToTarget(dragDiv);
        dragToSpin.setEnabled(true);

        footerPanel.setPixelSize(PREFERRED_WIDTH_PX, footerPanel.getPreferredHeightPx());
        int footerPanelTop = PREFERRED_HEIGHT_PX - footerPanel.getPreferredHeightPx();

        add(threedImagePanel, 0, 0);
        add(dragDiv, 0, 0);
        add(footerPanel, 0, footerPanelTop);

        PreviewPanelStyles.set(this);

        getElement().setId("gwt-debug-TopImagePanel");

    }


}