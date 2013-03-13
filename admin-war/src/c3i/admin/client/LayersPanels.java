package c3i.admin.client;

import c3i.util.shared.events.ChangeListener;
import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.ui.DeckLayoutPanel;
import c3i.imageModel.shared.ImageMode;
import c3i.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.ViewSession;
import c3i.smartClient.client.model.ViewsSession;
import c3i.smartClient.client.model.event.ViewChangeListener;

import java.util.ArrayList;

import static smartsoft.util.shared.Strings.getSimpleName;

public class LayersPanels extends DeckLayoutPanel {

    private final ViewsSession viewsSession;

    private final ArrayList<LayersPanel> layersPanels;

    public LayersPanels(ThreedAdminModel model) {
        this.viewsSession = model.getViewsSession();
        layersPanels = new ArrayList<LayersPanel>();

        ImmutableList<ViewSession> viewSessions = viewsSession.getViewSessions();
        for (ViewSession viewSession : viewSessions) {
            LayersPanelModel layersPanelModel = new LayersPanelModel(viewSession);
            LayersPanel layersPanel = new LayersPanel(layersPanelModel);
            layersPanels.add(layersPanel);
            add(layersPanel);
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

        setVisible(false);

        setSize("100%", "100%");

    }

    private void refresh() {
        for (LayersPanel layersPanel : layersPanels) {
            layersPanel.refresh();
        }
        int viewIndex = viewsSession.getViewIndex();
        showWidget(viewIndex);
        ImageMode imageMode = viewsSession.imageMode().get();
        setVisible(imageMode.isPngMode());

    }

    public int getPreferredWidthPx() {
        return 300;
    }
}
