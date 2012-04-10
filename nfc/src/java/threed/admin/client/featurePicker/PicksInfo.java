package threed.admin.client.featurePicker;

import com.google.gwt.event.shared.HandlerRegistration;
import threed.core.featureModel.shared.picks.PicksChangeHandler;

public interface PicksInfo {

    boolean isValid();

    HandlerRegistration addPicksChangeHandler(PicksChangeHandler handler);
}
