package c3i.admin.client;

import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.ui.UiContext;
import c3i.core.common.shared.SeriesKey;
import c3i.jpgGen.client.JpgGenClient;

public class App {

    private final UiContext uiContext;
    private final ThreedAdminClient threedAdminClient;
    private final JpgGenClient jpgGenClient;

    public App(final UiContext uiContext) {
        this.uiContext = uiContext;
        threedAdminClient = new ThreedAdminClient();
        jpgGenClient = new JpgGenClient(uiContext);
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
