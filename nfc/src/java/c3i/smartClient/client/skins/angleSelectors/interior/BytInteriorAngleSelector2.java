package c3i.smartClient.client.skins.angleSelectors.interior;

import c3i.smartClient.client.model.ViewModel;
import c3i.smartClient.client.widgets.AngleSelector;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import java.util.logging.Level;import java.util.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class BytInteriorAngleSelector2 extends AngleSelector {

    private static final int WIDTH_PX = 263;
    public static final int PREFERRED_HEIGHT_PX = 30;

    private final FlexTable table = new FlexTable();


    private final List<PushButton> buttons;


    public BytInteriorAngleSelector2(ViewModel model) {
        super(model);

        log.log(Level.INFO, "BytInteriorAngleSelector2.BytInteriorAngleSelector2");
        int angleCount = model.getView().getAngleCount();
        buttons = initButtons(angleCount);

        for (int angle = 1; angle <= angleCount; angle++) {
            PushButton button = buttons.get(angle - 1);
            table.setWidget(0, angle, button);
        }

        selectButton(1);

        initWidget(table);

        addStyleName("AngleSelector");
        addStyleName("Byt");
        addStyleName("Interior");

//        table.getElement().getStyle().setBackgroundColor("yellow");

        table.setWidth(WIDTH_PX + "px");

    }

    private List<PushButton> initButtons(int angleCount) {
        ArrayList<PushButton> a = new ArrayList<PushButton>();

        Resources r = Resources.INSTANCE;

        if (angleCount >= 1) a.add(new PushButton(1, r.angle1ButtonUp(), r.angle1ButtonDown()));
        if (angleCount >= 2) a.add(new PushButton(2, r.angle2ButtonUp(), r.angle2ButtonDown()));
        if (angleCount >= 3) a.add(new PushButton(3, r.angle3ButtonUp(), r.angle3ButtonDown()));
        if (angleCount >= 4) a.add(new PushButton(4, r.angle4ButtonUp(), r.angle4ButtonDown()));

        return a;
    }


    public void selectButton(int angle) {
        for (int i = 0; i < getButtonCount(); i++) {
            PushButton b = getButton(i);
            b.setSelected(angle == b.angleValue);
        }
    }

    private int getButtonCount() {
        return buttons.size();
    }

    private PushButton getButton(int index) {
        return buttons.get(index);
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

            getElement().getStyle().setMarginLeft(2, Style.Unit.PX);
            getElement().getStyle().setMarginRight(2, Style.Unit.PX);

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

    private static Logger log = Logger.getLogger(BytInteriorAngleSelector2.class.getName());
}
