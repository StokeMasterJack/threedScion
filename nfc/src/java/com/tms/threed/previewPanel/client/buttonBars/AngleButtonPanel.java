package com.tms.threed.previewPanel.client.buttonBars;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

abstract public class AngleButtonPanel extends Composite {

    @Override protected void initWidget(Widget widget) {
        super.initWidget(widget);
        setVisible(false);
    }

    public abstract int getPreferredHeightPx();

}

