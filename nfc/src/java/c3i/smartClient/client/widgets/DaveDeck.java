package c3i.smartClient.client.widgets;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.ArrayList;

public class DaveDeck extends Composite {

    private final SimplePanel simplePanel = new SimplePanel();

    private final ArrayList<IsWidget> widgets = new ArrayList<IsWidget>();

    public DaveDeck() {
        initWidget(simplePanel);
    }

    public void add(IsWidget w) {
        widgets.add(w);
    }

    public void setSelectedIndex(int index) {
        IsWidget w = widgets.get(index);
        simplePanel.setWidget(w);
    }

}
