package c3i.smartClient.client;

import com.google.gwt.core.client.EntryPoint;
import org.timepedia.exporter.client.ExporterUtil;
import smartsoft.util.gwt.client.IELogFix;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportJavaScriptEntryPoint implements EntryPoint {

    public ExportJavaScriptEntryPoint() {
        IELogFix.installFix();
    }

    public void onModuleLoad() {

        try {
            System.err.println("Before ExporterUtil.exportAll()");
            ExporterUtil.exportAll();
            System.err.println("After ExporterUtil.exportAll()");
        } catch (Exception e) {
            log.log(Level.SEVERE, "Problem with gwt-export: " + e.toString());
            System.err.println("Problem with gwt-export: " + e.toString());
            e.printStackTrace();
        }

        try {
            doOnThreedReady();
        } catch (Exception e) {
            log.log(Level.INFO, "Error in initThreed: " + e.toString());
            System.err.println("Error in initThreed: " + e.toString());
            e.printStackTrace();
        }

    }

    private native void doOnThreedReady() /*-{
        if ($wnd.onThreedReady) {
            try {
                $wnd.onThreedReady();
            } catch (e) {
                if (window.console) console.error("Error calling onThreedReady");
                if (window.console) console.error(e);
            }
        } else {
            if (window.console) console.log("no onThreedReady method to call")
        }
    }-*/;


    private static Logger log = Logger.getLogger(ExportJavaScriptEntryPoint.class.getName());

}
