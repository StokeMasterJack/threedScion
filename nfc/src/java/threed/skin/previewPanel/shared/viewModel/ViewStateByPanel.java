package threed.skin.previewPanel.shared.viewModel;

import threed.skin.common.client.ViewState;
import threed.core.threedModel.shared.ViewKey;

public class ViewStateByPanel implements ViewState {

    private final int panelIndex;

    private final ViewStates parent;

    public ViewStateByPanel(int panelIndex, ViewStates parent) {
        this.panelIndex = panelIndex;
        this.parent = parent;
    }

    @Override
    public ViewKey getCurrentView() {
        return parent.getCurrentViewForPanel(panelIndex);
    }

    @Override
    public void previousAngle() {
        parent.previousAngle(getCurrentView());
    }

    @Override
    public void nextAngle() {
        parent.nextAngle(getCurrentView());
    }

    @Override
    public void setCurrentAngle(int newAngle) {
        parent.setCurrentAngle(getCurrentView(), newAngle);
    }

    @Override
    public int getCurrentAngle() {
        return parent.getCurrentAngle(getCurrentView());
    }


}

