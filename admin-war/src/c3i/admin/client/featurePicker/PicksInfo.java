package c3i.admin.client.featurePicker;

import com.google.gwt.event.shared.HandlerRegistration;
import c3i.core.featureModel.shared.picks.PicksChangeHandler;

public interface PicksInfo {

    boolean isValid();

    HandlerRegistration addPicksChangeHandler(PicksChangeHandler handler);
}
