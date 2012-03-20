package com.tms.threed.threedAdmin.client;

import com.tms.threed.threedAdmin.client.featurePicker.CurrentUiPicks;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.imageModel.shared.IImageStack;

public interface StatusPanelModel {

    String getUserPicks();

    String getFixedPicks();
    String getThreedModelUrl();

    IImageStack getImageStack();

    CurrentUiPicks getCurrentUiPicks();

    FeatureModel getFeatureModel();

    boolean isPngMode();

}
