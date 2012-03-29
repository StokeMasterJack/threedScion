package smartClient.client;

import com.google.gwt.core.client.EntryPoint;
import org.timepedia.exporter.client.ExporterUtil;
import smartClient.client.util.futures.Future;
import smartClient.client.util.futures.OnSuccess;
import smartsoft.util.gwt.client.Console;

public class SmartClient implements EntryPoint {

    public void onModuleLoad() {

        try {
            ExporterUtil.exportAll();
        } catch (Exception e) {
            Console.log("Problem with gwt-export: " + e.toString());
            e.printStackTrace();
        }

        try {
            doInitThreed();
        } catch (Exception e) {
            Console.log("Error in initThreed: " + e.toString());
            e.printStackTrace();
        }





    }

    private native void doInitThreed() /*-{
        if ($wnd.initThreed) {
            try {
                $wnd.initThreed();
            } catch (e) {
                console.error("Error calling initThreed");
                console.error(e);
            }
        } else {
            console.log("no initThreed method to call")
        }
    }-*/;


}
