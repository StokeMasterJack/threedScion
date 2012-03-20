package com.tms.threed.threedAdmin.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import smartsoft.util.gwt.client.dialogs.MyDialogBox;

public class NoCommitsDialog extends MyDialogBox {

    private static final String TITLE_BAR = "Pick Series Version";

    public Command onCheckin;
    private final SeriesKey seriesKey;

    public NoCommitsDialog(SeriesKey seriesKey) {
        super(TITLE_BAR);
        if (seriesKey == null) {
            throw new NullPointerException();
        }
        this.seriesKey = seriesKey;
        initUi();
    }

    private void initUi() {
        FlowPanel fp = new FlowPanel();
        fp.add(new HTML("<div style='font-size:2em;font-weight:bold;padding-top:0em;padding-bottom:.5em;'>" + seriesKey.toStringPretty() + "</div>"));
        fp.add(createSimplePanel());
        fp.getElement().getStyle().setPadding(1, Style.Unit.EM);
        setWidget(fp);
        center();
    }


    private SimplePanel createSimplePanel() {

        FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(new HTML("The [" + seriesKey + "] repo has no versions. Perhaps you need to do a check-in"));
        final Button checkInButton = new Button("Check-in [" + seriesKey + "] now");
        checkInButton.getElement().getStyle().setMarginTop(2, Style.Unit.EM);
        flowPanel.add(checkInButton);

        checkInButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (onCheckin == null) {
                    throw new IllegalStateException();
                }
                NoCommitsDialog.this.hide();
                onCheckin.execute();
            }
        });

        SimplePanel simplePanel = new SimplePanel();
        addStyleName("commitPickList");
        simplePanel.setWidget(flowPanel);

        return simplePanel;

    }


}
