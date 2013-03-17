package c3i.smartClient.client.model;

import c3i.imageModel.shared.ImLayer;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.PngSpec;
import c3i.imageModel.shared.ViewKey;
import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.events.ValueChangeTopic;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Set;

public class LayerState {

    private final ValueChangeTopic<LayerState> change = new ValueChangeTopic<LayerState>("LayerStateChangeTopic");

    private final Set<ImLayer> hiddenLayers = Sets.newHashSet();

    private final ViewSession viewSession;
    private final ImView view;

    public LayerState(ViewSession viewSession) {
        this.viewSession = viewSession;
        this.view = viewSession.getView();
    }

    public void enableLayer(ImLayer layer) {
        checkPngMode("enableLayer");
        boolean removed = hiddenLayers.remove(layer);
        if (removed) {
            fire();
        }
    }

    public void disableLayer(ImLayer layer) {
        checkPngMode("disableLayer");
        boolean added = hiddenLayers.add(layer);
        if (added) {
            fire();
        }
    }


    public void toggleLayer(ImLayer layer) {
        checkPngMode("toggleLayer");
        if (hiddenLayers.contains(layer)) {
            hiddenLayers.remove(layer);
            fire();
        } else {
            hiddenLayers.add(layer);
            fire();
        }
    }


    public void enableAll() {
        checkPngMode("enableAll");
        hiddenLayers.clear();
        fire();
    }

    public void enableNone() {
        checkPngMode("enableNone");
        List<ImLayer> layers = view.getLayers();
        for (ImLayer layer : layers) {
            hiddenLayers.add(layer);
        }
        fire();
    }

    public boolean isLayerEnabled(ImLayer layer) {
        checkPngMode("isLayerEnabled");
        return !hiddenLayers.contains(layer);
    }


    public boolean isEnabled(PngSpec png) {
        checkPngMode("isEnabled");
        return isLayerEnabled(png.getLayer());
    }

    public void addChangeListener(ChangeListener<LayerState> l) {
        change.add(l);
    }

    public void removeChangeListener(ChangeListener<LayerState> l) {
        change.remove(l);
    }

    private void fire() {
        if (isPngMode()) {
            change.fire(this);
        }
    }

    private void checkPngMode(String opName) {
        if (!isPngMode()) {
            throw new IllegalStateException(opName + " is only allowed in pngMode");
        }
    }

    private boolean isPngMode() {
        return viewSession.imageMode().get().isPngMode();
    }

    public ViewKey getViewKey() {
        return view.getViewKey();
    }

    public Set<ImLayer> getHiddenLayers() {
        return hiddenLayers;
    }
}
