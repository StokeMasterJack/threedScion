package com.tms.threed.threedAdmin.main.client.tabLabel;

import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;

public class TabLabel extends FlexTable {


    private final ImageResource URL = Resources.INSTANCE.closeButton();
    private final ImageResource URL_MOUSE_OVER = Resources.INSTANCE.closeButtonMouseOver();

//    private static final String URL = "closeButton.png";
//    private static final String URL_MOUSE_OVER = "closeButton-mouseOver.png";

    private final Image closeButton = createTabCloseButton();

    private String label;
    private final boolean buttonOnRight;

    public TabLabel(String label) {
        this(label, false);
    }

    public TabLabel(String label, boolean buttonOnRight) {
        this.label = label;
        this.buttonOnRight = buttonOnRight;
        if (buttonOnRight) {
            setWidget(0, 1, closeButton);
        } else {
            setWidget(0, 0, closeButton);
        }
        refresh();
    }

    public void setLabel(String label) {
        this.label = label;
        refresh();
    }

    public HandlerRegistration addCloseButtonHandler(ClickHandler clickHandler) {
        return closeButton.addClickHandler(clickHandler);
    }

    public void refresh() {
        HTML html = new HTML("<nobr>&nbsp;<b>" + label + "</b>&nbsp;</nobr>");
        if (buttonOnRight) {
            setWidget(0, 0, html);
        } else {
            setWidget(0, 1, html);
        }
        getCellFormatter().getElement(0, 1).setAttribute("nowrap", "nowrap");


        getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        getCellFormatter().setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);

    }

    public Image createTabCloseButton() {

        final Image image = new Image(URL);
        image.addMouseOverHandler(new MouseOverHandler() {
            @Override public void onMouseOver(MouseOverEvent event) {
                image.setResource(URL_MOUSE_OVER);
            }
        });

        image.addMouseOutHandler(new MouseOutHandler() {
            @Override public void onMouseOut(MouseOutEvent event) {
                image.setResource(URL);
            }
        });

        return image;

    }

}


