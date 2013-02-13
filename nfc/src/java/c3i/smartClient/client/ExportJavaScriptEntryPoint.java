package c3i.smartClient.client;

import com.google.gwt.core.client.EntryPoint;
import org.timepedia.exporter.client.ExporterUtil;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ExportJavaScriptEntryPoint implements EntryPoint {

    public void onModuleLoad() {

        try {
            ExporterUtil.exportAll();
        } catch (Exception e) {
            log.log(Level.INFO, "Problem with gwt-export: " + e.toString());
            e.printStackTrace();
        }

        try {
            doOnThreedReady();
        } catch (Exception e) {
            log.log(Level.INFO, "Error in initThreed: " + e.toString());
            e.printStackTrace();
        }

    }

    private native void doOnThreedReady() /*-{
        if ($wnd.onThreedReady) {
            try {
                $wnd.onThreedReady();
            } catch (e) {
                console.error("Error calling onThreedReady");
                console.error(e);
            }
        } else {
            console.log("no onThreedReady method to call")
        }
    }-*/;


    private static Logger log = Logger.getLogger(ExportJavaScriptEntryPoint.class.getName());

}
