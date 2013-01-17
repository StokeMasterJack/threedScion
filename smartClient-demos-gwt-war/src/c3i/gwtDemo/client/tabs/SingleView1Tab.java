package c3i.gwtDemo.client.tabs;

import c3i.smartClient.client.model.ThreedSession;
import c3i.smartClient.client.skins.angleSelectors.simple.SimpleSpinControl;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.user.client.ui.FlowPanel;

public class SingleView1Tab extends FlowPanel {

    public SingleView1Tab(ThreedSession threedSession) {
        add(new ViewPanel(threedSession, 0));
        add(new SimpleSpinControl(threedSession));
    }


}
