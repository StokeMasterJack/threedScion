package threed.admin.client;

import threed.admin.client.featurePicker.CurrentUiPicks;
import threed.core.featureModel.shared.FeatureModel;
import threed.core.imageModel.shared.ImImageStack;
import threed.core.threedModel.shared.JpgWidth;

public interface StatusPanelModel {

    String getUserPicks();

    String getFixedPicks();
    String getThreedModelUrl();

    ImImageStack getImageStack();

    CurrentUiPicks getCurrentUiPicks();

    FeatureModel getFeatureModel();

    boolean isPngMode();

    JpgWidth getCurrentJpgWidth();

}
