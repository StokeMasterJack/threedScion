package c3i.smartClient.client.demoGwt.tabs;

import c3i.smartClient.client.model.ThreedSession;
import c3i.smartClient.client.skins.viewSelectors.SimpleViewSelector;
import c3i.smartClient.client.skins.viewSelectors.ViewSelector;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.user.client.ui.FlowPanel;

public class ViewDeck1Tab extends FlowPanel {

    public ViewDeck1Tab(ThreedSession threedSession) {
        ViewPanel viewPanel = new ViewPanel(threedSession);
        ViewSelector viewSelector = new SimpleViewSelector(threedSession);
        add(viewPanel);
        add(viewSelector);
    }


}