package com.tms.threed.threedFramework.threedAdmin.jpgGen.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tms.threed.threedFramework.repo.shared.CommitHistory;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;

public class CommitPickList extends Composite {

    private CommitHistory head;


    private SimplePanel simplePanel;

    private Callback callback;

    private SeriesKey seriesKey;

    public CommitPickList(SeriesKey seriesKey, CommitHistory head,Callback callback) {
        this.seriesKey = seriesKey;
        this.head = head;
        this.callback = callback;

        this.simplePanel = new SimplePanel();
//        simplePanel.setWidth("100em");

//        simplePanel.getElement().getStyle().setBackgroundColor("red");
        initWidget(simplePanel);

        addStyleName("commitPickList");

        refresh();


    }

    private Widget createNoHeadWidget() {
        FlowPanel flowPanel = new FlowPanel();
        flowPanel.add(new HTML("The [" + seriesKey + "] repo has no versions. Perhaps you need to do a check-in"));

        if (callback != null) {
            final Button checkInButton = new Button("Check-in [" + seriesKey + "] now");
            checkInButton.getElement().getStyle().setMarginTop(2, Style.Unit.EM);
            flowPanel.add(checkInButton);
            checkInButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    callback.checkinButtonClicked(seriesKey);
                }
            });

        }
        return flowPanel;
    }

    private Widget createMainWidget() {
        return createMainTable();
    }


    public CommitHistory getSelected() {
        return head;
    }

    public void refresh() {
        if (head == null) {
            refreshNoHead();
        } else {
            refreshWithHead();
        }
    }

    private void refreshWithHead() {
        simplePanel.setWidget(createMainWidget());
    }

    private void refreshNoHead() {
        simplePanel.setWidget(createNoHeadWidget());
    }

    private Widget createMainTable() {

        final DateTimeFormat dateFormat = DateTimeFormat.getFormat("yyyy-MM-dd");
        final DateTimeFormat timeFormat = DateTimeFormat.getFormat("hh:mm:ss");

        final TimeZone timeZone = TimeZone.createTimeZone(8 * 60);
        FlexTable table = new FlexTable();

        table.setBorderWidth(1);
        renderHeader(table);


        CommitHistory ch = head;
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

        return table;


    }

    private Widget createOpenButton(final CommitHistory ch) {
        final Anchor anchor = new Anchor("Open");
        anchor.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                if (callback != null) {
                    callback.openButtonClicked(seriesKey, ch);
                }
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

//        table.setText(0, col, "Message");
//
//        col++;

        table.setText(0, col, "Date");

        col++;

        table.setText(0, col, "Time");

        col++;

        table.setText(0, col, "Special Tags");

        col++;

        table.getRowFormatter().getElement(0).getStyle().setFontWeight(Style.FontWeight.BOLD);

        return headerRowCount;
    }

    private void setHead(CommitHistory head) {
        this.head = head;
    }

//    public void fetchCommitHistory(final SeriesKey seriesKey) {
//        this.seriesKey = seriesKey;
//
//        service.service2.getCommitHistory(seriesKey, new AsyncCallback<CommitHistory>() {
//
//            @Override
//            public void onSuccess(CommitHistory head) {
////                head.print();
//                setHead(head);
//                refresh();
//            }
//
//            @Override
//            public void onFailure(Throwable e) {
//                if (e instanceof RepoHasNoHeadException) {
//                    setHead(null);
//                    refreshNoHead();
//                } else {
//                    Window.alert("Problem fetching commit tags: " + e.toString());
//                    e.printStackTrace();
//                }
//            }
//
//
//        });

//        service.fetchTags(seriesKey, new FetchTagsCallback() {
//            @Override public void onSuccess(List<TagCommit> tags) {
//                removeRedundantHead(tags);
//                sort(tags);
//                clear();
//                for (TagCommit tag : tags) {
//                    add(tag);
//                }
//
//            }
//        });
//    }


    public static interface Callback {
        void openButtonClicked(SeriesKey seriesKey, CommitHistory commit);

        void checkinButtonClicked(SeriesKey seriesKey);
    }


}
