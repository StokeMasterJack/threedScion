package c3i.smartClient.client.skins.angleSelectors.interior;

import c3i.imageModel.shared.ViewKeyOld;
import c3i.smartClient.client.widgets.AngleSelector;
import c3i.smartClient.client.widgets.dragToSpin.DragToSpinModel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class BytInteriorAngleSelector extends AngleSelector {

    private static final int WIDTH_PX = 263;
    public static final int PREFERRED_HEIGHT_PX = 30;

    private final FlexTable table = new FlexTable();

    private final PushButton sideButton;
    private final PushButton dashButton;
    private final PushButton topButton;

    private final PushButton[] buttons = new PushButton[3];


    public BytInteriorAngleSelector(DragToSpinModel model) {
        super(model);
        Resources r = Resources.INSTANCE;

        sideButton = new PushButton(ViewKeyOld.SIDE_ANGLE, r.angle1ButtonUp(), r.angle1ButtonDown());
        dashButton = new PushButton(ViewKeyOld.DASH_ANGLE, r.angle2ButtonUp(), r.angle2ButtonDown());
        topButton = new PushButton(ViewKeyOld.TOP_ANGLE, r.angle3ButtonUp(), r.angle3ButtonDown());

        buttons[0] = sideButton;
        buttons[1] = dashButton;
        buttons[2] = topButton;

        table.setWidget(0, 0, buttons[0]);
        table.setWidget(0, 1, buttons[1]);
        table.setWidget(0, 2, buttons[2]);

        selectButton(1);

        initWidget(table);

        addStyleName("AngleSelector");
        addStyleName("Byt");
        addStyleName("Interior");

//        table.getElement().getStyle().setBackgroundColor("yellow");

    }


    public void selectButton(int angle) {
        for (int i = 0; i < getButtonCount(); i++) {
            PushButton b = getButton(i);
            b.setSelected(angle == b.angleValue);
        }
    }

    private int getButtonCount() {
        return 3;
    }

    private PushButton getButton(int index) {
        return buttons[index];
    }

    private class PushButton extends Image {

        private final int angleValue;
        private final ImageResource upImageResource;
        private final ImageResource downImageResource;

        private PushButton(final int angleValue, ImageResource upImageResource, ImageResource downImageResource) {
            super(upImageResource);
            this.angleValue = angleValue;
            this.upImageResource = upImageResource;
            this.downImageResource = downImageResource;

            addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    selectButton(angleValue);
                    model.setAngle(angleValue);
                }
            });

        }

        void setSelected(boolean selected) {
            if (selected) setResource(this.downImageResource);
            else setResource(this.upImageResource);
        }


    }

    @Override
    public Widget asWidget() {
        return table;
    }

//    @Override
//    public RectSize getPreferredSize() {
//        return new RectSize(WIDTH_PX, PREFERRED_HEIGHT_PX);
//    }
}
