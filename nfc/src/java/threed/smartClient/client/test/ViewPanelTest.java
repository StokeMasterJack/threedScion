package threed.smartClient.client.test;

import com.google.common.collect.ImmutableSet;
import com.google.gwt.user.client.ui.FlowPanel;
import threed.core.threedModel.shared.SeriesKey;
import threed.smartClient.client.api.ThreedSession;
import threed.smartClient.client.api.ThreedSessionFactory;
import threed.smartClient.client.ui.ViewPanel;
import threed.smartClient.client.ui.ViewPanelModel;
import threed.smartClient.client.util.futures.Future;
import threed.smartClient.client.util.futures.OnSuccess;

public class ViewPanelTest extends FlowPanel {

    public ViewPanelTest() {
        ThreedSessionFactory f = new ThreedSessionFactory();
        f.setProfileKey("wStd");
        f.setSeriesKey(SeriesKey.AVALON_2011);
//        f.setRepoBaseUrl(new Path(""));
        final Future<ThreedSession> threedSessionFuture = f.createSession();

        threedSessionFuture.success(new OnSuccess() {
            @Override
            public void call() {
                onThreedSessionReady(threedSessionFuture.getResult());
            }
        });

    }

    private void onThreedSessionReady(final ThreedSession threedSession) {
        ViewPanelModel viewPanelModel = threedSession.createViewPanelModel(0);


        ViewPanel viewPanel = new ViewPanel(viewPanelModel);
        add(viewPanel);

        threedSession.setPicksRaw(ImmutableSet.of("Base", "V6", "6AT", "070"));

        AngleButtonPanel angleButtonPanel = new AngleButtonPanel();
        add(angleButtonPanel);

        angleButtonPanel.setListener(new AngleButtonPanel.Listener() {
            @Override
            public void onNext() {
                threedSession.nextAngle();
            }

            @Override
            public void onPrevious() {
                threedSession.previousAngle();
            }
        });
    }


}
