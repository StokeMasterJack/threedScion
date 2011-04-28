package com.tms.threed.threedAdmin.main.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.tms.threed.threedAdmin.jpgGen.client.JpgGenStatusPanel;
import com.tms.threed.threedAdmin.jpgGen.client.JpgQueueMasterPanel;
import com.tms.threed.threedAdmin.jpgGen.client.NewSeriesRepoDialog;
import com.tms.threed.threedAdmin.jpgGen.client.StartJpgJobDialog;
import com.tms.threed.threedAdmin.jpgGen.client.TabCloseListener;
import com.tms.threed.threedAdmin.main.client.services.FetchSlicesCallback;
import com.tms.threed.threedAdmin.main.client.services.ThreedAdminService1Async;
import com.tms.threed.threedAdmin.main.client.tabLabel.TabLabel;
import com.tms.threed.threedAdmin.main.shared.InitData;
import com.tms.threed.threedFramework.previewPane.client.threedServiceClient.ThreedModelServiceJson;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.repo.shared.TagCommit;
import com.tms.threed.threedFramework.threedCore.shared.SeriesId;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.List;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.isEmpty;

public class MainEntryPoint implements EntryPoint, UiContext {

    private final TabLayoutPanel tab = new TabLayoutPanel(2, Style.Unit.EM);

    private final ThreedAdminService1Async service;

    ThreedModelServiceJson threedModelService;

    private final MenuBar mainMenuBar;

    private SeriesPickerMenuBar pickSeriesForDisplayMenuBar;
    private SeriesPickerMenuBar pickSeriesForJpgGenMenuBar;
    private SeriesPickerMenuBar pickSeriesForCheckin;

    private final TabLabel msgbox = new TabLabel("Place Holder", true);

    private InitData initData;

    public MainEntryPoint() {
        service = new ThreedAdminService1Async(this);


        pickSeriesForDisplayMenuBar = new SeriesPickerMenuBar(seriesPickerHandler1);
        pickSeriesForJpgGenMenuBar = new SeriesPickerMenuBar(seriesPickerHandler2);
        pickSeriesForCheckin = new SeriesPickerMenuBar(seriesPickerHandler3);

        mainMenuBar = createMenuBar();

    }

    private Path getRepoBaseUrl() {
//        return new Path("http://127.0.0.1:8080/configurator-content");


        Path hostPageBaseURL = new Path(GWT.getHostPageBaseURL());
        Path repoBaseUrl = hostPageBaseURL.dotDot().append("configurator-content");

        return repoBaseUrl;
    }


