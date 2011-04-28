package com.tms.threed.threedFramework.previewPanel.client.buttonBars;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.getSimpleName;

abstract public class AngleButtonPanel extends Composite {

    @Override protected void initWidget(Widget widget) {
        super.initWidget(widget);
        setVisible(false);
    }

    public abstract int getPreferredHeightPx();

}

