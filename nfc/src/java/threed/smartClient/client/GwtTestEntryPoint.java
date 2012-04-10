package threed.smartClient.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import threed.smartClient.client.test.ViewPanelTest;

public class GwtTestEntryPoint implements EntryPoint {

    public void onModuleLoad() {
        RootPanel.get("viewPanel").add(new ViewPanelTest());
    }


}
