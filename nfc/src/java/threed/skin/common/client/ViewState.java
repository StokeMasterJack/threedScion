package threed.skin.common.client;

import threed.core.threedModel.shared.ViewKey;

public interface ViewState {

    ViewKey getCurrentView();

    void previousAngle();

    void nextAngle();

    void setCurrentAngle(int newAngle);

    int getCurrentAngle();

}