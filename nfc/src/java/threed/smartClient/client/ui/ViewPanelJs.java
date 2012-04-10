package threed.smartClient.client.ui;

import com.google.gwt.user.client.ui.RootPanel;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import threed.smartClient.client.api.ThreedSession;

@Export("ViewPanel")
public class ViewPanelJs implements Exportable {

    private final ViewPanelModel model;
    private final ViewPanel viewPanel;

    public ViewPanelJs(String elementId, ThreedSession threedSession) {
        model = threedSession.createViewPanelModel(0);
        viewPanel = new ViewPanel(model);
        RootPanel container = RootPanel.get(elementId);
        container.add(viewPanel);
    }

    //Do not delete - required by gwt-exporter
    private ViewPanelJs() {
        // gwt-exporter requires a zero arg constructor
        throw new UnsupportedOperationException();
    }

    //Do not delete - required by gwt-exporter
    public void setPixelSize(int width, int height) {
        // Without at least ONE non-constructor method, gwt-exporter will NOT export the class
        viewPanel.setPixelSize(width, height);
    }

}
