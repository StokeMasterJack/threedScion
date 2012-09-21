package c3i.smartClient.client.widgets.dragToSpin;

import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.futures.RValue;
import com.google.gwt.dom.client.Style;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.RectSize;

public class DragToSpin extends ClearGif {

    private DragToSpinHelper<DragToSpin> dragToSpin;

    public DragToSpin(DragToSpinModel viewPanelModel, RectSize initSize) {
        super(initSize);
        dragToSpin = new DragToSpinHelper<DragToSpin>();
        dragToSpin.setAngleScrollListener(viewPanelModel);
        dragToSpin.attachToTarget(this);
        addStyleName("DragToSpin");
        setPixelSize(initSize.getWidth(), initSize.getHeight());

        RValue<Boolean> dragToSpinEnabled = viewPanelModel.dragToSpin();
        Boolean enabled = dragToSpinEnabled.get();
        setEnabled(enabled);

        dragToSpinEnabled.addChangeListener(new ChangeListener<Boolean>() {
            @Override
            public void onChange(Boolean newValue) {
                setEnabled(newValue);
            }
        });

//        Style style = getElement().getStyle();
//        style.setLeft(0, Style.Unit.PX);
//        style.setTop(0, Style.Unit.PX);
//        style.setRight(0, Style.Unit.PX);
//        style.setBottom(0, Style.Unit.PX);


        setVisible(true);

    }

    public void setEnabled(boolean newValue) {
        dragToSpin.setEnabled(newValue);
    }

}
