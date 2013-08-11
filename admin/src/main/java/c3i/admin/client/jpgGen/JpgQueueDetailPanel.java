package c3i.admin.client.jpgGen;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import java.util.logging.Level;import java.util.logging.Logger;
import smartsoft.util.gwt.client.ui.tabLabel.TabAware;
import smartsoft.util.gwt.client.ui.tabLabel.TabLabel;
import c3i.admin.shared.jpgGen.ExecutorStatus;
import c3i.admin.shared.jpgGen.JobId;
import smartsoft.util.gwt.client.rpc.FailureCallback;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;

import java.util.ArrayList;


public class JpgQueueDetailPanel extends DockLayoutPanel implements TabAware{


    private final JpgGenClient service;

    CellTable<ExecutorStatus> table;

    private final Timer timer;

    private final JobId jobId;

    public JpgQueueDetailPanel(final JpgGenClient service,JobId jobId) {
        super(Style.Unit.EM);
        this.jobId = jobId;
        this.service = service;

        table = buildTable();

        final FlowPanel fp = new FlowPanel();

        fp.add(table);

        table.getElement().getStyle().setMarginTop(2, Style.Unit.EM);

        refreshContent();

        fp.getElement().getStyle().setMargin(2, Style.Unit.EM);


        add(fp);


        timer = new Timer() {
            @Override
            public void run() {
                refreshContent();
            }
        };

        timer.scheduleRepeating(2000);


    }


    private CellTable<ExecutorStatus> buildTable() {

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
        if (jobId == null) {
            throw new IllegalStateException("jobId must be non-null before calling refreshContent()");
        }

        Req<ArrayList<ExecutorStatus>> request = service.getQueueDetails(jobId);

        request.onSuccess = new SuccessCallback<ArrayList<ExecutorStatus>>() {

            @Override
            public void call(Req<ArrayList<ExecutorStatus>> r) {
                ArrayList<ExecutorStatus> result = r.result;
                if (result == null || result.size() == 0) {
                    log.log(Level.INFO, "Job no longer valid");
                    afterClose();
                } else {
                    table.setRowCount(result.size());
                    table.setRowData(0, result);
                    table.redraw();
                    table.setVisible(true);
                }
            }
        };

        request.onFailure = new FailureCallback<ArrayList<ExecutorStatus>>() {
            @Override
            public void call(Req<ArrayList<ExecutorStatus>> r) {
                afterClose();
            }
        };

    }


    @Override
    public void afterClose() {
        if (timer != null) {
            timer.cancel();
        }
    }

    @Override
    public TabLabel getTabLabel() {
        return new TabLabel("Job Detail");
    }

    private static Logger log = Logger.getLogger(JpgQueueDetailPanel.class.getName());
}
