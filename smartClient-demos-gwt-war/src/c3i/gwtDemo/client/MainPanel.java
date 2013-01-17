package c3i.gwtDemo.client;

import c3i.gwtDemo.client.tabs.SingleView1Tab;
import c3i.gwtDemo.client.tabs.SingleView2Tab;
import c3i.gwtDemo.client.tabs.SingleView3Tab;
import c3i.gwtDemo.client.tabs.ViewDeck1Tab;
import c3i.gwtDemo.client.tabs.ViewDeck2Tab;
import c3i.gwtDemo.client.tabs.ViewStackTab;
import c3i.smartClient.client.model.ThreedSession;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import static smartsoft.util.shared.Strings.getSimpleName;

public class MainPanel extends DockLayoutPanel {

    private ThreedSession threedSession;
    private final TabLayoutPanel tabPanel;

    public MainPanel() {
        super(Style.Unit.EM);
        HTML title = new HTML("<h1>Configured 3D Images - Smart Client Demos</h1>");

        tabPanel = new TabLayoutPanel(2, Style.Unit.EM);

        addNorth(title, 5);
        add(tabPanel);

    }

    public void onThreedSessionReady(final ThreedSession threedSession) {
        this.threedSession = threedSession;

        addTab(new SingleView1Tab(threedSession));
        addTab(new SingleView2Tab(threedSession));
        addTab(new SingleView3Tab(threedSession));
        addTab(new ViewDeck1Tab(threedSession));
        addTab(new ViewDeck2Tab(threedSession));
        addTab(new ViewStackTab(threedSession));

        threedSession.setPicksRaw(ImmutableSet.of("Base", "V6", "6AT", "070"));
    }

    private void addTab(Widget widget) {
        tabPanel.add(widget, getSimpleName(widget).replace("Tab",""));
    }


}
