package com.tms.threed.threedFramework.previewPanel.client.buttonBars.exterior;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.tms.threed.threedFramework.previewPanel.client.PreviewPanelStyles;
import com.tms.threed.threedFramework.previewPanel.client.buttonBars.AngleButtonPanel;
import com.tms.threed.threedFramework.util.lang.shared.ImageSize;

public class ExteriorButtonPanel extends AngleButtonPanel implements ExteriorButtonPanelDisplay{

    private static final int WIDTH_PX = 263;
    public static final int PREFERRED_HEIGHT_PX = 30;

    private FlexTable flexTable = new FlexTable();

    private ExteriorButtonHandler buttonHandler;

    public ExteriorButtonPanel() {
        initWidget(flexTable);

        HTMLTable.CellFormatter cellFormatter = flexTable.getCellFormatter();

        cellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        cellFormatter.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_CENTER);
        cellFormatter.setHorizontalAlignment(0, 2, HasHorizontalAlignment.ALIGN_CENTER);

        cellFormatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);
        cellFormatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);
        cellFormatter.setVerticalAlignment(0, 2, HasVerticalAlignment.ALIGN_MIDDLE);

//        setPixelSize(WIDTH_PX, PREFERRED_HEIGHT_PX);

        setHeight(PREFERRED_HEIGHT_PX + "px");
//        setWidth("100%");

        Style style = flexTable.getElement().getStyle();
        style.setProperty("marginLeft", "auto");
        style.setProperty("marginRight", "auto");

        flexTable.setCellPadding(0);
        flexTable.setCellPadding(0);

        flexTable.setWidget(0, 0, createPrevButton());
        flexTable.setWidget(0, 1, createComboCenterPanel());
        flexTable.setWidget(0, 2, createNextButton());

        PreviewPanelStyles.set(this);

        setVisible(false);

    }


    @Override public void setButtonHandler(ExteriorButtonHandler buttonHandler) {
        this.buttonHandler = buttonHandler;
    }

    @Override public int getPreferredHeightPx() {
        return PREFERRED_HEIGHT_PX;
    }


    private Widget createPrevButton() {
        return createButton(Resources.INSTANCE.prevButton(), new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                if(buttonHandler == null) {
                    return;
                }
                buttonHandler.onPrevious();
            }
        });
    }

    private Widget createNextButton() {
        return createButton(Resources.INSTANCE.nextButton(), new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                if(buttonHandler == null) return;
                buttonHandler.onNext();
            }
        });
    }

    private Widget createButton(ImageResource imageResource, ClickHandler clickHandler) {
        Image img = new Image(imageResource);
        img.getElement().getStyle().setDisplay(Style.Display.BLOCK);
        img.getElement().getStyle().setFloat(Style.Float.LEFT);
        img.addClickHandler(clickHandler);
        img.getElement().getStyle().setZIndex(50000);
        return img;
    }

    private int HEIGHT_SPIN_TRANSPARENCY_PX = 20;
    private int HEIGHT_SPIN_LABEL_PX = 12;


    private AbsolutePanel createComboCenterPanel() {
        AbsolutePanel ap = new AbsolutePanel();
        ap.add(new SpinTransparency(), 0, 0);
        ap.add(new SpinLabel(), 0, 0);
        ap.setPixelSize(150, PREFERRED_HEIGHT_PX);
        return ap;
    }

    public void setMainImageSize(ImageSize imageSize) {

    }

    class SpinTransparency extends Grid {
        SpinTransparency() {
            super(1, 1);
            setWidth("100%");
            setHeight(PREFERRED_HEIGHT_PX + "px");
            setCellSpacing(0);
            setCellPadding(0);

            Style style = getElement().getStyle();
            style.setMargin(0, Style.Unit.PX);

            CellFormatter cellFormatter = getCellFormatter();
            Style cellStyle = cellFormatter.getElement(0, 0).getStyle();
            cellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
            cellFormatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);

            cellStyle.setPadding(0, Style.Unit.PX);

//            cellStyle.setBackgroundColor("yellow");


            setWidget(0, 0, createTransparency());
        }

        private Widget createTransparency() {
            final SimplePanel h = new SimplePanel();

            final Style s = h.getElement().getStyle();
            s.setBackgroundColor("#000");
            s.setOpacity(.2);
            s.setZIndex(20);
            s.setProperty("filter", "alpha(opacity=20)");/* For IE6&7 */

            h.setWidth("130px");
//            h.getElement().getStyle().setWidth(93, Style.Unit.PCT);
            h.getElement().getStyle().setHeight(HEIGHT_SPIN_TRANSPARENCY_PX, Style.Unit.PX);

            return h;
        }

    }

    class SpinLabel extends Grid {
        SpinLabel() {
            super(1, 1);
            setWidth("100%");
            setHeight(PREFERRED_HEIGHT_PX + "px");
            setText(0, 0, "Click and drag to spin");
            setCellSpacing(0);
            setCellPadding(0);

            Style style = getElement().getStyle();
            style.setMargin(0, Style.Unit.PX);

            CellFormatter cellFormatter = getCellFormatter();
            Element cellElement = cellFormatter.getElement(0, 0);
            Style cellStyle = cellElement.getStyle();
            cellFormatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
            cellFormatter.setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);

            PreviewPanelStyles.set(this);
            PreviewPanelStyles.set(cellElement);

            cellStyle.setFontSize(HEIGHT_SPIN_LABEL_PX, Style.Unit.PX);
            cellStyle.setColor("white");
            cellStyle.setPadding(0, Style.Unit.PX);
//            cellStyle.setBackgroundColor("yellow");
        }
    }

}