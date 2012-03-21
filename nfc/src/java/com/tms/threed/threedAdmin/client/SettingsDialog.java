package com.tms.threed.threedAdmin.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import com.tms.threed.repoService.shared.Settings;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import smartsoft.util.gwt.client.dialogs.MyDialogBox;
import smartsoft.util.gwt.client.rpc.UiLog;

import java.util.List;

public class SettingsDialog extends MyDialogBox {


    FlexTable main = new FlexTable();
    FlowPanel fp = new FlowPanel();

    FlexTable t = new FlexTable();

    TextBox addJpgWidthTextBox = new TextBox();
    TextBox threadCountTextBox = new TextBox();
    FlexTable jpgWidthTable = new FlexTable();
    Button bAdd = new Button("Add");


    Button bSave = new Button("Save");


    private final UiLog uiLog;


    private final ThreedAdminClient threedAdminClient;

    private Settings settings;

    public SettingsDialog(final Settings settings, final ThreedAdminClient threedAdminClient, final UiLog uiLog) {
        super("Jpg Width");

        if (settings == null) {
            throw new NullPointerException();
        }

        if (threedAdminClient == null) {
            throw new NullPointerException();
        }

        if (uiLog == null) {
            throw new IllegalArgumentException();
        }

        this.settings = settings;
        this.threedAdminClient = threedAdminClient;
        this.uiLog = uiLog;

        getElement().getStyle().setZIndex(2000);
        FlowPanel topPanel = new FlowPanel();
        topPanel.add(addJpgWidthTextBox);
        topPanel.add(bAdd);

        addJpgWidthTextBox.setWidth("5em");
        jpgWidthTable.setWidth("100%");


        t.setHTML(0, 0, "<b>JPG Widths:</b>");
        t.setWidget(1, 0, topPanel);
        t.setWidget(2, 0, jpgWidthTable);

        t.setHTML(3, 0, "<div style='font-weight:bold;padding-top:2em'>Thread Count:</div>");
        t.setWidget(4, 0, threadCountTextBox);

        main.setWidget(0, 0, t);
        main.setWidget(1, 0, bSave);
        main.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
        main.getFlexCellFormatter().getElement(1, 0).getStyle().setPaddingTop(2, Style.Unit.EM);
        fp.add(main);

        fp.getElement().getStyle().setPadding(1, Style.Unit.EM);
        setWidget(fp);


        bAdd.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
//                    add();
            }
        });

        bSave.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                saveSettings();
                hide();
            }
        });

        addJpgWidthTextBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                try {
                    if (settings == null) {
                        uiLog.log("SettingsDialog Error: no model object [Settings] set");
                    } else {
                        JpgWidth jpgWidth = new JpgWidth(addJpgWidthTextBox.getText());
                        settings.addJpgWidth(jpgWidth);
                    }
                    addJpgWidthTextBox.setText(null);
                    refresh();
                } catch (Exception e) {
                    uiLog.log("Bad JPG Width[" + addJpgWidthTextBox.getText() + "]");
                }
            }
        });

        threadCountTextBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                try {
                    if (settings == null) {
                        uiLog.log("SettingsDialog Error: no model object [Settings] set");
                    } else {
                        Integer tc = new Integer(threadCountTextBox.getText());
                        settings.setJpgGenThreadCount(tc);
                    }
                } catch (NumberFormatException e) {
                    uiLog.log("Bad Thread Count[" + threadCountTextBox.getText() + "]");
                }
            }
        });

        refresh();


    }

    public Settings getSettings() {
        return settings;
    }

    public void setSettings(Settings settings) {
        this.settings = settings;
        refresh();
    }

    private void clearScreen() {
        jpgWidthTable.clear();
        jpgWidthTable.removeAllRows();
        threadCountTextBox.setText("");
    }

    private void populateScreen() {

        jpgWidthTable.clear();
        jpgWidthTable.removeAllRows();
        List<JpgWidth> jpgWidths = settings.getJpgWidths();

        int row = 0;
        for (final JpgWidth jpgWidth : jpgWidths) {
            if (jpgWidth.isStandard()) continue;
            jpgWidthTable.setText(row, 0, jpgWidth.intValue() + "px");
            Anchor deleteButton = new Anchor("Delete");
            jpgWidthTable.setWidget(row, 1, deleteButton);
            deleteButton.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    settings.removeJpgWith(jpgWidth);
                    refresh();
                }
            });

            jpgWidthTable.getCellFormatter().getElement(row, 0).getStyle().setPadding(.3, Style.Unit.EM);
            jpgWidthTable.getCellFormatter().getElement(row, 1).getStyle().setPadding(.3, Style.Unit.EM);


            row++;
        }


        threadCountTextBox.setText(settings.getJpgGenThreadCount() + "");
    }

    void refresh() {
        if (settings == null) {
            uiLog.log("SettingsDialog Error: no model object [Settings] set. Clearing ui");
            clearScreen();
        } else {
            populateScreen();
        }
    }


    private void saveSettings() {
        threedAdminClient.saveSettings(settings);
    }

}
