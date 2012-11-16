package c3i.admin.client.jpgGen;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.view.client.SingleSelectionModel;
import java.util.logging.Level;import java.util.logging.Logger;
import smartsoft.util.gwt.client.ui.tabLabel.TabCreator;
import smartsoft.util.gwt.client.dialogs.MyDialogBox;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;
import smartsoft.util.gwt.client.ui.tabLabel.TabAware;
import smartsoft.util.gwt.client.ui.tabLabel.TabLabel;
import c3i.core.common.shared.SeriesId;
import c3i.core.imageModel.shared.Profile;
import c3i.admin.shared.jpgGen.JobId;
import c3i.admin.shared.jpgGen.JobState;
import c3i.admin.shared.jpgGen.JobStatus;
import c3i.admin.shared.jpgGen.JobStatusItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static smartsoft.util.shared.Strings.notEmpty;


public class JpgQueueMasterPanel extends DockLayoutPanel implements TabAware {

    private static final DateTimeFormat FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);


    private final JpgGenClient service;

    CellTable<MasterJobStatus> table1 = new CellTable<MasterJobStatus>();

    List<MasterJobStatus> masterRowData = new ArrayList<MasterJobStatus>();


    private final Timer timer;

    public JpgQueueMasterPanel(final JpgGenClient service, final TabCreator tabCreator) {
        super(Style.Unit.EM);
        this.service = service;

        refreshContent();

        table1.setSelectionModel(new SingleSelectionModel<Object>());


        TextColumn<MasterJobStatus> startTimeColumn = new TextColumn<MasterJobStatus>() {
            @Override
            public String getValue(MasterJobStatus o) {
                return FORMAT.format(o.startTime);
            }
        };


        TextColumn<MasterJobStatus> seriesColumn = new TextColumn<MasterJobStatus>() {
            @Override
            public String getValue(MasterJobStatus o) {
                return o.seriesId.getSeriesKey().toString();
            }
        };

        TextColumn<MasterJobStatus> versionColumn = new TextColumn<MasterJobStatus>() {
            @Override
            public String getValue(MasterJobStatus o) {
                return o.getVersionDisplay();
            }
        };

        TextColumn<MasterJobStatus> jpgWidthColumn = new TextColumn<MasterJobStatus>() {
            @Override
            public String getValue(MasterJobStatus o) {
                return o.getProfile().getKey();
            }
        };


//        TextColumn<MasterJobStatus> statusColumn = new TextColumn<MasterJobStatus>() {
//            @Override
//            public String getValue(MasterJobStatus o) {
//                return o.getState();
//            }
//
//
//        };


        ClickableTextCell clickableTextCell = new ClickableTextCell();
        Column<MasterJobStatus, String> statusColumn = new Column<MasterJobStatus, String>(clickableTextCell) {

            @Override
            public String getValue(MasterJobStatus o) {
                return o.getState();
            }

        };

        statusColumn.setFieldUpdater(new FieldUpdater<MasterJobStatus, String>() {

            @Override
            public void update(int index, final MasterJobStatus o, String value) {


                if (notEmpty(o.exception)) {

                    MyDialogBox d = new MyDialogBox("Jpg Gen Exception");


                    ScrollPanel scrollPanel = new ScrollPanel(new HTML("<pre>" + o.exception + "</pre>"));
                    scrollPanel.setSize("80em","50em");

                    d.setWidget(scrollPanel);
                    d.center();
                    d.show();

                }

            }
        });


        TextColumn<MasterJobStatus> sliceCountColumn = new TextColumn<MasterJobStatus>() {
            @Override
            public String getValue(MasterJobStatus o) {
                if (o.sliceCount == null) return null;
                else return o.sliceCount + "";
            }
        };

        TextColumn<MasterJobStatus> slicesCompleteColumn = new TextColumn<MasterJobStatus>() {
            @Override
            public String getValue(MasterJobStatus o) {
                if (o.sliceCount == null) return null;

                else return o.slicesComplete + "";
            }
        };


        TextColumn<MasterJobStatus> jpgCountColumn = new TextColumn<MasterJobStatus>() {
            @Override
            public String getValue(MasterJobStatus o) {
                Integer jpgCount = o.jpgCount;
                if (jpgCount == null) return null;
                return jpgCount + "";
            }
        };


        TextColumn<MasterJobStatus> jpgsCompleteColumn = new TextColumn<MasterJobStatus>() {
            @Override
            public String getValue(MasterJobStatus o) {
                if (o.jpgsComplete == null) {
                    return null;
                } else {
                    return o.jpgsComplete + "";
                }
            }
        };

        TextColumn<MasterJobStatus> jpgCompletePercentColumn = new TextColumn<MasterJobStatus>() {
            @Override
            public String getValue(MasterJobStatus o) {
                return o.getGenPercentString();
            }
        };

        ButtonCell buttonCell = new ButtonCell();
        Column<MasterJobStatus, String> cancelRemoveCountColumn = new Column<MasterJobStatus, String>(buttonCell) {

            @Override
            public String getValue(MasterJobStatus o) {

                if (o.isTerminal()) {
                    return "Remove";
                } else {
                    return "Cancel";
                }
            }


        };

        cancelRemoveCountColumn.setFieldUpdater(new FieldUpdater<MasterJobStatus, String>() {

            @Override
            public void update(int index, final MasterJobStatus o, String value) {


                if (o.isTerminal()) {

                    log.log(Level.INFO, "Removing job[" + o.jobId + "]");
                    masterRowData.remove(index);
                    table1.setRowCount(masterRowData.size());
                    table1.setRowData(0, masterRowData);
                    table1.redraw();

                    service.removeJob(o.jobId);

                } else {
                    log.log(Level.INFO, "Canceling job[" + o.jobId + "] ...");
                    service.cancelJob(o.jobId);
                }
            }
        });


        Column<MasterJobStatus, String> detailButtonColumn = new Column<MasterJobStatus, String>(new ButtonCell()) {
            @Override
            public String getValue(MasterJobStatus o) {
                return "Details";
            }
        };


        detailButtonColumn.setFieldUpdater(new FieldUpdater<MasterJobStatus, String>() {
            @Override
            public void update(int index, MasterJobStatus o, String value) {
                JpgQueueDetailPanel tabAware = new JpgQueueDetailPanel(service, o.jobId);
                tabCreator.addTab(tabAware);
            }
        });


        Column<MasterJobStatus, String> statsButtonColumn = new Column<MasterJobStatus, String>(new ButtonCell()) {
            @Override
            public String getValue(MasterJobStatus o) {
                return "Stats";
            }
        };


        statsButtonColumn.setFieldUpdater(new FieldUpdater<MasterJobStatus, String>() {
            @Override
            public void update(int index, MasterJobStatus o, String value) {
                JobId jobId = o.jobId;
                FinalStatsDialog finalStatsDialog = new FinalStatsDialog(service, jobId);
                finalStatsDialog.center();
                finalStatsDialog.show();
            }
        });


        /*
       int jpgCompleteCount;
        */

        TextColumn<MasterJobStatus> analysisCountColumn = new TextColumn<MasterJobStatus>() {
            @Override
            public String getValue(MasterJobStatus o) {
                return o.getAnalysisPercentString();
            }
        };

        table1.addColumn(seriesColumn, "Series");
        table1.addColumn(versionColumn, "Version");
        table1.addColumn(jpgWidthColumn, "Jpg Width");
        table1.addColumn(startTimeColumn, "Job Start Time");
        table1.addColumn(statusColumn, "Job Status");
        table1.addColumn(sliceCountColumn, "Analysis Count");
        table1.addColumn(slicesCompleteColumn, "Analysis Complete");
        table1.addColumn(analysisCountColumn, "Analysis Percent Complete");
        table1.addColumn(jpgCountColumn, "Jpg Count");
        table1.addColumn(jpgsCompleteColumn, "Jpg Count Complete");
        table1.addColumn(jpgCompletePercentColumn, "Jpg Percent Complete");
        table1.addColumn(cancelRemoveCountColumn, "Cancel or Remove Job");
        table1.addColumn(detailButtonColumn, "Details");
        table1.addColumn(statsButtonColumn, "Stats");


        final FlowPanel fp = new FlowPanel();

        fp.add(createButtonPanel());
        fp.add(table1);


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

    private FlowPanel createButtonPanel() {
        FlowPanel p = new FlowPanel();
        Button button = new Button("Remove all terminal jobs");
        p.add(button);
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                service.removeTerminal();
            }
        });
        return p;
    }


    private void refreshContent() {

        Req<ArrayList<JobStatusItem>> request = service.getQueueStatus();

        request.onSuccess = new SuccessCallback<ArrayList<JobStatusItem>>() {

            @Override
            public void call(Req<ArrayList<JobStatusItem>> r) {
                ArrayList<JobStatusItem> jobs = r.result;
                masterRowData.clear();

                for (int i = 0; i < jobs.size(); i++) {

                    JobStatusItem job = jobs.get(i);

                    MasterJobStatus s = new MasterJobStatus();

                    s.startTime = job.getStartTime();
                    s.seriesId = job.getJobSpec().getSeriesId();
                    s.jobId = job.getJobId();

                    JobStatus status = job.getStatus();
                    s.jpgCount = status.getJpgCount();
                    s.sliceCount = status.getSliceCount();
                    s.slicesComplete = status.getSlicesComplete();


                    s.jpgsComplete = status.getJpgsComplete();

                    s.state = status.getState().name();

                    s.profile = job.getJobSpec().getProfile();

                    s.exception = job.getStatus().getSerializedStackTrace();


                    masterRowData.add(s);
                }

                Collections.sort(masterRowData, new Comparator<MasterJobStatus>() {
                    @Override
                    public int compare(MasterJobStatus o1, MasterJobStatus o2) {
                        return o1.startTime.compareTo(o2.startTime);
                    }
                });


                table1.setRowCount(masterRowData.size());
                table1.setRowData(0, masterRowData);


                table1.redraw();
            }
        };

    }


    class MasterJobStatus {

        JobId jobId;
        Date startTime;
        SeriesId seriesId;
        Integer jpgCount;
        Integer sliceCount;

        Integer slicesComplete;
        Integer jpgsComplete;

        String state;

        public String tag;
        Profile profile;

        String exception;

        Integer getAnalysisPercent() {
            if (sliceCount == null) return null;
            if (slicesComplete == null) return null;
            double d1 = sliceCount;
            double d2 = slicesComplete;
            double p = d2 / d1 * 100.0;
            return (int) p;
        }

        Integer getGenPercent() {
            if (jpgCount == null) return null;
            if (jpgsComplete == null) return null;
            double d1 = jpgCount;
            double d2 = jpgsComplete;
            double p = d2 / d1 * 100.0;
            return (int) p;
        }

        String getAnalysisPercentString() {
            Integer p = getAnalysisPercent();
            if (p == null) return null;
            return p + "%";
        }

        String getGenPercentString() {
            Integer p = getGenPercent();
            if (p == null) return null;
            return p + "%";
        }

        boolean isComplete() {
            if (jpgCount == null || jpgsComplete == null) {
                return false;
            }
            return jpgCount.equals(jpgsComplete);
        }

        boolean isTerminal() {
            return isComplete() || state.equals(JobState.Error.name()) || state.equals(JobState.Canceled.name());
        }

        String getState() {
            return state;
        }

        String getVersionDisplay() {
            if (tag != null) {
                return tag;
            } else {
                return seriesId.getRootTreeId().getName();
            }
        }


        public Profile getProfile() {
            return profile;
        }
    }



    @Override
    public void afterClose() {
        if (timer != null) {
            timer.cancel();
        }
    }

    private TabLabel tabLabel;

    @Override
    public TabLabel getTabLabel() {
        if (tabLabel == null) {
            tabLabel = new TabLabel("Jpg Queue Status");
        }
        return tabLabel;
    }


    private static Logger log = Logger.getLogger(JpgQueueMasterPanel.class.getName());


}
