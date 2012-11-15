package c3i.admin.client;

import c3i.admin.client.jpgGen.JpgGenClient;
import c3i.admin.client.jpgGen.JpgQueueMasterPanel;
import c3i.admin.client.messageLog.UserLog;
import c3i.admin.client.messageLog.UserLogView;
import c3i.admin.shared.BrandInit;
import c3i.core.common.shared.SeriesKey;
import c3i.repo.shared.CommitHistory;
import c3i.repo.shared.RepoHasNoHeadException;
import c3i.repo.shared.SeriesCommit;
import c3i.util.shared.futures.OnSuccess;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.rpc.FailureCallback;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;
import smartsoft.util.gwt.client.ui.tabLabel.TabAware;
import smartsoft.util.gwt.client.ui.tabLabel.TabCreator;
import smartsoft.util.gwt.client.ui.tabLabel.TabLabel;

import javax.annotation.Nonnull;

public class MainEntryPoint implements EntryPoint, TabCreator {

    private App app;

    private MainMenu mainMenuBar;
    private TabLayoutPanel tab = new TabLayoutPanel(2, Style.Unit.EM);

    private UserLog userLog;

    public void onModuleLoad() {

        app = new App(this);
        userLog = app.getUserLog();



        app.ensureLoaded().success(new OnSuccess<BrandInit>() {
            @Override
            public void onSuccess(@Nonnull BrandInit brand) {
                mainMenuBar = createMainMenuBar(brand);
                buildMainWindow();
                Place place = Place.createFromToken(History.getToken());
                gotoToInitialPlace(brand, place);
            }
        });


    }


    private void gotoToInitialPlace(BrandInit brand, Place place) {
        gotoPlace(brand, place);
    }

    private void gotoPlace(BrandInit brand, Place place) {
        final SeriesKey sk = place.getSeriesKey();
        if (sk != null) {
            openSeriesHead(brand, sk);
        }
    }


    private void openSeriesCommitDialog(final BrandInit brand, final SeriesKey seriesKey) {

        log("Loading " + seriesKey.getShortName() + " commit history...");
        Req<CommitHistory> request = app.getThreedAdminClient().getCommitHistory(seriesKey);

        request.onSuccess = new SuccessCallback<CommitHistory>() {
            @Override
            public void call(Req<CommitHistory> r) {
                log("Loading " + seriesKey.getShortName() + " commit history complete!");
                assert r.result != null;
                final SeriesCommitDialog dlg = new SeriesCommitDialog(seriesKey, r.result);
                dlg.onSeriesCommitSelected = new Command() {
                    @Override
                    public void execute() {
                        CommitHistory selectedCommit = dlg.getSelectedCommit();
                        SeriesCommit seriesCommit = new SeriesCommit(seriesKey, selectedCommit);
                        openSeriesVersion(brand, seriesCommit);
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
                            app.localCheckin(seriesKey);
                        }
                    };
                } else {
                    String msg = "Could not fetch list of versions (i.e. commits) for series [" + seriesKey + "].  Error: [" + r.exception.toString() + "]. ";
                    Console.log(msg);
                    r.exception.printStackTrace();
                }
            }
        };

    }

    private void openSeriesHead(final BrandInit brand, final SeriesKey seriesKey) {
        Req<CommitHistory> r1 = app.getThreedAdminClient().getCommitHistory(seriesKey);

        r1.onSuccess = new SuccessCallback<CommitHistory>() {
            @Override
            public void call(Req<CommitHistory> request) {
                CommitHistory commitHistory = request.result;
                SeriesCommit seriesCommit = new SeriesCommit(seriesKey, commitHistory);
                openSeriesVersion(brand, seriesCommit);
            }
        };
    }

    private void openSeriesVersion(BrandInit brand, final SeriesCommit seriesCommit) {
        assert seriesCommit != null;
        SeriesPanel seriesPanel = new SeriesPanel(new SeriesSession(app, brand, seriesCommit));
        addTab(seriesPanel);
    }


    public int addTab(final TabAware tabAware) {
        final TabLabel tabLabel = tabAware.getTabLabel();
        tab.add(tabAware, tabLabel);


        tab.selectTab(tabAware);
        tabLabel.addCloseButtonHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                tab.remove(tabAware.asWidget());
                tabAware.afterClose();
            }
        });
        return tab.getWidgetCount() - 1;

    }


    private MenuBar createMiscMenu(BrandInit brandInit) {

        final Command purgeRepoCacheCommand = new Command() {
            @Override
            public void execute() {
                ThreedAdminClient threedAdminClient = app.getThreedAdminClient();
                Console.log("Purging cache..");
                threedAdminClient.purgeRepoCache(app.getBrandKey());
            }
        };

        MenuBar mb = new MenuBar();
        mb.addItem("Purge Admin Tool Repo Cache", purgeRepoCacheCommand);
//        mb.addItem("jsonpCommand", jsonpCommand);
        return mb;
    }

    private MainMenu createMainMenuBar(BrandInit brand) {
        MainMenu mm = new MainMenu(brand);
        return mm;
    }

    class MainMenu extends MenuBar {

        SeriesPickerMenuBar seriesPickerForOpenMenu;
        SeriesPickerMenuBar seriesPickerForCheckinMenu;

        MainMenu(final BrandInit brand) {

            final Command showJpgQueueMasterStatus = new Command() {
                @Override
                public void execute() {
                    int i = isJpgQueueMasterStatusAlreadyOpen();
                    if (i != -1) {
                        tab.selectTab(i);
                    } else {
                        JpgGenClient jpgGenClient = app.getJpgGenClient();
                        JpgQueueMasterPanel d = new JpgQueueMasterPanel(jpgGenClient, MainEntryPoint.this);
                        addTab(d);
                    }
                }
            };


            seriesPickerForOpenMenu = new SeriesPickerMenuBar();
            seriesPickerForCheckinMenu = new SeriesPickerMenuBar();

            seriesPickerForOpenMenu.onSeriesPicked = new SeriesPickerHandler() {
                @Override
                public void onSeriesPicked(final SeriesKey seriesKey) {
                    openSeriesCommitDialog(brand, seriesKey);
                }
            };

            seriesPickerForCheckinMenu.onSeriesPicked = new SeriesPickerHandler() {
                @Override
                public void onSeriesPicked(final SeriesKey sk) {
                    app.localCheckin(sk);
                }
            };


            addItem(new MenuItem("Open Series", seriesPickerForOpenMenu));
            addItem(new MenuItem("JPG Gen Job Status", showJpgQueueMasterStatus));
            addItem(new MenuItem("Local Checkin", seriesPickerForCheckinMenu));
            addItem(new MenuItem("Misc", createMiscMenu(brand)));


            SeriesPickList seriesPickList = brand.getSeriesPickList();
            seriesPickerForOpenMenu.populate(seriesPickList);
            seriesPickerForCheckinMenu.populate(seriesPickList);

            getElement().getStyle().setZIndex(30000);
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
        return new UserLogView(userLog);
    }

    public void log(String msg) {
        userLog.log(msg);
    }

    private Widget createHeaderPanel() {
        DockLayoutPanel dock = new DockLayoutPanel(Style.Unit.EM);
        dock.addNorth(mainMenuBar, 1.7);
        return dock;
    }

}
