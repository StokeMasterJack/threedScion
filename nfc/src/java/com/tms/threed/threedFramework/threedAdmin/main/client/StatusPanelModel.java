package com.tms.threed.threedFramework.threedAdmin.main.client;

import com.tms.threed.threedFramework.threedAdmin.featurePicker.client.CurrentUiPicks;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.imageModel.shared.IImageStack;

public interface StatusPanelModel {

    String getUserPicks();

    String getFixedPicks();
    String getThreedModelUrl();

    IImageStack getImageStack();

    CurrentUiPicks getCurrentUiPicks();

    FeatureModel getFeatureModel();

    boolean isPngMode();

}
