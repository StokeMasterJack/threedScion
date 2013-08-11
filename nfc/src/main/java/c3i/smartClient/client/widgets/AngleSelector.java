package c3i.smartClient.client.widgets;

import c3i.smartClient.client.widgets.dragToSpin.DragToSpinModel;
import com.google.common.base.Preconditions;
import com.google.gwt.user.client.ui.Composite;

public abstract class AngleSelector extends Composite {

    protected final DragToSpinModel model;

    protected AngleSelector(DragToSpinModel model) {
        Preconditions.checkNotNull(model);
        this.model = model;
    }


}
