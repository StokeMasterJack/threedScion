package c3i.gwtDemo.client;

import c3i.smartClient.client.model.ThreedSessionFuture;
import c3i.smartClient.client.model.ThreedSessionOnSuccess;
import c3i.util.shared.futures.Future;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import smartsoft.util.shared.Path;
import c3i.core.common.shared.SeriesKey;
import c3i.smartClient.client.model.ThreedSession;
import c3i.smartClient.client.model.ThreedSessionFactory;

public class MainEntryPoint implements EntryPoint {

    public void onModuleLoad() {

        final MainPanel mainPanel = new MainPanel();
        RootLayoutPanel.get().add(mainPanel);

        ThreedSessionFactory f = new ThreedSessionFactory();
        f.setProfileKey("wStd");
        f.setSeriesKey(SeriesKey.AVALON_2011);
        f.setRepoBaseUrl(new Path("/configurator-content-v2"));
        final ThreedSessionFuture threedSessionFuture = f.createSession();

        threedSessionFuture.success(new ThreedSessionOnSuccess() {
            @Override
            public void onSuccess(ThreedSession threedSession) {
                mainPanel.onThreedSessionReady(threedSession);
            }
        });



    }


}
