package com.tms.threed.threedAdmin.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.*;
import com.tms.threed.repo.shared.CommitHistory;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import smartsoft.util.gwt.client.dialogs.MyDialogBox;


public class SeriesCommitDialog extends MyDialogBox {

    public Command onSeriesCommitSelected;

    private CommitHistory selectedCommit;

    private final SeriesKey seriesKey;
    private final CommitHistory seriesHead; //null means there is no head for this seriesKey - no commits

    /**
     *
     * @param seriesKey
     * @param seriesHead
     */
    public SeriesCommitDialog(SeriesKey seriesKey, CommitHistory seriesHead) {
        super("Pick Series Version");
        if (seriesKey == null) {
            throw new NullPointerException();
        }
        if (seriesHead == null) {
            throw new NullPointerException();
        }
        this.seriesKey = seriesKey;
        this.seriesHead = seriesHead;
        initUi();
    }

    private void initUi() {
        FlowPanel fp = new FlowPanel();
        fp.add(new HTML("<div style='font-size:2em;font-weight:bold;padding-top:0em;padding-bottom:.5em;'>" + seriesKey.toStringPretty() + "</div>"));
        fp.add(createMainTable());
        fp.getElement().getStyle().setPadding(1, Style.Unit.EM);
        setWidget(fp);
        center();

    }

    public CommitHistory getSelectedCommit() {
        return selectedCommit;
    }

    private Widget createMainTable() {

        final DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
        final DateTimeFormat timeFormat = DateTimeFormat.getFormat("hh:mm:ss");

        final TimeZone timeZone = TimeZone.createTimeZone(8 * 60);
        FlexTable table = new FlexTable();

        table.setBorderWidth(1);
        renderHeader(table);


        CommitHistory ch = seriesHead;
        while (true) {
            if (ch == null) {
                break;
            }


            int row = table.getRowCount();
            int col = 0;
            table.setText(row, col++, ch.getTag());
            table.setText(row, col++, ch.getCommitId().getName());
            table.setText(row, col++, ch.getRootTreeId().getName());
            table.setText(row, col++, ch.getCommitter());
            //            table.setText(row, col++, ch.getShortMessage());
            table.setText(row, col++, dateFormat.format(ch.getCommitTimeAsDate(), timeZone));
            table.setText(row, col++, timeFormat.format(ch.getCommitTimeAsDate(), timeZone));

            table.setText(row, col++, ch.getSpecialTags());
            table.setWidget(row, col++, createOpenButton(ch));

            if (ch.hasParents()) {
                final CommitHistory parent = ch.getParents()[0];
                ch = parent;
            } else {
                ch = null;
            }

        }

        SimplePanel simplePanel = new SimplePanel();
        simplePanel.addStyleName("commitPickList");
        simplePanel.setWidget(table);
        return simplePanel;


    }

    private Widget createOpenButton(final CommitHistory ch) {
        final Anchor anchor = new Anchor("Open");
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (onSeriesCommitSelected == null) {
                    throw new IllegalStateException();
                }
                selectedCommit = ch;
                SeriesCommitDialog.this.hide();
                onSeriesCommitSelected.execute();
            }
        });
        return anchor;
    }

    private int renderHeader(FlexTable table) {
        int headerRowCount = 1;

        int col = 0;

        table.setText(0, col, "Tag");

        col++;

        table.setText(0, col, "CommitId");

        col++;

        table.setText(0, col, "RootTreeId (content hash)");

        col++;

        table.setText(0, col, "Committer");

        col++;

        table.setText(0, col, "Date");

        col++;

        table.setText(0, col, "Time");

        col++;

        table.setText(0, col, "Special Tags");

        col++;

        table.getRowFormatter().getElement(0).getStyle().setFontWeight(Style.FontWeight.BOLD);

        return headerRowCount;
    }


}
