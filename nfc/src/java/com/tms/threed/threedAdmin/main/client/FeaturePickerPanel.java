package com.tms.threed.threedAdmin.main.client;

import com.google.gwt.user.client.ui.SimplePanel;
import com.tms.threed.threedAdmin.featurePicker.client.VarPanel;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.getSimpleName;

public class FeaturePickerPanel extends SimplePanel{

    private VarPanel rootVarPanel;

    public FeaturePickerPanel() {
        setSize("100%","100%");
    }

    public void setRootVarPanel(VarPanel rootVarPanel) {
        this.rootVarPanel = rootVarPanel;
        setWidget(rootVarPanel);
        rootVarPanel.setSize("100%","99%");
    }

    public void refresh() {
        rootVarPanel.refresh();
    }
}
