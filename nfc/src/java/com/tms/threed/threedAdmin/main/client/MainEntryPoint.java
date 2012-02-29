package com.tms.threed.threedAdmin.main.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.*;
import com.tms.threed.threedAdmin.jpgGen.client.CommitPickList;
import com.tms.threed.threedAdmin.jpgGen.client.JpgQueueMasterPanel;
import com.tms.threed.threedAdmin.jpgGen.client.TabCloseListener;
import com.tms.threed.threedAdmin.main.client.messageLog.MessageLog;
import com.tms.threed.threedAdmin.main.client.messageLog.MessageLogView;
import com.tms.threed.threedAdmin.main.client.services.JpgGenServiceAsync;
import com.tms.threed.threedAdmin.main.shared.ThreedAdminServiceAsync;
import com.tms.threed.threedAdmin.main.client.tabLabel.TabLabel;
import com.tms.threed.threedAdmin.main.shared.InitData;
import com.tms.threed.threedAdmin.main.shared.ThreedAdminService;
import com.tms.threed.threedFramework.repo.shared.CommitHistory;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RepoHasNoHeadException;
import com.tms.threed.threedFramework.repo.shared.RtConfig;
import com.tms.threed.threedFramework.threedModel.client.ThreedModelServiceJson;
import com.tms.threed.threedFramework.threedModel.shared.SeriesId;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.List;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.isEmpty;

public class MainEntryPoint implements EntryPoint, UiContext {

    private final TabLayoutPanel tab = new TabLayoutPanel(2, Style.Unit.EM);

    private final ThreedAdminServiceAsync service;
    private final JpgGenServiceAsync jpgGenService;

    ThreedModelServiceJson threedModelService;

    private final MenuBar mainMenuBar;

    private SeriesPickerMenuBar pickSeriesForDisplayMenuBar;
    private SeriesPickerMenuBar pickSeriesForCheckin;

    private final MessageLog messageLog = new MessageLog();
//    private final TabLabel msgbox = new TabLabel("Place Holder", true);

    private InitData initData;

    public MainEntryPoint() {

        service = createThreedAdminService();
        jpgGenService = new JpgGenServiceAsync(this, service);

        SeriesPickerMenuBar.SeriesPickerHandler seriesPickerHandler1 = new SeriesPickerMenuBar.SeriesPickerHandler() {
            @Override
            public void onSeriesPicked(final SeriesKey seriesKey) {

                final CommitPickList.Callback callback = new CommitPickList.Callback() {
                    @Override
                    public void openButtonClicked(SeriesKey seriesKey, CommitHistory ch) {
                        openSeries(seriesKey, ch);
                    }

                    @Override
                    public void checkinButtonClicked(SeriesKey seriesKey) {
                        localCheckin(seriesKey);
                    }
                };

                service.getCommitHistory(seriesKey, new AsyncCallback<CommitHistory>() {

                    @Override
                    public void onSuccess(CommitHistory head) {
                        new OpenSeriesDialog(seriesKey, head, callback);
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        if (e instanceof RepoHasNoHeadException) {
                            new OpenSeriesDialog(seriesKey, null, callback);
                        } else {
                            String msg = "Could not fetch list of versions (i.e. commits) for series [" + seriesKey + "].  Error: [" + e.toString() + "]. ";
                            showMessage(msg);
                            e.printStackTrace();
                        }
                    }

                });


            }
        };
        pickSeriesForDisplayMenuBar = new SeriesPickerMenuBar(seriesPickerHandler1);
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
        dock.addNorth(mainMenuBar, 1.7);
        return dock;
    }

    public void showMessage(String msg) {
        messageLog.log(msg);
    }


