package com.tms.threed.threedAdmin.jpgGen.client;

import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.view.client.SingleSelectionModel;
import com.tms.threed.threedAdmin.main.client.MyDialogBox;
import com.tms.threed.threedAdmin.main.client.UiContext;
import com.tms.threed.threedAdmin.main.client.services.*;
import com.tms.threed.threedFramework.jpgGen.shared.JobId;
import com.tms.threed.threedFramework.jpgGen.shared.JobState;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedModel.shared.SeriesId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.notEmpty;


public class JpgQueueMasterPanel extends DockLayoutPanel implements TabCloseListener {

    private static final DateTimeFormat FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM);


    private final JpgGenServiceAsync service;

    CellTable<MasterJobStatus> table1 = new CellTable<MasterJobStatus>();

    List<MasterJobStatus> masterRowData = new ArrayList<MasterJobStatus>();


    private final UiContext ctx;
    private final Timer timer;

    public JpgQueueMasterPanel(final JpgGenServiceAsync service, final UiContext ctx) {
        super(Style.Unit.EM);
        this.ctx = ctx;
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
                return o.getJpgWidth().stringValue();
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

            @Override public void update(int index, final MasterJobStatus o, String value) {



                if (notEmpty(o.exception)){

                    MyDialogBox d = new MyDialogBox("Jpg Gen Exception");
                    d.setWidget(new HTML("<pre>" + o.exception + "</pre>"));
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

            @Override public void update(int index, final MasterJobStatus o, String value) {


                if (o.isTerminal()) {

                    ctx.showMessage("Removing job[" + o.jobId + "]");
                    masterRowData.remove(index);
                    table1.setRowCount(masterRowData.size());
                    table1.setRowData(0, masterRowData);
                    table1.redraw();

                    service.removeJob(o.jobId, new JpgGenServiceAsync.RemoveJobCallBack() {
                        @Override public void onSuccess() {
                            ctx.showMessage("Job[" + o.jobId + "] removed");
                        }
                    });
                } else {
                    ctx.showMessage("Canceling job[" + o.jobId + "]");
                    service.cancelJob(o.jobId, new JpgGenServiceAsync.CancelJobCallBack() {
                        @Override public void onSuccess() {
                            ctx.showMessage("Job[" + o.jobId + "] canceled");
                        }
                    });
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
            @Override public void update(int index, MasterJobStatus o, String value) {
                JpgQueueDetailPanel widget = new JpgQueueDetailPanel(service, ctx, o.jobId);
                ctx.addTab(widget, "Job Detail");
            }
        });


        Column<MasterJobStatus, String> statsButtonColumn = new Column<MasterJobStatus, String>(new ButtonCell()) {
            @Override
            public String getValue(MasterJobStatus o) {
                return "Stats";
            }
        };


        statsButtonColumn.setFieldUpdater(new FieldUpdater<MasterJobStatus, String>() {
            @Override public void update(int index, MasterJobStatus o, String value) {
                JobId jobId = new JobId(o.jobId);
                FinalStatsDialog finalStatsDialog = new FinalStatsDialog(ctx, service, jobId);
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
            @Override public void run() {
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
            @Override public void onClick(ClickEvent event) {
                service.removeTerminal(new JpgGenServiceAsync.RemoveJobCallBack() {
                    @Override public void onSuccess() {
                        ctx.showMessage("Terminal jobs removed!");
                    }
                });
            }
        });
        return p;
    }


    private void refreshContent() {
        service.fetchJpgQueueStatus(new JpgGenServiceAsync.FetchJpgStatusCallback() {
            @Override public void onSuccess(JSONArray jsonArray) {

                masterRowData.clear();
                for (int i = 0; i < jsonArray.size(); i++) {
                    JSONObject o = jsonArray.get(i).isObject();

                    MasterJobStatus s = new MasterJobStatus();
                    s.startTime = new Date((long) o.get("startTime").isNumber().doubleValue());
                    s.seriesId = JsonHelper.getSeriesId(o);
                    s.jobId = o.get("jobId").isString().stringValue();

                    JSONValue jpgCount = o.get("jpgCount");
                    if (jpgCount == null) {
                        s.jpgCount = null;
                    } else {
                        s.jpgCount = (int) jpgCount.isNumber().doubleValue();
                    }

                    JSONValue sliceCount = o.get("sliceCount");
                    if (sliceCount != null) {
                        s.sliceCount = (int) sliceCount.isNumber().doubleValue();
                    } else {
                        s.sliceCount = null;
                    }


                    JSONValue slicesComplete = o.get("slicesComplete");
                    if (slicesComplete != null) {
                        s.slicesComplete = (int) slicesComplete.isNumber().doubleValue();
                    } else {
                        s.slicesComplete = null;
                    }


                    JSONValue jpgsComplete = o.get("jpgsComplete");
                    if (jpgsComplete != null) {
                        s.jpgsComplete = (int) jpgsComplete.isNumber().doubleValue();
                    } else {
                        s.jpgsComplete = null;
                    }


                    s.state = o.get("state").isString().stringValue();

                    JSONValue tag = o.get("tag");
                    if (tag != null) {
                        String tagName = tag.isString().stringValue();
                        s.tag = tagName;
                    }


                    s.jpgWidth = new JpgWidth(o.get("jpgWidth").isString().stringValue());


                    JSONValue jsException = o.get("exception");
                    if (jsException != null) {
                        String exception = jsException.isString().stringValue();
                        s.exception = exception;
                    }


                    masterRowData.add(s);
                }

                Collections.sort(masterRowData, new Comparator<MasterJobStatus>() {
                    @Override public int compare(MasterJobStatus o1, MasterJobStatus o2) {
                        return o1.startTime.compareTo(o2.startTime);
                    }
                });


                table1.setRowCount(masterRowData.size());
                table1.setRowData(0, masterRowData);


                table1.redraw();
            }

            @Override public void onError(int statusCode, String statusText, String responseText) {
                ctx.showMessage("Error returned from server: " + statusCode + " " + statusText + " " + responseText);
                System.err.println("statusCode = " + statusCode);
                System.err.println("statusText = " + statusText);
                System.err.println("responseText = " + responseText);
                System.err.println();
            }
        });
    }


    class MasterJobStatus {

        String jobId;
        Date startTime;
        SeriesId seriesId;
        Integer jpgCount;
        Integer sliceCount;

        Integer slicesComplete;
        Integer jpgsComplete;

        String state;

        public String tag;
        JpgWidth jpgWidth;

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


        public JpgWidth getJpgWidth() {
            return jpgWidth;
        }
    }

    @Override public void afterClose() {
        if (timer != null) {
            timer.cancel();
        }
    }


}
