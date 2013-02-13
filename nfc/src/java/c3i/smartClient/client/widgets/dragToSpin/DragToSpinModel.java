package c3i.smartClient.client.widgets.dragToSpin;

import c3i.util.shared.futures.RValue;
import org.timepedia.exporter.client.Exportable;

public interface DragToSpinModel extends Exportable {

    void setAngle(int angle);

    void angleNext();

    void anglePrevious();

    RValue<Boolean> dragToSpin();

}