    public void onModuleLoad() {

        service.getInitData(new AsyncCallback<InitData>() {

            @Override
            public void onSuccess(InitData initData) {
                MainEntryPoint.this.initData = initData;
                onInitDataReturned();
            }

            @Override
            public void onFailure(Throwable e) {
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


    private final SeriesPickerMenuBar.SeriesPickerHandler seriesPickerHandler3 = new SeriesPickerMenuBar.SeriesPickerHandler() {

        @Override
        public void onSeriesPicked(final SeriesKey sk) {
            localCheckin(sk);
        }

    };

    private void localCheckin(final SeriesKey sk) {
        showMessage("Checking-in: [" + sk + "] ...");

        service.addAllAndCommit(sk, null, null, new AsyncCallback<CommitHistory>() {
            @Override
            public void onFailure(Throwable e) {
                showMessage("Local " + sk + " check-in failed: " + e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onSuccess(CommitHistory result) {
                showMessage("Checking-in: [" + sk + "] complete!");
            }
        });
    }


    public void onInitDataReturned() {
        Console.log("Received: [" + initData.getRtConfig() + "]");
        pickSeriesForDisplayMenuBar.populate(initData.getSeriesNameWithYears());
        pickSeriesForCheckin.populate(initData.getSeriesNameWithYears());
    }


    private void openSeries(final SeriesKey pickedSeriesKey, final CommitHistory commit) {
        assert pickedSeriesKey != null;
        assert service != null;


        this.showMessage("Loading data for series [" + pickedSeriesKey + "] ...");
        Console.log("Fetching ThreedModel[" + pickedSeriesKey + "] JSON...");

        System.out.println("tagCommit.getCommitId() = " + commit.getCommitId());
        System.out.println("tagCommit.getTreeId() = " + commit.getRootTreeId());

        if (threedModelService == null) {
            Path repoBaseUrl = initData.getRepoBaseUrl();
            threedModelService = new ThreedModelServiceJson(repoBaseUrl);
        }

        final String threedModelUrl = threedModelService.getThreedModelUrl(pickedSeriesKey, commit.getRootTreeId());

        Console.log("About to request ThreedModel from server using threedModelUrl[" + threedModelUrl + "]");


        threedModelService.fetchThreedModel2(pickedSeriesKey, commit.getRootTreeId(), new ThreedModelServiceJson.Callback() {
            @Override
            public void onThreeModelReceived(ThreedModel threedModel) {
                showMessage("Loading series data for [" + pickedSeriesKey + "] complete!");
                SeriesViewPanel seriesViewPanel = new SeriesViewPanel(MainEntryPoint.this, service, threedModel, commit, threedModelUrl, initData.getRtConfig());
                seriesViewPanel.setCallback(new SeriesViewPanel.Callback() {
                    @Override
                    public void generateJpgsButtonClicked(SeriesKey seriesKey, CommitHistory commit, JpgWidth jpgWidth) {
                        SeriesId seriesId = new SeriesId(seriesKey, commit.getRootTreeId());
                        jpgGenService.startJpgJob(seriesId, jpgWidth);
                        showMessage("Jpg job started");
                        showJpgQueueMasterStatus.execute();
                    }
                });
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
                @Override
                public void onClick(ClickEvent event) {
//                    add();
                }
            });

            bSave.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    saveRtConfig();
                    hide();
                }
            });

            addJpgWidthTextBox.addChangeHandler(new ChangeHandler() {
                @Override
                public void onChange(ChangeEvent event) {
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
                @Override
                public void onChange(ChangeEvent event) {
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
                    @Override
                    public void onClick(ClickEvent event) {
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

            service.saveRtConfig(initData.getRtConfig(), new AsyncCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    showMessage("Config data saved");
                }

                @Override
                public void onFailure(Throwable e) {
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
            @Override
            public void onClick(ClickEvent event) {
                tab.remove(widget);
                if (widget instanceof TabCloseListener) {
                    TabCloseListener tabCloseListener = (TabCloseListener) widget;
                    tabCloseListener.afterClose();
                }
            }
        });
        return tab.getWidgetCount() - 1;

    }


    private MenuBar createMiscMenu() {
        MenuBar mb = new MenuBar();
        mb.addItem("Purge Admin Tool Repo Cache", new PurgeRepoCacheCommand());
        return mb;
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.addItem(new MenuItem("Open Series", pickSeriesForDisplayMenuBar));
        menuBar.addItem(new MenuItem("JPG Gen Job Status", showJpgQueueMasterStatus));
        menuBar.addItem(new MenuItem("Settings", openSettings));
        menuBar.addItem(new MenuItem("Local Checkin", pickSeriesForCheckin));
        menuBar.addItem(new MenuItem("Misc", createMiscMenu()));
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
                JpgQueueMasterPanel d = new JpgQueueMasterPanel(jpgGenService, MainEntryPoint.this);
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


        SplitLayoutPanel splitLayoutPanel = new SplitLayoutPanel();
        splitLayoutPanel.addSouth(createLogWindow(), 50);
        splitLayoutPanel.add(tab);

        DockLayoutPanel dock = new DockLayoutPanel(Style.Unit.EM);
        dock.addNorth(createHeaderPanel(), 1.8);
        dock.add(splitLayoutPanel);
        RootLayoutPanel.get().add(dock);
    }

    private Widget createLogWindow() {
        final MessageLogView messageLogView = new MessageLogView(messageLog);
        return messageLogView;
    }


    private class PurgeRepoCacheCommand implements Command {
        @Override
        public void execute() {
            showMessage("Purging cache..");
            service.purgeRepoCache(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable e) {
                    e.printStackTrace();
                    showMessage("Purge failed[" + e.toString() + "]");
                }

                @Override
                public void onSuccess(Void result) {
                    showMessage("Purge complete!");
                }
            });
        }
    }

    private ThreedAdminServiceAsync createThreedAdminService() {
        String baseUrl = JpgGenServiceAsync.getBaseUrl();
        ThreedAdminServiceAsync service = GWT.create(ThreedAdminService.class);
        ((ServiceDefTarget) service).setServiceEntryPoint(baseUrl);
        return service;
    }
}
