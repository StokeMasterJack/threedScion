package com.tms.threed.threedFramework.previewPanel.client.headerPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.layout.client.Layout;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.tms.threed.threedFramework.previewPanel.client.PreviewPanelStyles;
import com.tms.threed.threedFramework.previewPanel.client.chatPanel.ChatPanel;

public class HeaderPanel extends LayoutPanel {

    private static final int TOTAL_HEIGHT_PX = 57;

    private static final String FONT_COLOR = "white";
    private static final int FONT_SIZE_PX = 20;


    private final YearSeriesLabel yearSeriesLabel;

    private final ChatPanel chatPanel;
    private final int year;
    private final String displayName;

    public HeaderPanel(ChatPanel chatPanel, int year, String displayName) {
        assert chatPanel != null;
        assert displayName != null;
        assert year != 0;

        this.chatPanel = chatPanel;
        this.year = year;
        this.displayName = displayName;

        yearSeriesLabel = new YearSeriesLabel();

        HeaderTextBox textPanel = new HeaderTextBox();
        Widget background = createTransparentBackground();

        setSize("100%", TOTAL_HEIGHT_PX + "px");


        add(textPanel);
        setWidgetHorizontalPosition(textPanel, Layout.Alignment.BEGIN);
        setWidgetTopBottom(textPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);

        add(chatPanel);
        setWidgetHorizontalPosition(chatPanel, Layout.Alignment.END);
        setWidgetTopBottom(chatPanel, 5, Style.Unit.PX, 5, Style.Unit.PX);

        add(background);
        PreviewPanelStyles.set(this);

        chatPanel.getElement().getStyle().setZIndex(100000);


    }

    private class HeaderTextBox extends FlowPanel {

        private HeaderTextBox() {
            add(yearSeriesLabel);

            Style style = getElement().getStyle();
            style.setPadding(0, Style.Unit.PX);
            style.setMargin(0, Style.Unit.PX);
            style.setZIndex(200);
            style.setColor(FONT_COLOR);
            style.setFontSize(FONT_SIZE_PX, Style.Unit.PX);
            style.setFontWeight(Style.FontWeight.BOLD);

            PreviewPanelStyles.set(this);

//            style.setBackgroundColor("yellow");

            style.setMarginLeft(5, Style.Unit.PX);

        }

    }

    private class YearSeriesLabel extends Label {

        private YearSeriesLabel() {
            this.setVisible(false);

            Style style = this.getElement().getStyle();
            style.setProperty("textTransform", "uppercase");

            style.setMargin(0, Style.Unit.PX);
            style.setPadding(0, Style.Unit.PX);
            style.setFontSize(FONT_SIZE_PX, Style.Unit.PX);

            assert year != 0;
            assert displayName != null;

            String s = "your " + year + " " + displayName;
            this.setText(s);
            this.setVisible(true);
        }
    }


    private Widget createTransparentBackground() {
        HTML p = new HTML();
        p.setWidth("100%");
//        p.setHeight(TOTAL_HEIGHT_PX + "px");

        Style s = p.getElement().getStyle();
        s.setBackgroundColor("#000");
        s.setOpacity(.2);
        s.setProperty("filter", "alpha(opacity=20)");/* For IE6&7 */
//        s.setZIndex(100);
        return p;
    }


}