package c3i.smartClient.client.skins;

import c3i.smartClient.client.model.ViewModel;
import c3i.smartClient.client.skins.angleSelectors.simple.SimpleSpinControl;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

public class ViewStackSkin implements Skin {

    @Override
    public IsWidget createPreviewPanel(ViewModel viewModel) {

        ViewPanel viewPanel0 = new ViewPanel(viewModel, 0, "ViewPanel-Exterior");
        ViewPanel viewPanel1 = new ViewPanel(viewModel, 1, "ViewPanel-Interior");

        SimpleSpinControl spinControl = new SimpleSpinControl(viewModel);
        spinControl.addStyleName("over");
        viewPanel0.add(spinControl);

        FlowPanel p = new FlowPanel();
        p.add(viewPanel0);
        p.add(viewPanel1);

        return p;
    }

    @Override
    public String getSkinName() {
        return getSimpleName(this);
    }
}
