package c3i.gwtDemo.client.tabs;

import c3i.smartClient.client.model.ThreedSession;
import c3i.smartClient.client.skins.angleSelectors.simple.SimpleSpinControl;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.user.client.ui.FlowPanel;

public class SingleView2Tab extends FlowPanel {

    public SingleView2Tab(ThreedSession threedSession) {
        ViewPanel viewPanel = new ViewPanel(threedSession, 0);
        SimpleSpinControl spinControl = new SimpleSpinControl(threedSession);
        spinControl.addStyleName("over");
        viewPanel.add(spinControl);
        add(viewPanel);
    }


}
