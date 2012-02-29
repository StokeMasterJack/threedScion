package com.tms.threed.threedFramework.previewPanel.client.main.thumbsPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DomEvent;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.tms.threed.threedFramework.previewPanel.client.PreviewPanel;
import com.tms.threed.threedFramework.previewPanel.client.ThreedImagePanel;
import com.tms.threed.threedFramework.threedModel.shared.ViewKey;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.getSimpleName;

public class ThumbPanel extends FlowPanel implements ThumbDisplay, HasClickHandlers {

    private final PreviewPanel threedImagePanel;
    private final Label captionLabel;
    private final int panelIndex;

//    private static final ImageSize THUMB_IMAGE_SIZE = new ImageSize(66,108);
    private static final ImageSize THUMB_IMAGE_SIZE = new ImageSize(108,66);



    private static final int LABEL_HEIGHT_PX = 16;
    private static final int HORIZONTAL_PADDING_PX = 0;
    private static final int TOP_PADDING_PX = 6;
    private static final int BOTTOM_PADDING_PX = 2;

    public static final int PREFERRED_WIDTH_PX = THUMB_IMAGE_SIZE.getWidth() + HORIZONTAL_PADDING_PX * 2;
    public static final int PREFERRED_HEIGHT_PX = THUMB_IMAGE_SIZE.getHeight() + LABEL_HEIGHT_PX + TOP_PADDING_PX + BOTTOM_PADDING_PX;


    public ThumbPanel(int thumbIndex) {
        setPixelSize(PREFERRED_WIDTH_PX, PREFERRED_HEIGHT_PX);
        this.panelIndex = thumbIndex;
        this.setVisible(false);

//        getElement().getStyle().setBackgroundColor("green");
        getElement().getStyle().setZIndex(2000);

        threedImagePanel = new PreviewPanel(panelIndex, THUMB_IMAGE_SIZE,false);

        captionLabel = new Label("Thumb " + thumbIndex);
        captionLabel.setHeight(LABEL_HEIGHT_PX + "px");
        captionLabel.getElement().getStyle().setFontSize(12, Style.Unit.PX);
        captionLabel.getElement().getStyle().setProperty("textAlign", "center");
//        captionLabel.getElement().getStyle().setBackgroundColor("purple");
        captionLabel.getElement().getStyle().setPaddingTop(2, Style.Unit.PX);

        add(threedImagePanel);
//        setWidgetTopHeight(threedImagePanel, TOP_PADDING_PX, Style.Unit.PX, IMAGE_HEIGHT_PX, Style.Unit.PX);
//        setWidgetLeftRight(threedImagePanel, HORIZONTAL_PADDING_PX, Style.Unit.PX, HORIZONTAL_PADDING_PX, Style.Unit.PX);

        add(captionLabel);
//        setWidgetBottomHeight(captionLabel, BOTTOM_PADDING_PX, Style.Unit.PX, LABEL_HEIGHT_PX, Style.Unit.PX);
//        setWidgetLeftRight(captionLabel, HORIZONTAL_PADDING_PX, Style.Unit.PX, HORIZONTAL_PADDING_PX, Style.Unit.PX);

        setVisible(true);

//        image.addLoadHandler(new LoadHandler() {
//            @Override public void onLoad(LoadEvent event) {
//                setVisible(true);
//            }
//        });

       


    }

    public PreviewPanel getThreedImagePanel() {
        return threedImagePanel;
    }

    public void setViewKey(ViewKey viewKey) {
        captionLabel.setText(viewKey.getLabel());
    }

    @Override public HandlerRegistration addClickHandler(ClickHandler handler) {
        DomEvent.Type<ClickHandler> clickHandlerType = ClickEvent.getType();
        return this.addDomHandler(handler, clickHandlerType);
    }


    public int getPanelIndex() {
        return panelIndex;
    }
}
