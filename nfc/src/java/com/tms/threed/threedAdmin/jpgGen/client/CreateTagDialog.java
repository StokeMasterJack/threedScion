package com.tms.threed.threedAdmin.jpgGen.client;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.tms.threed.threedAdmin.main.client.MyDialogBox;


public class CreateTagDialog extends MyDialogBox {

    private final TextBox textBox = new TextBox();
    private boolean canceled;
    private String tagName;

    public CreateTagDialog() {
        super("Tag this version");

        FlowPanel fp = new FlowPanel();


        class TLabel extends Label {
            TLabel(String text) {
                super(text);
                getElement().getStyle().setPadding(1, Style.Unit.EM);
                getElement().getStyle().setFontWeight(Style.FontWeight.BOLD);
            }
        }

        FlexTable t = new FlexTable();

        t.setWidget(0, 0, new TLabel("Enter tag name:"));
        t.setWidget(0, 1, textBox);


        FlowPanel buttonPanel = new FlowPanel();
//        buttonPanel.getElement().getStyle().setBackgroundColor("#CCCCCC");
        Button okButton = new Button("OK");
        Button cancelButton = new Button("Cancel");
        buttonPanel.add(cancelButton);
        buttonPanel.add(okButton);

//        startButton.getElement().getStyle().setMarginRight(1, Style.Unit.EM);
        cancelButton.getElement().getStyle().setMarginTop(2, Style.Unit.EM);
        cancelButton.getElement().getStyle().setMarginRight(1, Style.Unit.EM);

        t.setWidget(4, 0, buttonPanel);
        t.getFlexCellFormatter().setColSpan(4, 0, 2);
        t.getFlexCellFormatter().setHorizontalAlignment(4, 0, HasHorizontalAlignment.ALIGN_CENTER);

        fp.add(t);

        textBox.addChangeHandler(new ChangeHandler() {
            @Override public void onChange(ChangeEvent event) {
                tagName = textBox.getText();
                CreateTagDialog.this.hide();
            }
        });

        okButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                tagName = textBox.getText();
                CreateTagDialog.this.hide();

            }
        });

        cancelButton.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                canceled = true;
                CreateTagDialog.this.hide();
            }
        });


        fp.getElement().getStyle().setPadding(1, Style.Unit.EM);

        setWidget(fp);

        addAttachHandler(new AttachEvent.Handler() {
            @Override public void onAttachOrDetach(AttachEvent event) {
                 Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override public void execute() {
                        textBox.setFocus(true);
                    }
                });
            }
        });



    }

    public boolean isCanceled() {
        return canceled;
    }

    public String getTagName() {
        return tagName;
    }
}
