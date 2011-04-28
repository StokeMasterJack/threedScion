package com.tms.threed.threedAdmin.jpgGen.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.tms.threed.threedAdmin.main.client.JpgWidthListBox;
import com.tms.threed.threedAdmin.main.client.MyDialogBox;
import com.tms.threed.threedAdmin.main.client.services.ThreedAdminService1Async;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.repo.shared.TagCommit;
import com.tms.threed.threedFramework.threedCore.shared.SeriesId;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.capFirstLetter;


public class StartJpgJobDialog extends MyDialogBox {

    private final SeriesKey seriesKey;

    private final JpgWidthListBox jpgWidthListBox;
    private final TagCommitListBox tagListBox;
    private ThreedAdminService1Async service;
    private boolean canceled;


    public StartJpgJobDialog(final ThreedAdminService1Async service, final SeriesKey seriesKey,RtConfig rtConfig) {
        super("Start Jpg Generator Job");
        this.seriesKey = seriesKey;
        this.service = service;

        jpgWidthListBox = new JpgWidthListBox(rtConfig);

        tagListBox = new TagCommitListBox(service);
        tagListBox.fetchTags(seriesKey);

//        tagListBox.addItem("Loading versions..");
//        jpgWidthListBox.addItem("Loading jpg widths versions..");

        tagListBox.setWidth("9em");
        jpgWidthListBox.setWidth("9em");

        setSize("100%", "100");

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

        t.setWidget(3, 0, new TLabel("JPG Width:"));

        t.setWidget(3, 1, jpgWidthListBox);


        FlowPanel buttonPanel = new FlowPanel();
//        buttonPanel.getElement().getStyle().setBackgroundColor("#CCCCCC");
        Button startButton = new Button("Start");
        Button cancelButton = new Button("Cancel");
        buttonPanel.add(cancelButton);
        buttonPanel.add(startButton);

//        startButton.getElement().getStyle().setMarginRight(1, Style.Unit.EM);
        cancelButton.getElement().getStyle().setMarginTop(2, Style.Unit.EM);
        cancelButton.getElement().getStyle().setMarginRight(1, Style.Unit.EM);

        t.setWidget(4, 0, buttonPanel);
        t.getFlexCellFormatter().setColSpan(4, 0, 2);
        t.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);

        fp.add(t);

        startButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {


                TagCommit selectedTagCommit = tagListBox.getSelectedTagCommit();

                JpgWidth jpgWidth = jpgWidthListBox.getSelectedJpgWidth();

                SeriesId seriesId = new SeriesId(seriesKey, selectedTagCommit.getRootTreeId());
                service.startJpgJob(seriesId, jpgWidth);


                StartJpgJobDialog.this.hide();

            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                canceled = true;
                StartJpgJobDialog.this.hide();
            }
        });


        fp.getElement().getStyle().setPadding(1, Style.Unit.EM);


        setWidget(fp);


    }

    public boolean isCanceled() {
        return canceled;
    }
}
