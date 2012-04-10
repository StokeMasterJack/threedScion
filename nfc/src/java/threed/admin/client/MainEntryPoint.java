package threed.admin.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import threed.jpgGen.client.JpgGenClient;
import threed.jpgGen.client.JpgQueueMasterPanel;
import threed.jpgGen.client.TabCloseListener;
import threed.jpgGen.shared.JobSpec;
import threed.repo.shared.CommitHistory;
import threed.repo.shared.RepoHasNoHeadException;
import threed.repo.shared.SeriesCommit;
import threed.repo.shared.Settings;
import threed.admin.client.messageLog.MessageLog;
import threed.admin.client.messageLog.MessageLogView;
import threed.admin.client.tabLabel.TabLabel;
import threed.admin.shared.InitData;
import threed.core.threedModel.shared.BrandKey;
import threed.smartClient.client.api.ThreedModelClient;
import threed.core.threedModel.shared.JpgWidth;
import threed.core.threedModel.shared.SeriesId;
import threed.core.threedModel.shared.SeriesKey;
import threed.core.threedModel.shared.ThreedModel;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.TabCreator;
import smartsoft.util.gwt.client.rpc.*;
import smartsoft.util.lang.shared.Path;

public class MainEntryPoint implements EntryPoint, TabCreator, UiLog, UiContext {

    private MainMenu mainMenuBar;
    private TabLayoutPanel tab = new TabLayoutPanel(2, Style.Unit.EM);

    private ThreedAdminClient threedAdminClient;
    private JpgGenClient jpgGenClient;
    private ThreedModelClient threedModelClient;

    private MessageLog messageLog;
    private SettingsDialog settingsDialog;

    //init data
    private Path repoBaseUrl;
    private SeriesPickList seriesPickList;
    private Settings settings;

    private BrandKey brandKey = BrandKey.TOYOTA; //todo DF

    public void onModuleLoad() {
        messageLog = new MessageLog();
        threedAdminClient = new ThreedAdminClient(this);
        jpgGenClient = new JpgGenClient(this);

        repoBaseUrl = ThreedAdminClient.getUrlOfRepoService();
        if (repoBaseUrl == null) {
            throw new NullPointerException();
        }
        this.threedModelClient = new ThreedModelClient(this, repoBaseUrl);


        mainMenuBar = createMainMenuBar();

        Req<InitData> request = threedAdminClient.getInitData();

        request.onSuccess = new SuccessCallback<InitData>() {

            @Override
            public void call(Req<InitData> r) {
                onInitDataReturned(r.result);
            }

        };

        buildMainWindow();

        Place place = Place.createFromToken(History.getToken());
        gotoToInitialPlace(place);


    }

    private void gotoToInitialPlace(Place place) {
        gotoPlace(place);
    }

    private void gotoPlace(Place place) {
        final SeriesKey sk = place.getSeriesKey();
        if (sk != null) {
            openSeries(sk);
        }
    }


    private void openSeriesCommitDialog(final SeriesKey seriesKey) {

        Req<CommitHistory> request = threedAdminClient.getCommitHistory(seriesKey);

        request.onSuccess = new SuccessCallback<CommitHistory>() {
            @Override
            public void call(Req<CommitHistory> r) {
                assert r.result != null;
                final SeriesCommitDialog dlg = new SeriesCommitDialog(seriesKey, r.result);
                dlg.onSeriesCommitSelected = new Command() {
                    @Override
                    public void execute() {
                        CommitHistory selectedCommit = dlg.getSelectedCommit();
                        openSeries(new SeriesCommit(seriesKey, selectedCommit));
                    }
                };
            }
        };

        request.onFailure = new FailureCallback<CommitHistory>() {
            @Override
            public void call(Req<CommitHistory> r) {
                if (r.exception instanceof RepoHasNoHeadException) {
                    NoCommitsDialog dlg = new NoCommitsDialog(seriesKey);
                    dlg.onCheckin = new Command() {
                        @Override
                        public void execute() {
                            localCheckin(seriesKey);
                        }
                    };
                } else {
                    String msg = "Could not fetch list of versions (i.e. commits) for series [" + seriesKey + "].  Error: [" + r.exception.toString() + "]. ";
                    showMessage(msg);
                    r.exception.printStackTrace();
                }
            }
        };

    }

    private void localCheckin(final SeriesKey sk) {
        showMessage("Checking-in: [" + sk + "] ...");
        threedAdminClient.addAllAndCommit(sk, null, null);
    }


    public void onInitDataReturned(InitData initData) {
        Console.log("Received: [" + initData.getSettings() + "]");
        if (settings == null && seriesPickList == null) {
            setSettings(initData.getSettings());
            setSeriesList(initData.getSeriesPickList());
        } else {
            throw new IllegalStateException();
        }
    }


