package c3i.smartClient.client.widgets;

import c3i.smartClient.client.model.ThreedSession;
import com.google.gwt.user.client.ui.RootPanel;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;

import java.util.logging.Level;
import java.util.logging.Logger;

@Export("ViewPanel")
public class ViewPanelJs implements Exportable {

    private final ViewPanel viewPanel;

    public ViewPanelJs(String elementId, ThreedSession threedSession) {
        this(elementId, threedSession, -1);
    }

    public ViewPanelJs(String elementId, ThreedSession threedSession, int viewIndex) {
        viewPanel = new ViewPanel(threedSession, viewIndex == -1 ? null : viewIndex);
        RootPanel container = RootPanel.get(elementId);
        container.add(viewPanel);
        log.log(Level.INFO, "viewIndex = " + viewIndex);


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


    private static Logger log = Logger.getLogger(ViewPanelJs.class.getName());


}
