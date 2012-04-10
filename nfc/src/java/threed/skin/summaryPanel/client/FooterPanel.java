package threed.skin.summaryPanel.client;

import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import threed.skin.common.client.buttonBars.AngleButtonPanel;
import threed.skin.common.client.buttonBars.exterior.ExteriorButtonPanel;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

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
