package com.tms.threed.threedAdmin.main.client;

import com.tms.threed.threedAdmin.featurePicker.client.CurrentUiPicks;
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
