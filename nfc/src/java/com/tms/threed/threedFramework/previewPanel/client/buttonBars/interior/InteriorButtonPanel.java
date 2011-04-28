package com.tms.threed.threedFramework.previewPanel.client.buttonBars.interior;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.AngleButtonPanel;
import com.tms.threed.threedFramework.threedCore.shared.ViewKey;

public class InteriorButtonPanel extends AngleButtonPanel implements InteriorButtonPanelDisplay {

    private final FlowPanel flowPanel = new FlowPanel();


    private static final int PREFERRED_HEIGHT_PX = 19;
    private static final int WIDTH_PX = 263;

    private final PushButton sideButton;
    private final PushButton dashButton;
    private final PushButton topButton;


    private InteriorButtonHandler buttonHandler;

    public InteriorButtonPanel() {

        Resources r = Resources.INSTANCE;
        initWidget(flowPanel);

        setPixelSize(WIDTH_PX, PREFERRED_HEIGHT_PX);
        Style style = getElement().getStyle();
        style.setProperty("marginLeft", "auto");
        style.setProperty("marginRight", "auto");


        sideButton = new PushButton(ViewKey.SIDE_ANGLE, r.sideViewButtonUp(), r.sideViewButtonDown());
        dashButton = new PushButton(ViewKey.DASH_ANGLE, r.dashViewButtonUp(), r.dashViewButtonDown());
        topButton = new PushButton(ViewKey.TOP_ANGLE, r.topViewButtonUp(), r.topViewButtonDown());

        addButton(sideButton);
        addButton(dashButton);
        addButton(topButton);

        selectButton(ViewKey.SIDE_ANGLE);

        setVisible(false);

    }

    @Override public void setButtonHandler(InteriorButtonHandler buttonHandler) {
        this.buttonHandler = buttonHandler;
    }

    @Override public int getPreferredHeightPx() {
        return PREFERRED_HEIGHT_PX;
    }

    private void addButton(PushButton button) {
        flowPanel.add(button);
        int ml = getButtonCount() == 1 ? 0 : 1;
        button.getElement().getStyle().setMarginLeft(ml, Style.Unit.PX);
    }

    public void selectButton(int angle) {
        for (int i = 0; i < getButtonCount(); i++) {
            PushButton b = getButton(i);
            b.setSelected(angle == b.angleValue);
        }
    }

    private int getButtonCount() {return flowPanel.getWidgetCount();}

    private PushButton getButton(int index) {
        return (PushButton) flowPanel.getWidget(index);
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

            getElement().setAttribute("valign", "bottom");
            getElement().getStyle().setBorderWidth(0, Style.Unit.PX);
            getElement().getStyle().setPadding(0, Style.Unit.EM);
            getElement().getStyle().setFloat(Style.Float.LEFT);

            addClickHandler(new ClickHandler() {
                public void onClick(ClickEvent event) {
                    if(buttonHandler == null) return;
                    selectButton(angleValue);
                    buttonHandler.onSelection(angleValue);
                }
            });

        }

        void setSelected(boolean selected) {
            if (selected) setResource(this.downImageResource);
            else setResource(this.upImageResource);
        }


    }


}