    private Widget createHeaderPanel() {
        DockLayoutPanel dock = new DockLayoutPanel(Style.Unit.EM);

        FlowPanel flowPanel = new FlowPanel();

        flowPanel.setSize("100%", "100%");

        hideMessage();

        flowPanel.add(msgbox);


        msgbox.getElement().getStyle().setMarginTop(.3, Style.Unit.EM);
        msgbox.getElement().getStyle().setMarginRight(2, Style.Unit.EM);
        msgbox.getElement().getStyle().setFloat(Style.Float.RIGHT);
        msgbox.getElement().getStyle().setColor("red");

        dock.addNorth(mainMenuBar, 1.7);
        dock.addSouth(flowPanel, 2.3);


        msgbox.addCloseButtonHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                hideMessage();
            }
        });


        return dock;
    }

    public void hideMessage() {
        msgbox.setVisible(false);
    }

    public void showMessage(String msg) {
        msgbox.setLabel(msg);
        msgbox.setVisible(true);
    }


    public void onModuleLoad() {

        service.getThreedAdminService2Async().getInitData(new AsyncCallback<InitData>() {

            @Override public void onSuccess(InitData initData) {
                MainEntryPoint.this.initData = initData;
                onInitDataReturned();
            }

            @Override public void onFailure(Throwable e) {
                showMessage("Problem loading init data. See log for more details. " + e);
                e.printStackTrace();
            }


        });

//        service.getThreedAdminService2Async().getSeriesNameWithYears(new AsyncCallback<ArrayList<SeriesNamesWithYears>>() {
//            @Override public void onFailure(Throwable e) {
//                String msg = "Error getting seriesNames: " + e;
//                Console.log(msg);
//                showMessage(msg);
//                e.printStackTrace();
//
//            }
//
//            @Override public void onSuccess(ArrayList<SeriesNamesWithYears> result) {
//
//            }
//        });


        buildMainWindow();

    }

    private final SeriesPickerMenuBar.SeriesPickerHandler seriesPickerHandler1 = new SeriesPickerMenuBar.SeriesPickerHandler() {
        @Override
        public void onSeriesPicked(SeriesKey seriesKey) {

            OpenSeriesDialog d = new OpenSeriesDialog(service, seriesKey, new OpenSeriesDialog.OpenSeriesCallback() {
                @Override
                public void onOpen(SeriesKey seriesKey, TagCommit tagCommit) {
                    openNewSeriesViewTab1(seriesKey, tagCommit);
                }
            });

            d.show();
            d.center();


        }
    };


    private final SeriesPickerMenuBar.SeriesPickerHandler seriesPickerHandler3 = new SeriesPickerMenuBar.SeriesPickerHandler() {
        @Override
        public void onSeriesPicked(final SeriesKey sk) {

            showMessage("Checking-in: [" + sk + "] ...");

            service.addAllAndCommit(sk, null, new AsyncCallback<String>() {
                @Override public void onFailure(Throwable e) {
                    showMessage("Local " + sk + " check-in failed: " + e.getMessage());
                    e.printStackTrace();
                }

                @Override public void onSuccess(String result) {
                    showMessage("Local " + sk + " check-in successful");
                }
            });

        }


    };


    private final SeriesPickerMenuBar.SeriesPickerHandler seriesPickerHandler2 = new SeriesPickerMenuBar.SeriesPickerHandler() {
        @Override
        public void onSeriesPicked(final SeriesKey newSeriesKey) {

            RtConfig rtConfig = initData.getRtConfig();
            final StartJpgJobDialog d = new StartJpgJobDialog(service, newSeriesKey, rtConfig);
            d.show();
            d.center();

            d.addCloseHandler(new CloseHandler<PopupPanel>() {
                @Override public void onClose(CloseEvent<PopupPanel> popupPanelCloseEvent) {
                    if (!d.isCanceled()) {
                        showMessage("Jpg job started");
                        showJpgQueueMasterStatus.execute();
                    }
                }
            });
        }
    };

    public void onInitDataReturned() {
        Console.log("Received: [" + initData.getRtConfig() + "]");

        pickSeriesForDisplayMenuBar.populate(initData.getSeriesNameWithYears());
        pickSeriesForJpgGenMenuBar.populate(initData.getSeriesNameWithYears());
        pickSeriesForCheckin.populate(initData.getSeriesNameWithYears());
    }

    private void showJpgGenStatusTab(final SeriesId seriesId, final JpgWidth jpgWidth) {

        service.fetchViews(seriesId, new FetchSlicesCallback() {
            @Override public void onSuccess(JSONArray jsViews) {

                JpgGenStatusPanel d = new JpgGenStatusPanel(service, seriesId, jsViews, jpgWidth);
                addTab(d, "Jpg Gen Status");


            }
        });


    }

