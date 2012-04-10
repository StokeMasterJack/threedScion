package threed.skin.summaryPanel.client;

import com.google.gwt.user.client.ui.FlowPanel;
import threed.skin.common.client.AbstractPreviewPanel;
import threed.skin.common.client.PreviewPanelStyles;

public class PreviewPanelSummary extends AbstractPreviewPanel {

    private static final int PREFERRED_WIDTH_PX = 374;
    private static final int PREFERRED_HEIGHT_PX = 229 * 2;

    public PreviewPanelSummary(TopImagePanel topPanel, BottomImagePanel bottomPanel) {
        FlowPanel flow = new FlowPanel();
        flow.add(topPanel);
        flow.add(bottomPanel);
        initWidget(flow);
        PreviewPanelStyles.set(this);
    }

    @Override public int getPreferredWidthPx() {
        return PREFERRED_WIDTH_PX;
    }

    @Override public int getPreferredHeightPx() {
        return PREFERRED_HEIGHT_PX;
    }

}