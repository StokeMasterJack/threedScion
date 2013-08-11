package c3i.smartClient.client.skins.bytSkin.chatPanel;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasMouseOutHandlers;
import com.google.gwt.event.dom.client.HasMouseOverHandlers;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
//import c3i.skin.common.client.PreviewPanelStyles;
import smartsoft.util.shared.Path;

import static com.google.gwt.user.client.ui.HasHorizontalAlignment.ALIGN_CENTER;
import static com.google.gwt.user.client.ui.HasHorizontalAlignment.HorizontalAlignmentConstant;
import static com.google.gwt.user.client.ui.HasVerticalAlignment.ALIGN_MIDDLE;
import static com.google.gwt.user.client.ui.HasVerticalAlignment.VerticalAlignmentConstant;

/**
 * Chat Graphic: 84w x 43h
 * Car Graphic:  84w???43h
 */
public class ChatPanel extends FlowPanel implements HasMouseOverHandlers, HasMouseOutHandlers, HasClickHandlers {

    public static final Resources RESOURCES = Resources.INSTANCE;

    private static final String FONT_COLOR_MOUSE_OVER = "rgb(240,220,1)";
    private static final String FONT_COLOR_MOUSE_OUT = "white";

    private ChatTextBox chatTextBox;
    private ChatGraphic chatGraphic;

    private final String displayName;

    private ChatInfo chatInfo;

    public ChatPanel(String displayName) {
        this.displayName = displayName;

        setVisible(false);
        Style style = getElement().getStyle();
        style.setZIndex(100000);
//        style.setMargin(5, Style.Unit.PX);
        style.setColor("white");


        chatTextBox = new ChatTextBox();
        chatGraphic = new ChatGraphic();

        add(chatTextBox);
        add(chatGraphic);
        chatGraphic.getElement().getStyle().setMarginRight(7, Style.Unit.PX);

        addClickHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                Path actionUrl = chatInfo.getClickActionUrl();
                Window.open(actionUrl.toString(), "chat", null);
            }

        });

