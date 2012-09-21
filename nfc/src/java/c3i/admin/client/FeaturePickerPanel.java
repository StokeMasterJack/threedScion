package c3i.admin.client;

import com.google.gwt.user.client.ui.Composite;
import c3i.admin.client.featurePicker.VarPanel;

public class FeaturePickerPanel extends Composite {

    private VarPanel rootVarPanel;


    public void setRootVarPanel(VarPanel rootVarPanel) {
        this.rootVarPanel = rootVarPanel;
        initWidget(rootVarPanel);

        setHeight("100%");

    }

    public void refresh() {
        rootVarPanel.refresh();
    }
}