    private void setSettings(Settings settings) {
        if (settings == null) {
            throw new NullPointerException();
        }
        this.settings = settings;

        if (settingsDialog != null) {
            settingsDialog.setSettings(settings);
        }
    }

    private void setSeriesList(SeriesPickList seriesPickList) {
        if (seriesPickList == null) {
            throw new NullPointerException();
        }
        this.seriesPickList = seriesPickList;
        mainMenuBar.setSeriesPickList(seriesPickList);
    }

    private void openSeries(final SeriesKey seriesKey) {
        Req<CommitHistory> r1 = threedAdminClient.getCommitHistory(seriesKey);
        r1.onSuccess = new SuccessCallback<CommitHistory>() {
            @Override
            public void call(Req<CommitHistory> request) {
                CommitHistory commitHistory = request.result;
                SeriesCommit seriesCommit = new SeriesCommit(seriesKey, commitHistory);
                openSeries(seriesCommit);
            }
        };
    }

    private void openSeries(final SeriesCommit seriesCommit) {
        assert seriesCommit != null;
        assert threedAdminClient != null;

        final Path threedModelUrl = threedModelClient.getThreedModelUrl(seriesCommit.getSeriesId());
        Req<ThreedModel> r = threedModelClient.fetchThreedModel(seriesCommit.getSeriesId());

        r.onSuccess = new SuccessCallback<ThreedModel>() {
            @Override
            public void call(Req<ThreedModel> r) {
                ThreedModel threedModel = r.result;
                showMessage("Loading series data for [" + seriesCommit + "] complete!");
                SeriesPanel seriesPanel = new SeriesPanel(MainEntryPoint.this, threedAdminClient, threedModel, seriesCommit.getCommitHistory(), threedModelUrl, settings);
                seriesPanel.setCallback(new SeriesPanel.Callback() {
                    @Override
                    public void generateJpgsButtonClicked(SeriesKey seriesKey, CommitHistory commit, JpgWidth jpgWidth) {
                        SeriesId seriesId = new SeriesId(seriesKey, commit.getRootTreeId());
                        jpgGenClient.startJpgJob(new JobSpec(seriesId, jpgWidth));
                        showMessage("Jpg job started");
                        showJpgQueueMasterStatus.execute();
                    }
                });
                String tabLabel = seriesPanel.getTabLabel();
                addTab(seriesPanel, tabLabel);
            }
        };


    }

    private final Command openSettings = new Command() {
        @Override
        public void execute() {
            openSettingsDialog();
        }
    };

    private void openSettingsDialog() {

        if (settingsDialog == null) {
            settingsDialog = new SettingsDialog(settings, threedAdminClient, this);
        } else {
            settingsDialog.setSettings(settings);
        }

        settingsDialog.center();
        settingsDialog.show();
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

    private MainMenu createMainMenuBar() {
        return new MainMenu();
    }

    class MainMenu extends MenuBar {

        SeriesPickerMenuBar seriesPickerForOpenMenu;
        SeriesPickerMenuBar seriesPickerForCheckinMenu;

        MainMenu() {
            seriesPickerForOpenMenu = new SeriesPickerMenuBar();
            seriesPickerForCheckinMenu = new SeriesPickerMenuBar();

            seriesPickerForOpenMenu.onSeriesPicked = new SeriesPickerHandler() {
                @Override
                public void onSeriesPicked(final SeriesKey seriesKey) {
                    openSeriesCommitDialog(seriesKey);
                }
            };

            seriesPickerForCheckinMenu.onSeriesPicked = new SeriesPickerHandler() {
                @Override
                public void onSeriesPicked(final SeriesKey sk) {
                    localCheckin(sk);
                }
            };


            addItem(new MenuItem("Open Series", seriesPickerForOpenMenu));
            addItem(new MenuItem("JPG Gen Job Status", showJpgQueueMasterStatus));
            addItem(new MenuItem("Settings", openSettings));
            addItem(new MenuItem("Local Checkin", seriesPickerForCheckinMenu));
            addItem(new MenuItem("Misc", createMiscMenu()));


        }

        public void setSeriesPickList(SeriesPickList seriesPickList) {
            seriesPickerForOpenMenu.populate(seriesPickList);
            seriesPickerForCheckinMenu.populate(seriesPickList);
        }

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
                JpgQueueMasterPanel d = new JpgQueueMasterPanel(jpgGenClient, MainEntryPoint.this, MainEntryPoint.this);
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
            threedAdminClient.purgeRepoCache();
        }
    }


    @Override
    public void log(String msg) {
        messageLog.log(msg);
    }

    private Widget createHeaderPanel() {
        DockLayoutPanel dock = new DockLayoutPanel(Style.Unit.EM);
        dock.addNorth(mainMenuBar, 1.7);
        return dock;
    }

    public void showMessage(String msg) {
        messageLog.log(msg);
    }
}
