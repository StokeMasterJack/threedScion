package c3i.gwtDemo.client.tabs;

import c3i.smartClient.client.model.ThreedSession;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.user.client.ui.FlowPanel;

public class SingleView3Tab extends FlowPanel {

    public SingleView3Tab(ThreedSession threedSession) {
        add(new ViewPanel(threedSession, 1));
    }


}
