package com.tms.threed.threedAdmin.jpgGen.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.tms.threed.threedAdmin.main.client.services.ThreedAdminService1Async;


public class NewSeriesRepoDialog extends DialogBox {

    private final ThreedAdminService1Async service;
    private final TextBox seriesNameTextBox;
    private final TextBox seriesYearTextBox;

    public NewSeriesRepoDialog(final ThreedAdminService1Async service) {
        super(true);
        this.service = service;
        setSize("100%", "100");
        setHTML("<div style='font-weight:bold;text-align:center'>New Series Repo</div>");


        FlowPanel fp = new FlowPanel();


        class TLabel extends Label {
            TLabel(String text) {
                super(text);
                getElement().getStyle().setPadding(1, Style.Unit.EM);
                getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
            }
        }

        FlexTable t = new FlexTable();

        t.setWidget(0, 0, new TLabel("Series Name:"));
        this.seriesNameTextBox = new TextBox();
        t.setWidget(0, 1, this.seriesNameTextBox);

        t.setWidget(1, 0, new TLabel("Series Year:"));
        this.seriesYearTextBox = new TextBox();
        t.setWidget(1, 1, this.seriesYearTextBox);


        FlowPanel buttonPanel = new FlowPanel();
        Button okButton = new Button("Create Repo");
        Button cancelButton = new Button("Cancel");
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

        cancelButton.getElement().getStyle().setMarginTop(2, Style.Unit.EM);
        cancelButton.getElement().getStyle().setMarginRight(1, Style.Unit.EM);

        t.setWidget(4, 0, buttonPanel);
        t.getFlexCellFormatter().setColSpan(4, 0, 2);
        t.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);

        fp.add(t);

        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {


                String seriesName = seriesNameTextBox.getText();
                String seriesYear = seriesYearTextBox.getText();

                service.createNewRepo(seriesName, seriesYear, new AsyncCallback<String>() {
                    @Override public void onFailure(Throwable caught) {
                    }

                    @Override public void onSuccess(String result) {
                        //To change body of implemented methods use File | Settings | File Templates.
                    }
                });

                NewSeriesRepoDialog.this.hide();

            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                NewSeriesRepoDialog.this.hide();
            }
        });


        fp.getElement().getStyle().setPadding(1, Style.Unit.EM);

        setWidget(fp);


        seriesNameTextBox.addAttachHandler(new AttachEvent.Handler() {
            @Override public void onAttachOrDetach(AttachEvent event) {
                seriesNameTextBox.setFocus(true);
            }
        });


    }


}
