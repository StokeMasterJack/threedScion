package com.tms.threed.previewPanel.client.main;

import com.google.gwt.layout.client.Layout;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.tms.threed.previewPanel.client.main.thumbsPanel.ThumbPanel;
import com.tms.threed.previewPanel.client.main.thumbsPanel.ThumbsPanel;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

public class BottomPanel extends Composite {

    private final LayoutPanel layoutPanel = new LayoutPanel();

    public static final int PREFERRED_HEIGHT_PX = ThumbPanel.PREFERRED_HEIGHT_PX;

    public BottomPanel(MsrpPanel msrpPanel, ThumbsPanel thumbsPanel) {
        initWidget(layoutPanel);
        setWidth("100%");
        setHeight("100%");


        layoutPanel.add(thumbsPanel);
        layoutPanel.setWidgetHorizontalPosition(thumbsPanel, Layout.Alignment.END);

        layoutPanel.add(msrpPanel);
        layoutPanel.setWidgetHorizontalPosition(msrpPanel, Layout.Alignment.BEGIN);

    }


}