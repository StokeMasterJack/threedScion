package threed.admin.client;

import com.google.gwt.user.client.ui.Composite;
import threed.admin.client.featurePicker.VarPanel;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

public class FeaturePickerPanel extends Composite {

    private VarPanel rootVarPanel;

    public void setRootVarPanel(VarPanel rootVarPanel) {
        this.rootVarPanel = rootVarPanel;
        initWidget(rootVarPanel);

//        setSize("100%", "99%");

//        getElement().getStyle().setBackgroundColor("yellow");
//        getElement().getStyle().setProperty("borderBottom", "#CCCCCC 4px solid");
    }

    public void refresh() {
        rootVarPanel.refresh();
    }
}
