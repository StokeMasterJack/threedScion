package com.tms.threed.threedAdmin.client;

import com.tms.threed.threedAdmin.client.featurePicker.CurrentUiPicks;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;

public interface StatusPanelModel {

    String getUserPicks();

    String getFixedPicks();
    String getThreedModelUrl();

    ImageStack getImageStack();

    CurrentUiPicks getCurrentUiPicks();

    FeatureModel getFeatureModel();

    boolean isPngMode();

    JpgWidth getCurrentJpgWidth();

}