//    private void populateSeriesMenuBar(SeriesKeys seriesKeys) {
//        assert seriesKeys != null;
//        SortedSet<Integer> years = seriesKeys.getYears();
//        for (Integer year : years) {
//            MenuBar yearMenuBar = new MenuBar(true);
//            SortedSet<SeriesKey> seriesKeysSet = this.seriesKeys.getSeriesKeys(year);
//            for (final SeriesKey seriesKey : seriesKeysSet) {
//                yearMenuBar.addItem(seriesKey.getName(), new Command() {
//                    @Override
//                    public void execute() {
//                        pickSeries(seriesKey);
//                    }
//                });
//            }
//            seriesMenuBar.addItem(year + "", yearMenuBar);
//        }
//    }


    private void openNewSeriesViewTab1(final SeriesKey pickedSeriesKey, final TagCommit tagCommit) {
        assert pickedSeriesKey != null;
        assert service != null;


        Console.log("Fetching ThreedModel[" + pickedSeriesKey + "] JSON...");

        System.out.println("tagCommit.getCommitId() = " + tagCommit.getCommitId());
        System.out.println("tagCommit.getTreeId() = " + tagCommit.getRootTreeId());

         if (threedModelService == null) {
            Path repoBaseUrl = initData.getRepoBaseUrl();
            threedModelService = new ThreedModelServiceJson(repoBaseUrl);
        }

        final String threedModelUrl = threedModelService.getThreedModelUrl(pickedSeriesKey, tagCommit.getRootTreeId());

        Console.log("About to request ThreedModel from server using threedModelUrl[" + threedModelUrl + "]");



        threedModelService.fetchThreedModel2(pickedSeriesKey, tagCommit.getRootTreeId(), new ThreedModelServiceJson.Callback() {
            @Override public void onThreeModelReceived(ThreedModel threedModel) {
                SeriesViewPanel seriesViewPanel = new SeriesViewPanel(MainEntryPoint.this, service, threedModel, tagCommit, threedModelUrl, initData.getRtConfig());
                String tabLabel = seriesViewPanel.getTabLabel();
                addTab(seriesViewPanel, tabLabel);
            }
        });


    }

    class JpgWidthDialog extends MyDialogBox {


        FlexTable main = new FlexTable();
        FlowPanel fp = new FlowPanel();

        FlexTable t = new FlexTable();

        TextBox addJpgWidthTextBox = new TextBox();
        TextBox threadCountTextBox = new TextBox();
        FlexTable jpgWidthTable = new FlexTable();
        Button bAdd = new Button("Add");


        Button bSave = new Button("Save");

        JpgWidthDialog() {
            super("Jpg Width");
            getElement().getStyle().setZIndex(2000);
            FlowPanel topPanel = new FlowPanel();
            topPanel.add(addJpgWidthTextBox);
            topPanel.add(bAdd);
//            topPanel.getElement().getStyle().setBackgroundColor("yellow");
//            topPanel.getElement().getStyle().setPadding(0, Style.Unit.EM);

//            textBox.getElement().getStyle().setMargin(0, Style.Unit.EM);
//            bAdd.getElement().getStyle().setMargin(0, Style.Unit.EM);


            addJpgWidthTextBox.setWidth("5em");
//            listBox.setVisibleItemCount(5);
//            jpgWidthTable.setWidth("100%");
//            jpgWidthTable.setBorderWidth(1);
            jpgWidthTable.setWidth("100%");

//            FlowPanel buttonPanel = new FlowPanel();
//            buttonPanel.add(bSave);

            t.setHTML(0, 0, "<b>JPG Widths:</b>");
            t.setWidget(1, 0, topPanel);
            t.setWidget(2, 0, jpgWidthTable);

            t.setHTML(3, 0, "<div style='font-weight:bold;padding-top:2em'>Thread Count:</div>");
            t.setWidget(4, 0, threadCountTextBox);


//            t.setWidget(2, 0, buttonPanel);
//            t.getFlexCellFormatter().setColSpan(2, 0, 2);
//            t.getFlexCellFormatter().setHorizontalAlignment(2, 0, HasHorizontalAlignment.ALIGN_CENTER);

//            bAdd.getElement().getStyle().setMargin(1, Style.Unit.EM);

//            add(bRemove);
//            add(bAdd);


            main.setWidget(0, 0, t);
            main.setWidget(1, 0, bSave);
            main.getFlexCellFormatter().setHorizontalAlignment(1, 0, HasHorizontalAlignment.ALIGN_CENTER);
            main.getFlexCellFormatter().getElement(1, 0).getStyle().setPaddingTop(2, Style.Unit.EM);
            fp.add(main);

//            t.setHeight("30em");
//            t.getFlexCellFormatter().setVerticalAlignment(0, 0, HasVerticalAlignment.ALIGN_TOP);
//            t.setBorderWidth(1);

            fp.getElement().getStyle().setPadding(1, Style.Unit.EM);
            setWidget(fp);


//            bRemove.addClickHandler(new ClickHandler() {
//                @Override public void onClick(ClickEvent event) {
//                    int selectedIndex = listBox.getSelectedIndex();
//                    if (selectedIndex == -1) return;
//                    listBox.removeItem(selectedIndex);
//
//                    if(listBox.getItemCount()>0){
//                        listBox.setSelectedIndex(0);
//                    }
//
//                }
//            });

            bAdd.addClickHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
//                    add();
                }
            });

            bSave.addClickHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
                    saveRtConfig();
                    hide();
                }
            });

            addJpgWidthTextBox.addChangeHandler(new ChangeHandler() {
                @Override public void onChange(ChangeEvent event) {
                    try {
                        JpgWidth jpgWidth = new JpgWidth(addJpgWidthTextBox.getText());
                        initData.getRtConfig().addJpgWidth(jpgWidth);
                        addJpgWidthTextBox.setText(null);
                        refresh();
                    } catch (Exception e) {
                        Window.alert("Bad JPG Width[" + addJpgWidthTextBox.getText() + "]");
                    }
                }
            });

            threadCountTextBox.addChangeHandler(new ChangeHandler() {
                @Override public void onChange(ChangeEvent event) {
                    try {
                        Integer tc = new Integer(threadCountTextBox.getText());
                        initData.getRtConfig().setJpgGenThreadCount(tc);
                    } catch (NumberFormatException e) {
                        Window.alert("Bad Thread Count[" + threadCountTextBox.getText() + "]");
                    }
                }
            });

            refresh();


        }

        void refresh() {
            jpgWidthTable.clear();
            jpgWidthTable.removeAllRows();
            List<JpgWidth> jpgWidths = initData.getRtConfig().getJpgWidths();

            int row = 0;
            for (final JpgWidth jpgWidth : jpgWidths) {
                if (jpgWidth.isStandard()) continue;
                jpgWidthTable.setText(row, 0, jpgWidth.intValue() + "px");
                Anchor deleteButton = new Anchor("Delete");
                jpgWidthTable.setWidget(row, 1, deleteButton);
                deleteButton.addClickHandler(new ClickHandler() {
                    @Override public void onClick(ClickEvent event) {
                        initData.getRtConfig().removeJpgWith(jpgWidth);
                        refresh();
                    }
                });

                jpgWidthTable.getCellFormatter().getElement(row, 0).getStyle().setPadding(.3, Style.Unit.EM);
                jpgWidthTable.getCellFormatter().getElement(row, 1).getStyle().setPadding(.3, Style.Unit.EM);


                row++;
            }


            threadCountTextBox.setText(initData.getRtConfig().getJpgGenThreadCount() + "");
        }


        void saveRtConfig() {

            service.service2.saveRtConfig(initData.getRtConfig(), new AsyncCallback<Void>() {
                @Override public void onSuccess(Void result) {
                    showMessage("Config data saved");
                }

                @Override public void onFailure(Throwable e) {
                    String msg = "Problem saving config data: " + e;
                    showMessage(msg);
                    e.printStackTrace();
                }


            });


        }

        boolean isJpgWidthValid(String sWidth) {
            if (isEmpty(sWidth)) return false;
            try {
                new JpgWidth(sWidth);
                return true;
            } catch (Exception e) {
                return false;
            }
        }


