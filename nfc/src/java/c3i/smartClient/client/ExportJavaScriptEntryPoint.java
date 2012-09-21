package c3i.smartClient.client;

import com.google.gwt.core.client.EntryPoint;
import org.timepedia.exporter.client.ExporterUtil;
import smartsoft.util.gwt.client.Console;

public class ExportJavaScriptEntryPoint implements EntryPoint {

    public void onModuleLoad() {

        try {
            ExporterUtil.exportAll();
        } catch (Exception e) {
            Console.log("Problem with gwt-export: " + e.toString());
            e.printStackTrace();
        }

        try {
            doOnThreedReady();
        } catch (Exception e) {
            Console.log("Error in initThreed: " + e.toString());
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


}
