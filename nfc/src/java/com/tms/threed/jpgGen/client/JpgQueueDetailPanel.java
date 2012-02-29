package com.tms.threed.jpgGen.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tms.threed.threedAdmin.main.client.UiContext;
import com.tms.threed.threedAdmin.main.client.services.JpgGenServiceAsync;
import com.tms.threed.jpgGen.shared.ExecutorStatus;
import com.tms.threed.util.gwtUtil.client.UiLog;

import java.util.List;


public class JpgQueueDetailPanel extends DockLayoutPanel implements TabCloseListener {


    private final JpgGenServiceAsync service;

    CellTable<ExecutorStatus> table2 = buildTable2();

    private final UiLog ctx;
    private final Timer timer;

    private final String jobId;

    public JpgQueueDetailPanel(final JpgGenServiceAsync service,final UiLog ctx, String jobId) {
        super(Style.Unit.EM);
        this.ctx = ctx;
        this.jobId = jobId;
        this.service = service;

        final FlowPanel fp = new FlowPanel();

        fp.add(table2);

        table2.getElement().getStyle().setMarginTop(2, Style.Unit.EM);

        refreshContent();

        fp.getElement().getStyle().setMargin(2, Style.Unit.EM);


        add(fp);


        timer = new Timer() {
            @Override public void run() {
                refreshContent();
            }
        };

        timer.scheduleRepeating(2000);


    }


    private CellTable<ExecutorStatus> buildTable2() {

        TextColumn<ExecutorStatus> queueNameCountColumn = new TextColumn<ExecutorStatus>() {
            @Override
            public String getValue(ExecutorStatus o) {
                return o.getName();
            }
        };

        TextColumn<ExecutorStatus> taskCountColumn = new TextColumn<ExecutorStatus>() {
            @Override
            public String getValue(ExecutorStatus o) {
                return o.getTaskCount() + "";
            }
        };

        TextColumn<ExecutorStatus> activeTaskCountColumn = new TextColumn<ExecutorStatus>() {
            @Override
            public String getValue(ExecutorStatus o) {
                return o.getActiveTaskCount() + "";
            }
        };

        TextColumn<ExecutorStatus> completedTaskCountColumn = new TextColumn<ExecutorStatus>() {
            @Override
            public String getValue(ExecutorStatus o) {
                return o.getCompletedTaskCount() + "";
            }
        };

        TextColumn<ExecutorStatus> shutdownColumn = new TextColumn<ExecutorStatus>() {
            @Override
            public String getValue(ExecutorStatus o) {
                return o.isShutdown() + "";
            }
        };

        TextColumn<ExecutorStatus> terminatedColumn = new TextColumn<ExecutorStatus>() {
            @Override
            public String getValue(ExecutorStatus o) {
                return o.isTerminated() + "";
            }
        };


        CellTable<ExecutorStatus> t = new CellTable<ExecutorStatus>();

        t.addColumn(queueNameCountColumn, "Queue");
        t.addColumn(taskCountColumn, "All Tasks");
        t.addColumn(activeTaskCountColumn, "Active Tasks");
        t.addColumn(completedTaskCountColumn, "Completed Tasks");
        t.addColumn(shutdownColumn, "Shutdown");
        t.addColumn(terminatedColumn, "Terminated");


        return t;


    }

    private void refreshContent() {

        service.fetchJpgQueueDetails(jobId, new JpgGenServiceAsync.FetchQueueDetailsCallback() {
            @Override public void onSuccess(List<ExecutorStatus> queueDetails) {


                table2.setRowCount(queueDetails.size());
                table2.setRowData(0, queueDetails);
                table2.redraw();
                table2.setVisible(true);
            }

            @Override public void onError(String text) {
                System.err.println(text);
                afterClose();
                ctx.log("Error calling fetchJpgQueueDetails: " + text);
            }

            @Override public void badJobId() {
                afterClose();
                ctx.log("Job no longer valid");
            }
        });


    }


    @Override public void afterClose() {
        if (timer != null) {
            timer.cancel();
        }
    }


}
