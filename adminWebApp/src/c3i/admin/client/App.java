package c3i.admin.client;

import c3i.core.common.shared.BrandKey;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.ui.UiContext;
import c3i.core.common.shared.SeriesKey;
import c3i.admin.client.jpgGen.JpgGenClient;

public class App {

    private final UiContext uiContext;
    private final ThreedAdminClient threedAdminClient;
    private final JpgGenClient jpgGenClient;

    public App(final UiContext uiContext,BrandKey brandKey) {
        this.uiContext = uiContext;
        threedAdminClient = new ThreedAdminClient();
        jpgGenClient = new JpgGenClient(brandKey);
    }

    public UiContext getUiContext() {
        return uiContext;
    }

    public ThreedAdminClient getThreedAdminClient() {
        return threedAdminClient;
    }

    public JpgGenClient getJpgGenClient() {
        return jpgGenClient;
    }

    public void localCheckin(final SeriesKey sk) {
        Console.log("Checking-in: [" + sk + "] ...");
        threedAdminClient.addAllAndCommit(sk, null, null);
    }
}
