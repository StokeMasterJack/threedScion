package c3i.admin.client.jpgGen;

import c3i.imgGen.shared.JobId;
import c3i.imgGen.shared.Stats;
import com.google.gwt.dom.client.Style;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import java.util.logging.Level;import java.util.logging.Logger;
import smartsoft.util.gwt.client.dialogs.MyDialogBox;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;

import java.util.Date;

public class FinalStatsDialog extends MyDialogBox {

    private final FlexTable t = new FlexTable();
    private final JpgGenClient service;

    public FinalStatsDialog(final JpgGenClient service, JobId jobId) {
        super("Final Stats");
        this.service = service;

        FlowPanel fp = new FlowPanel();

        class TLabel extends Label {
            TLabel(String text) {
                super(text);
                getElement().getStyle().setPadding(1, Style.Unit.EM);
                getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
            }
        }

        t.setWidget(0, 0, new TLabel("Start Time:"));
        t.setHTML(0, 1, "Loading...");

        t.setWidget(1, 0, new TLabel("End Time:"));
        t.setHTML(1, 1, "Loading...");

        t.setWidget(2, 0, new TLabel("Duration:"));
        t.setHTML(2, 1, "Loading...");

        t.setWidget(3, 0, new TLabel("Time spent reading pngs:"));
        t.setHTML(3, 1, "Loading...");

        t.setWidget(4, 0, new TLabel("Time spent combining pngs:"));
        t.setHTML(4, 1, "Loading...");

        t.setWidget(5, 0, new TLabel("Time spent scaling image:"));
        t.setHTML(5, 1, "Loading...");

        t.setWidget(6, 0, new TLabel("Time spent writing combined image to disk:"));
        t.setHTML(6, 1, "Loading...");

        t.setWidget(7, 0, new TLabel("finalStatus:"));
        t.setHTML(7, 1, "Loading...");


        fp.add(t);


        fp.getElement().getStyle().setPadding(1, Style.Unit.EM);

        fp.setSize("30em", "30em");

        setWidget(fp);

        log.log(Level.INFO, "Fetching stats...");

        Req<Stats> request = service.getJpgGenFinalStats(jobId);
        request.onSuccess = new SuccessCallback<Stats>() {

            @Override
            public void call(Req<Stats> r) {
                log.log(Level.INFO, "Fetching stats complete!");
                refresh(r.result);
            }
        };


    }

    private void refresh(Stats stats) {
        t.setHTML(0, 1, DATE_FORMAT.format(new Date(stats.masterJobStartTime)));
        long et;
        if (stats.masterJobEndTime == -1) {
            et = System.currentTimeMillis();
        } else {
            et = stats.masterJobEndTime;
        }

        t.setHTML(1, 1, DATE_FORMAT.format(new Date(et)));

        t.setHTML(2, 1, NUMBER_FORMAT.format(stats.getDurationInMinutes()) + " minutes");

        t.setHTML(3, 1, stats.readSrcPngDeltaSum + "");

        t.setHTML(4, 1, stats.combinePngsDeltaSum + "");

        t.setHTML(5, 1, stats.maybeScalePngDeltaSum + "");

        t.setHTML(6, 1, stats.writeBaseImageDeltaSum + "");

        t.setHTML(7, 1, stats.finalStatus + "");

    }

    private static final DateTimeFormat DATE_FORMAT = DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT);
    private static final NumberFormat NUMBER_FORMAT = NumberFormat.getDecimalFormat();


    private static Logger log = Logger.getLogger(FinalStatsDialog.class.getName());
}
