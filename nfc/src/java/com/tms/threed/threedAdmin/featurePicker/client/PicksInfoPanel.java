package com.tms.threed.threedAdmin.featurePicker.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksChangeHandler;
//import com.tms.threed.featureModel.shared.picks.PicksChangeEvent;
//import com.tms.threed.featureModel.shared.picks.PicksChangeHandler;

public class PicksInfoPanel extends LayoutPanel {

    int r = 0;
    private PicksInfo picksInfo;

    public PicksInfoPanel(PicksInfo picksInfo) {

        this.picksInfo = picksInfo;
        add("IsValid", picksInfo.isValid() + "");
        add("Product ", "47");
        add("Product Count dd", "47");
        add("Prod", "47");

        setSize("100%", "100%");
        getElement().getStyle().setBackgroundColor("yellow");
    }

    public void refresh() {

    }

    public void add(String name, String value) {
        DockLayoutPanel dock = new DockLayoutPanel(Style.Unit.EM);
        dock.getElement().getStyle().setBackgroundColor("pink");

        InlineHTML lbl = new InlineHTML("<b>" + name + ": </b>");
        dock.addWest(lbl, 10);

        final InlineHTML fld = new InlineHTML(value);
        dock.add(fld);

        picksInfo.addPicksChangeHandler(new PicksChangeHandler() {
            @Override public void onPicksChange(PicksChangeEvent e) {
                fld.setHTML(picksInfo.isValid() + "");
            }
        });


        add(dock);
        setWidgetTopHeight(dock, .5 + r * 1.5, Style.Unit.EM, 1, Style.Unit.EM);
        setWidgetLeftRight(dock, 1, Style.Unit.EM, 1, Style.Unit.EM);

        r += 1;
    }
}
