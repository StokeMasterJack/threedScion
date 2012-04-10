package threed.core.imageModel.shared;

import threed.core.imageModel.shared.slice.SimplePicks;

public interface ILayer {

    boolean isVisible();

    IPng computePngForPicks(SimplePicks simplePicks, int angle);

    void toggleVisibility();

    void setVisible(boolean visible);

    String getSimpleName();
}
