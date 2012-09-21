package c3i.admin.client;

import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import c3i.util.shared.events.ChangeListener;
import c3i.core.imageModel.shared.ImageMode;
import c3i.core.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.ViewSession;
import c3i.smartClient.client.model.ViewsSession;
import c3i.smartClient.client.model.event.ViewChangeListener;

import java.util.ArrayList;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

public class StatusPanels extends DeckLayoutPanel {

    private final Series series;
    private final ThreedAdminModel threedAdminModel;
    private final ViewsSession viewsSession;

    private final ArrayList<StatusPanel> statusPanels;

    public StatusPanels(final Series series) {
        this.series = series;
        this.threedAdminModel = series.getThreedAdminModel();
        this.viewsSession = threedAdminModel.getViewsSession();


        statusPanels = new ArrayList<StatusPanel>();

        ImmutableList<ViewSession> viewSessions = viewsSession.getViewSessions();
        for (ViewSession viewSession : viewSessions) {
            StatusPanelModel statusPanelModel = new StatusPanelModel(viewSession, series);
            StatusPanel statusPanel = new StatusPanel(statusPanelModel);
            statusPanels.add(statusPanel);
            add(statusPanel);
        }

        showWidget(0);

        viewsSession.addViewChangeListener(new ViewChangeListener() {
            @Override
            public void onChange(ViewKey newValue) {
                refresh();
            }
        });

        viewsSession.imageMode().addChangeListener(new ChangeListener<ImageMode>() {
            @Override
            public void onChange(ImageMode newValue) {
                refresh();
            }
        });

        addStyleName(getSimpleName(this));

        setWidth("100%");

        refresh();

    }

    private void refresh() {
        for (StatusPanel statusPanel : statusPanels) {
            statusPanel.refresh();
        }
        int viewIndex = viewsSession.getViewIndex();
        showWidget(viewIndex);

    }
}