//        PreviewPanelStyles.set(this);

        refreshChatInfo();
        refreshSeries();

    }

    public void setChatInfo(ChatInfo chatInfo) {
        this.chatInfo = chatInfo;
        refreshChatInfo();
    }

    private void refreshSeries() {
        chatTextBox.refreshSeries();
    }


    private void refreshChatInfo() {
        if (chatInfo == null) {
            setVisible(false);
        } else {
            chatGraphic.refreshChatInfo();
            setVisible(true);
        }
    }

    @Override public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
        return this.addDomHandler(handler, MouseOverEvent.getType());
    }

    @Override public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
        return this.addDomHandler(handler, MouseOutEvent.getType());
    }

    @Override public HandlerRegistration addClickHandler(ClickHandler handler) {
        return this.addDomHandler(handler, ClickEvent.getType());
    }

    class ChatGraphic extends Composite {

        static final int imageWidthPx = 84;
        static final int imageHeightPx = 43;

        private final Image offImage;
        private final Image onImage;
        private final Image vehicleIcon;

        private ChatGraphic() {

            offImage = initOffImage();
            onImage = initOverImage();

            vehicleIcon = initCarImage();

            AbsolutePanel absolutePanel = initAbsolutePanel();
//            absolutePanel.setPixelSize(imageWidthPx, imageHeightPx);

            Grid gAbsolutePanel = wrapWithVerticallyCenteringGrid(absolutePanel, ALIGN_CENTER, ALIGN_MIDDLE);

            gAbsolutePanel.setBorderWidth(0);
            gAbsolutePanel.setCellPadding(0);
            gAbsolutePanel.setCellSpacing(0);
            gAbsolutePanel.setHeight("100%");

            initWidget(gAbsolutePanel);


            Style style = getElement().getStyle();
            style.setFloat(Style.Float.LEFT);


            out();

            ChatPanel.this.addMouseOverHandler(new MouseOverHandler() {
                @Override public void onMouseOver(MouseOverEvent event) {
                    over();
                }
            });

            ChatPanel.this.addMouseOutHandler(new MouseOutHandler() {
                @Override public void onMouseOut(MouseOutEvent event) {
                    out();
                }
            });

            offImage.setVisible(true);
        }

        private AbsolutePanel initAbsolutePanel() {
            AbsolutePanel absolutePanel = new AbsolutePanel();

            absolutePanel.setPixelSize(imageWidthPx, imageHeightPx);

            absolutePanel.add(offImage, 0, 0);
            absolutePanel.add(onImage, 0, 0);
            absolutePanel.add(vehicleIcon, 0, 0);
            return absolutePanel;
        }

        private Image initOffImage() {
            Image i = new Image(RESOURCES.chatIconOut());
            i.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            i.getElement().getStyle().setZIndex(33010);
            i.setVisible(false);
            return i;
        }

        private Image initOverImage() {
            Image i = new Image(RESOURCES.chatIconOver());
            i.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            i.getElement().getStyle().setZIndex(33020);
            i.setVisible(false);
            return i;
        }

        private Image initCarImage() {
            Image i = new Image();
            i.getElement().getStyle().setCursor(Style.Cursor.POINTER);
            i.getElement().getStyle().setZIndex(33030);
            return i;
        }

        public void refreshChatInfo() {
            Path path = chatInfo.getVehicleIconUrl();
            String url = path.toString();
            vehicleIcon.setUrl(url);
        }

        void over() {
            onImage.setVisible(true);
        }

        void out() {
            onImage.setVisible(false);
        }


    }

    private class ChatTextBox extends Grid {

        private final int leftPaddingPx = 3;
        private final int rightPaddingPx = 7;

        private final HTML htmlText;

        private ChatTextBox() {
            super(1, 1);
            htmlText = createHtmlText();

            setWidget(0, 0, htmlText);
            Style style = getElement().getStyle();
            style.setFloat(Style.Float.LEFT);
            getElement().getStyle().setZIndex(9000);


            ChatPanel.this.addMouseOverHandler(new MouseOverHandler() {
                @Override public void onMouseOver(MouseOverEvent event) {
                    over();
                }
            });

            ChatPanel.this.addMouseOutHandler(new MouseOutHandler() {
                @Override public void onMouseOut(MouseOutEvent event) {
                    out();
                }
            });

            setHeight("100%");
            getCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_MIDDLE);

        }

        private HTML createHtmlText() {
            HTML h = new HTML();
            Style hStyle = h.getElement().getStyle();
            hStyle.setFontSize(7, Style.Unit.PT); //7
            hStyle.setColor("#fff");
            hStyle.setCursor(Style.Cursor.POINTER);
            hStyle.setPaddingLeft(leftPaddingPx, Style.Unit.PX);
            hStyle.setPaddingRight(rightPaddingPx, Style.Unit.PX);
            hStyle.setProperty("textAlign", "left");
            hStyle.setZIndex(10000);
            return h;
        }


        public void over() {
            htmlText.getElement().getStyle().setColor(FONT_COLOR_MOUSE_OVER);
        }

        public void out() {
            htmlText.getElement().getStyle().setColor(FONT_COLOR_MOUSE_OUT);
        }

        public void refreshSeries() {
            String seriesName = displayName.toUpperCase();
            String html = "Want more info?<br/>Chat with a " + seriesName + "<br/>specialist.";
            htmlText.setHTML(html);
        }
    }

    public static Grid wrapWithVerticallyCenteringGrid(Widget widget, HorizontalAlignmentConstant hAlign, VerticalAlignmentConstant vAlign) {
        Grid g = new Grid(1, 1);
        g.setWidget(0, 0, widget);
        g.setCellPadding(0);
        g.setCellSpacing(0);
        g.getCellFormatter().setVerticalAlignment(0, 0, vAlign);
        g.getCellFormatter().setHorizontalAlignment(0, 0, hAlign);

        return g;
    }


}
