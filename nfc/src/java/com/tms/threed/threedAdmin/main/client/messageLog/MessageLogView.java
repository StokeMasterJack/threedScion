package com.tms.threed.threedAdmin.main.client.messageLog;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;

import javax.annotation.Nonnull;

public class MessageLogView extends Composite {

    @Nonnull
    private final MessageLog messageLog;
    @Nonnull
    private final HTML html;

    private PopupPanel popupPanel;
    private ScrollPanel scrollPanel;

    public MessageLogView(@Nonnull MessageLog messageLog) {
        this.messageLog = messageLog;

        popupPanel = createPopupPanel();

        html = new HTML();
        html.getElement().getStyle().setColor("red");
        html.getElement().getStyle().setPaddingLeft(.5, Style.Unit.EM);
        html.getElement().getStyle().setPaddingRight(.5, Style.Unit.EM);

        scrollPanel = new ScrollPanel(html);
        scrollPanel.getElement().getStyle().setBackgroundColor("#EEEEEE");
        scrollPanel.setAlwaysShowScrollBars(false);

        initWidget(scrollPanel);


        messageLog.addValueChangeHandler(new ValueChangeHandler<MessageLog>() {
            @Override
            public void onValueChange(ValueChangeEvent<MessageLog> messageLogValueChangeEvent) {
                refresh();
            }
        });


        addDomHandler(new ContextMenuHandler() {
                    @Override
                    public void onContextMenu(ContextMenuEvent event) {
                        event.getNativeEvent().preventDefault();
                        final int x = event.getNativeEvent().getClientX();
                        final int y = event.getNativeEvent().getClientY();
                        popupPanel.setPopupPosition(x, y);
                        popupPanel.show();
                    }
                }, ContextMenuEvent.getType());
    }

    private PopupPanel createPopupPanel() {
        PopupPanel p = new PopupPanel(true);
        final HTML menu = new HTML("Clear Log");
        p.setWidget(menu);

        menu.getElement().getStyle().setCursor(Style.Cursor.POINTER);

        menu.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                popupPanel.hide();
                messageLog.clearLog();
            }
        });

        return p;
    }

    private void refresh() {
        SafeHtmlBuilder sb = new SafeHtmlBuilder();

        for (MessageLog.LogMessage logMessage : messageLog) {
            sb.appendHtmlConstant("<li>");
            sb.appendEscaped(logMessage.getText());
            sb.appendHtmlConstant("</li>");
        }

        html.setHTML(sb.toSafeHtml());

        scrollPanel.scrollToBottom();


    }
}
