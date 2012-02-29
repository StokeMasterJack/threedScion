package com.tms.threedToyota.byt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.ui.RootPanel;
import com.tms.threedToyota.byt.client.main.PreviewPaneMain;
import com.tms.threedToyota.byt.client.summary.PreviewPaneSummary;
import com.tms.threed.util.gwtUtil.client.Console;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class PreviewPanelAdaptor implements EntryPoint {

    private static PreviewPane previewPane = null;

    public PreviewPanelAdaptor() {
        Console.log("PreviewPanelAdaptor Constructor");

    }

    private static boolean isSummaryPage() {

        Element summaryElement = Document.get().getElementById("flashFrame");
        Element mainElement = Document.get().getElementById("flash_frame");

        if (summaryElement != null && mainElement == null) {
            return true;
        } else if (summaryElement == null && mainElement != null) {
            return false;
        } else {
            throw new IllegalStateException("PreviewPanel host page must contain either an element named flashFrame(summary) or flash_frame(main)");
        }
    }

    private static String getDivId() {
        if (isSummaryPage()) return "flashFrame";
        else return "flash_frame";
    }

    private static PreviewPane createPreviewPane() {
        if (isSummaryPage()) {
            return new PreviewPaneSummary();
        } else {
            return new PreviewPaneMain();
        }
    }

    public void onModuleLoad() {

        previewPane = createPreviewPane();

        RootPanel.get(getDivId()).add(previewPane);
        setRhsLoadedFlag();
        registerHooks();

        Console.log("PreviewPanelAdaptor onModuleLoad - complete");
    }

    private static native void setRhsLoadedFlag()/*-{
        $wnd.rhs_loaded = '1';
    }-*/;

    private static native void registerHooks()/*-{
        var ssCallback = @com.tms.threedToyota.byt.client.PreviewPanelAdaptor::setSeries(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;);
        $wnd.Configurator.FlashBridge.setSeries = $entry(ssCallback);
    }-*/;

    public static void setSeries(
            String flash_key,
            String model,
            String option,
            String excolor,
            String incolor,
            String accessory,
            String msrp,
            String seriesname,
            String helpimgid,
            String helpimgurl,
            String flashdescription) {


        if (model == null || model.length() == 0) return;
        if (incolor == null || incolor.length() == 0) return;
        previewPane.updateImage(flash_key, model, option, excolor, incolor, accessory, msrp, seriesname, helpimgid, helpimgurl, flashdescription);
    }


}
