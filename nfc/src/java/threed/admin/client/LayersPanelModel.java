package threed.admin.client;

import threed.core.imageModel.shared.ILayer;
import threed.core.imageModel.shared.IPng;

import java.util.List;

public interface LayersPanelModel {
    void selectAll();

    void selectNone();

    List<ILayer> getLayers();

    IPng getPngForLayer(ILayer layer);

    void toggleLayer(ILayer layer);

    boolean isPngMode();

    boolean isInvalidBuild();
    boolean isValidBuild();
}
