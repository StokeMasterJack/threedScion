package com.tms.threed.threedFramework.previewPanel.client.summary;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.AngleButtonPanel;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.exterior.ExteriorButtonPanel;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.getSimpleName;

public class FooterPanel extends AbsolutePanel {

    private static int PREFERRED_HEIGHT_PX = 34;

    public FooterPanel(ExteriorButtonPanel exteriorButtonPanel) {
        exteriorButtonPanel.setVisible(true);
        addButtonPanel(exteriorButtonPanel);
    }

    void addButtonPanel(AngleButtonPanel angleButtonPanel) {

        FlowPanel fp = new FlowPanel();
        fp.setWidth("100%");
        fp.add(angleButtonPanel);

        int gap = 4;
        int top = PREFERRED_HEIGHT_PX - angleButtonPanel.getPreferredHeightPx() - gap;
        add(fp, 0, top);
    }

    public int getPreferredHeightPx() {
        return PREFERRED_HEIGHT_PX;
    }


}
