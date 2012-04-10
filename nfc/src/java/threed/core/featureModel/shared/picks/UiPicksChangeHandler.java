package threed.core.featureModel.shared.picks;

import com.google.gwt.event.shared.EventHandler;

public interface UiPicksChangeHandler extends EventHandler {
    void onPicksChange(UiPicksChangeEvent e);
}