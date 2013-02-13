package c3i.admin.client;

import c3i.imageModel.shared.PngSpec;
import c3i.smartClient.client.model.LayerState;
import java.util.logging.Level;import java.util.logging.Logger;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.imageModel.shared.ImLayer;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageMode;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.smartClient.client.model.ViewSession;
import c3i.smartClient.client.model.ViewsSession;

import java.util.List;

import static c3i.core.threedModel.shared.ImFeatureModel.toSimplePicks;

public class LayersPanelModel {

    private final ViewSession viewSession;

    //cache
    private final ViewsSession viewsSession;
    private final ThreedModel threedModel;
    private final ImView view;
    private final List<ImLayer> layers;


    public LayersPanelModel(ViewSession viewSession) {
        this.viewSession = viewSession;
        this.viewsSession = viewSession.getParent();
        this.threedModel = viewsSession.getThreedModel();
        this.view = viewSession.getView();
        layers = this.view.getLayers();
    }

    public ViewSession getViewSession() {
        return viewSession;
    }

    public void enableAll() {
        LayerState m = viewSession.getLayerState();
        m.enableAll();
    }

    public void enableNone() {
        LayerState m = viewSession.getLayerState();
        m.enableNone();
    }

    public List<ImLayer> getLayers() {
        return layers;
    }


    public PngSpec getPngForLayer(ImLayer layer) {
        FixedPicks fixedPicks = viewsSession.fixedPicks().get();
        if (fixedPicks == null) throw new IllegalStateException();
        return layer.getPngSpec(toSimplePicks(fixedPicks), viewSession.getAngle());
    }

    public void toggleLayer(final ImLayer layer) {
        log.log(Level.INFO, "LayersPanelModel.toggleLayer");
        LayerState m = viewSession.getLayerState();
        m.toggleLayer(layer);
    }

    public boolean isPngMode() {
        ImageMode imageMode = viewsSession.imageMode().get();
        return imageMode == ImageMode.PNG;
    }

    public boolean isInvalidBuild() {
        FixedPicks fixedPicks = viewsSession.fixedPicks().get();
        if (fixedPicks == null) {
            return false;
        } else {
            return fixedPicks.isInvalidBuild();
        }
    }

    public boolean isValidBuild() {
        return !isInvalidBuild();
    }

    private static Logger log = Logger.getLogger(LayersPanelModel.class.getName());
}
