package com.tms.threed.threedAdmin.main.client;

import com.tms.threed.threedCore.imageModel.shared.ILayer;
import com.tms.threed.threedCore.imageModel.shared.IPng;

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
