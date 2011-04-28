package com.tms.threed.threedAdmin.main.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.tms.threed.threedAdmin.jpgGen.client.TagCommitListBox;
import com.tms.threed.threedFramework.repo.shared.TagCommit;
import com.tms.threed.threedAdmin.main.client.services.ThreedAdminService1Async;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.capFirstLetter;


public class OpenSeriesDialog extends MyDialogBox {

    private final TagCommitListBox tagListBox;

    public OpenSeriesDialog(final ThreedAdminService1Async service, final SeriesKey seriesKey, final OpenSeriesCallback openSeriesCallback) {
        super("Open Series");
        setSize("100%", "100");


        tagListBox = new TagCommitListBox(service);
        tagListBox.fetchTags(seriesKey);


        FlowPanel fp = new FlowPanel();
//        fp.setSize("50em", "50em");
//        fp.getElement().getStyle().setBackgroundColor("yellow");


        class TLabel extends Label {
            TLabel(String text) {
                super(text);
                getElement().getStyle().setPadding(1, Style.Unit.EM);
                getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
            }
        }

        FlexTable t = new FlexTable();

        t.setWidget(0, 0, new TLabel("Series Name:"));
        t.setHTML(0, 1, capFirstLetter(seriesKey.getName()));

        t.setWidget(1, 0, new TLabel("Series Year:"));
        t.setHTML(1, 1, seriesKey.getYear() + "");

        t.setWidget(2, 0, new TLabel("Series Version:"));
        t.setWidget(2, 1, tagListBox);


        FlowPanel buttonPanel = new FlowPanel();
//        buttonPanel.getElement().getStyle().setBackgroundColor("#CCCCCC");
        Button okButton = new Button("Open");
        Button cancelButton = new Button("Cancel");
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

//        startButton.getElement().getStyle().setMarginRight(1, Style.Unit.EM);
        cancelButton.getElement().getStyle().setMarginTop(2, Style.Unit.EM);
        cancelButton.getElement().getStyle().setMarginRight(1, Style.Unit.EM);

        t.setWidget(4, 0, buttonPanel);
        t.getFlexCellFormatter().setColSpan(4, 0, 2);
        t.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);

        fp.add(t);

        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {


                TagCommit tagCommit = tagListBox.getSelectedTagCommit();

                openSeriesCallback.onOpen(seriesKey, tagCommit);

                OpenSeriesDialog.this.hide();

            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                OpenSeriesDialog.this.hide();
            }
        });


        fp.getElement().getStyle().setPadding(1, Style.Unit.EM);

        setWidget(fp);



    }


    public static interface OpenSeriesCallback {
        void onOpen(SeriesKey seriesKey, TagCommit version);
    }

}
