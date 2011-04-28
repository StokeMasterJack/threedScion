package com.tms.threed.threedFramework.imageModel.shared;

import com.tms.threed.threedFramework.imageModel.shared.slice.SimplePicks;

public interface ILayer {

    boolean isVisible();

    IPng computePngForPicks(SimplePicks simplePicks, int angle);

    void toggleVisibility();

    void setVisible(boolean visible);

    String getSimpleName();
}
