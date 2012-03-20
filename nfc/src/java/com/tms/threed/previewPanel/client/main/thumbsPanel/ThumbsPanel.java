package com.tms.threed.previewPanel.client.main.thumbsPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tms.threed.threedCore.threedModel.shared.SeriesInfo;
import com.tms.threed.threedCore.threedModel.shared.SeriesInfoBuilder;

import java.util.ArrayList;
import java.util.List;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

public class ThumbsPanel extends Composite {

    private final HandlerManager handlers = new HandlerManager(this);

    private static SeriesInfo dummySeriesInfo = SeriesInfoBuilder.createDummySeriesInfo();

    private int thumbCount;   //viewCount - 1

    private final ThumbPanel thumbPanel1 = new ThumbPanel(1);
    private final ThumbPanel thumbPanel2 = new ThumbPanel(2);

    private ClickHandler clickHandler = new ClickHandler() {
        @Override public void onClick(ClickEvent event) {
            ThumbPanel thumbPanel = (ThumbPanel) event.getSource();
            onThumbClick(thumbPanel.getPanelIndex());
        }
    };


    private final FlowPanel flowPanel = new FlowPanel();

    private static final int PREFERRED_WIDTH_PX = ThumbPanel.PREFERRED_WIDTH_PX * 2 + 30;
    public static final int PREFERRED_HEIGHT_PX = ThumbPanel.PREFERRED_HEIGHT_PX;


    public ThumbsPanel() {

        initWidget(flowPanel);
        setPixelSize(PREFERRED_WIDTH_PX, PREFERRED_HEIGHT_PX);
        getElement().getStyle().setZIndex(1000);


        thumbPanel1.getElement().getStyle().setFloat(Style.Float.RIGHT);
        thumbPanel1.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        thumbPanel1.getElement().getStyle().setMarginTop(6, Style.Unit.PX);


        thumbPanel2.getElement().getStyle().setFloat(Style.Float.RIGHT);
        thumbPanel2.getElement().getStyle().setMarginRight(10, Style.Unit.PX);
        thumbPanel2.getElement().getStyle().setMarginTop(6, Style.Unit.PX);


        flowPanel.add(thumbPanel1);
        thumbPanel1.addClickHandler(clickHandler);

        flowPanel.add(thumbPanel2);
        thumbPanel2.addClickHandler(clickHandler);

        setThumbCount(2);

    }

    public HandlerRegistration addThumbClickHandler(ThumbClickHandler handler) {
        return handlers.addHandler(ThumbClickEvent.TYPE, handler);
    }

    private void onThumbClick(int thumbIndex) {
        if (thumbIndex > thumbCount) return;
        handlers.fireEvent(new ThumbClickEvent(thumbIndex));
    }

    public void setThumbCount(int thumbCount) {
        if (this.thumbCount == thumbCount) return;
        this.thumbCount = thumbCount;

        if (thumbCount == 1) {
            thumbPanel1.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            thumbPanel2.getElement().getStyle().setDisplay(Style.Display.NONE);
        } else if (thumbCount == 2) {
            thumbPanel1.getElement().getStyle().setDisplay(Style.Display.BLOCK);
            thumbPanel2.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        } else {
            throw new IllegalStateException();
        }


    }


    public List<ThumbPanel> getThumbPanels() {
        ArrayList<ThumbPanel> a = new ArrayList<ThumbPanel>();
        if (thumbCount == 1) {
            a.add(thumbPanel1);
        } else if (thumbCount == 2) {
            a.add(thumbPanel1);
            a.add(thumbPanel2);
        } else {
            throw new IllegalStateException();
        }
        return a;
    }

}