//        void setJpgWidths(List<JpgWidth> jpgWidths) {
//            listBox.clear();
//            for (JpgWidth jpgWidth : jpgWidths) {
//                listBox.addItem(jpgWidth.intValue() + "");
//            }
//            listBox.setSelectedIndex(0);
//        }
    }

    private final Command openSettings = new Command() {
        @Override
        public void execute() {
            openJpgWidthsPopup();
        }
    };

    private void openJpgWidthsPopup() {

        if (initData == null) {
            showMessage("initData is null");
            return;
        }

        RtConfig rtConfig = initData.getRtConfig();
        if (rtConfig == null) {
            showMessage("rtConfig is null");
            return;
        }


        List<JpgWidth> jpgWidths = rtConfig.getJpgWidths();
        if (jpgWidths == null) {
            showMessage("jpgWidths is null");
            return;
        }

        if (jpgWidths.isEmpty()) {
            showMessage("jpgWidths is empty");
            return;
        }

        Console.log("jpgWidths[" + jpgWidths + "]");

        JpgWidthDialog d = new JpgWidthDialog();
//        d.setJpgWidths(jpgWidths);
        d.center();
        d.show();
    }

    public int addTab(final Widget widget, String tabName) {
        final TabLabel tabLabel = new TabLabel(tabName);
        tab.add(widget, tabLabel);

        if (widget instanceof TabAware) {
            TabAware tabAware = (TabAware) widget;
            tabAware.setTabLabel(tabLabel);
        }

        tab.selectTab(widget);
        tabLabel.addCloseButtonHandler(new ClickHandler() {
            @Override public void onClick(ClickEvent event) {
                tab.remove(widget);
                if (widget instanceof TabCloseListener) {
                    TabCloseListener tabCloseListener = (TabCloseListener) widget;
                    tabCloseListener.afterClose();
                }
            }
        });
        return tab.getWidgetCount() - 1;

    }


    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem(new MenuItem("Open Series", pickSeriesForDisplayMenuBar));
        menuBar.addItem(new MenuItem("New JPG Gen Job", pickSeriesForJpgGenMenuBar));
        menuBar.addItem(new MenuItem("JPG Gen Job Status", showJpgQueueMasterStatus));
        menuBar.addItem(new MenuItem("Settings", openSettings));
        menuBar.addItem(new MenuItem("Local Checkin", pickSeriesForCheckin));
        return menuBar;
    }

    private MenuBar createVersionControlMenu() {

        MenuBar menuBar = new MenuBar(true);

        menuBar.addItem("Create new series repo", new Command() {
            @Override public void execute() {
                NewSeriesRepoDialog d = new NewSeriesRepoDialog(service);
                d.show();
                d.center();
            }
        });


//        menuBar.addItem("Checkin", checkingCommand);

        return menuBar;

    }


    private final Command todoCommand = new Command() {
        @Override
        public void execute() {
            Window.alert("TODO");
        }
    };

    private int isJpgQueueMasterStatusAlreadyOpen() {
        for (int i = 0; i < tab.getWidgetCount(); i++) {
            Widget w = tab.getWidget(i);
            if (w instanceof JpgQueueMasterPanel) {
                return i;
            }
        }

        return -1;

    }


    private Command showJpgQueueMasterStatus = new Command() {
        @Override
        public void execute() {
            int i = isJpgQueueMasterStatusAlreadyOpen();
            if (i != -1) {
                tab.selectTab(i);
            } else {
                JpgQueueMasterPanel d = new JpgQueueMasterPanel(service, MainEntryPoint.this);
                addTab(d, "Jpg Queue Status");
            }
        }
    };


    private static String jpgGenUrlTemplate = "/jpgGenerator/queueStatus.jsp?seriesName=${seriesName}&seriesYear=${seriesYear}";

    public static native void open(String url) /*-{
        $wnd.open(url);
    }-*/;


    void buildMainWindow() {
        tab.setSize("100%", "100%");
        DockLayoutPanel dock = new DockLayoutPanel(Style.Unit.EM);
        dock.addNorth(createHeaderPanel(), 4);
        dock.add(tab);
        RootLayoutPanel.get().add(dock);
    }

}
