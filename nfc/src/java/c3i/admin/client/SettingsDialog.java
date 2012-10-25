package c3i.admin.client;

import c3i.core.common.shared.BrandKey;
import c3i.core.imageModel.shared.Profile;
import c3i.repo.shared.Settings;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TextBox;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.dialogs.MyDialogBox;

import java.util.List;

public class SettingsDialog extends MyDialogBox {

    private final FlexTable main = new FlexTable();
    private final FlowPanel fp = new FlowPanel();

    private final FlexTable t = new FlexTable();

    private final TextBox addJpgWidthTextBox = new TextBox();
    private final TextBox threadCountTextBox = new TextBox();
    private final FlexTable jpgWidthTable = new FlexTable();
    private final Button bAdd = new Button("Add");
    private final Button bSave = new Button("Save");

    private final ThreedAdminClient threedAdminClient;

    private BrandKey brandKey;
    private Settings settings;

    public SettingsDialog(BrandKey brandKey, final Settings settings, final ThreedAdminClient threedAdminClient) {
        super("Jpg Width");

        if (brandKey == null) {
            throw new NullPointerException();
        }

        if (settings == null) {
            throw new NullPointerException();
        }

        if (threedAdminClient == null) {
            throw new NullPointerException();
        }


        this.settings = settings;
        this.threedAdminClient = threedAdminClient;

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
                saveSettings(SettingsDialog.this.brandKey);
                hide();
            }
        });

//        addJpgWidthTextBox.addChangeHandler(new ChangeHandler() {
//            @Override
//            public void onChange(ChangeEvent event) {
//                try {
//                    if (settings == null) {
//                        uiLog.log("SettingsDialog Error: no model object [Settings] set");
//                    } else {
//                        JpgWidth jpgWidth = new JpgWidth(addJpgWidthTextBox.getText());
//                        settings.addJpgWidth(jpgWidth);
//                    }
//                    addJpgWidthTextBox.setText(null);
//                    refresh();
//                } catch (Exception e) {
//                    uiLog.log("Bad JPG Width[" + addJpgWidthTextBox.getText() + "]");
//                }
//            }
//        });

//        threadCountTextBox.addChangeHandler(new ChangeHandler() {
//            @Override
//            public void onChange(ChangeEvent event) {
//                try {
//                    if (settings == null) {
//                        uiLog.log("SettingsDialog Error: no model object [Settings] set");
//                    } else {
//                        Integer tc = new Integer(threadCountTextBox.getText());
//                        settings.setJpgGenThreadCount(tc);
//                    }
//                } catch (NumberFormatException e) {
//                    uiLog.log("Bad Thread Count[" + threadCountTextBox.getText() + "]");
//                }
//            }
//        });

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
        List<Profile> profiles = settings.getProfiles();

        int row = 0;
        for (final Profile profile : profiles) {
            jpgWidthTable.setText(row, 0, profile.getKey());
            Anchor deleteButton = new Anchor("Delete");
            jpgWidthTable.setWidget(row, 1, deleteButton);

//            deleteButton.addClickHandler(new ClickHandler() {
//                @Override
//                public void onClick(ClickEvent event) {
//                    settings.removeProfile(profile);
//                    refresh();
//                }
//            });

            jpgWidthTable.getCellFormatter().getElement(row, 0).getStyle().setPadding(.3, Style.Unit.EM);
            jpgWidthTable.getCellFormatter().getElement(row, 1).getStyle().setPadding(.3, Style.Unit.EM);


            row++;
        }


        threadCountTextBox.setText(settings.getThreadCount() + "");
    }

    void refresh() {
        if (settings == null) {
            Console.log("SettingsDialog Error: no model object [Settings] set. Clearing ui");
            clearScreen();
        } else {
            populateScreen();
        }
    }


    private void saveSettings(BrandKey brandKey) {
        threedAdminClient.saveSettings(brandKey, settings);
    }

}
