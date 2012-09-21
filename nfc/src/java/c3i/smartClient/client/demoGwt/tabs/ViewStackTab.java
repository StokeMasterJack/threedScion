package c3i.smartClient.client.demoGwt.tabs;

import c3i.smartClient.client.model.ThreedSession;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.user.client.ui.FlowPanel;

public class ViewStackTab extends FlowPanel {

    public ViewStackTab(ThreedSession threedSession) {

        add(new ViewPanel(threedSession, 0));
        add(new ViewPanel(threedSession, 1));


    }


}