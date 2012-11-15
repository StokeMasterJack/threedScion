package c3i.admin.client;

import c3i.admin.client.featurePicker.CurrentUiPicks;
import c3i.admin.client.jpgGen.JpgGenClient;
import c3i.admin.client.jpgGen.JpgQueueMasterPanel;
import c3i.admin.shared.jpgGen.JobSpec;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.imageModel.shared.ImageMode;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.Profiles;
import c3i.core.threedModel.shared.CommitKey;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.shared.CommitHistory;
import c3i.smartClient.client.model.ViewsSession;
import c3i.smartClient.client.skins.BytSummary;
import c3i.smartClient.client.skins.SimpleSkin;
import c3i.smartClient.client.skins.Skin;
import c3i.smartClient.client.skins.bytSkin.BytMain;
import c3i.util.shared.futures.RWValue;
import c3i.util.shared.futures.Value;
import com.google.common.collect.ImmutableList;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Window;
import smartsoft.util.gwt.client.rpc.Req;
import smartsoft.util.gwt.client.rpc.SuccessCallback;
import smartsoft.util.gwt.client.ui.tabLabel.TabCreator;

import java.util.ArrayList;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

public class ThreedAdminModel {

    private final ViewsSession viewsSession;
    private final Profiles profiles;

    //cache
    private final ThreedModel threedModel;
    private final SeriesKey seriesKey;
    private final FeatureModel featureModel;

    private final ThreedAdminClient threedAdminClient;
    private final ImmutableList<Skin> skins;


    private final Value<Skin> skin;


    private CurrentUiPicks currentUiPicks;

    private final RWValue<CommitHistory> commitHistory;

    private final JpgGenClient jpgGenClient;
    private Series series;


    public ThreedAdminModel(Series series, ViewsSession viewsSession, RWValue<CommitHistory> commitHistory, Profiles profiles) {
        this.series = series;
        this.viewsSession = viewsSession;
        this.profiles = profiles;

        this.jpgGenClient = series.getApp().getJpgGenClient();

        this.threedModel = viewsSession.getThreedModel();
        this.seriesKey = threedModel.getSeriesKey();
        this.featureModel = threedModel.getFeatureModel();

        this.commitHistory = commitHistory;


        this.threedAdminClient = series.getApp().getThreedAdminClient();

        skins = initSkins();

        skin = new Value<Skin>(skins.get(0));

        this.currentUiPicks = new CurrentUiPicks(threedModel);

    }

    public void log(String msg) {
        series.log(msg);
    }

    public CurrentUiPicks getCurrentUiPicks() {
        return currentUiPicks;
    }

    public void generateJpgsButtonOnClick() {
        Profile profile = viewsSession.profile().get();
        profile.getBaseImageType();

        CommitKey commitKey = commitHistory.get().getCommitKey();
        if (commitKey == null) {
            throw new IllegalStateException("Cannot run jpgs with no commitKey");
        }
        SeriesId seriesId = new SeriesId(seriesKey, commitKey.getRootTreeId());
        profile.getBaseImageType();
        JobSpec jobSpec = new JobSpec(seriesId, profile);
        log("Starting jpg job...");
        jpgGenClient.startJpgJob(jobSpec).onSuccess = new SuccessCallback<Boolean>() {
            @Override
            public void call(Req<Boolean> request) {
                log("Jpg job started!");
            }
        };

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                JpgQueueMasterPanel d = new JpgQueueMasterPanel(jpgGenClient, getTabCreator());
                getTabCreator().addTab(d);
            }
        });

    }

    public TabCreator getTabCreator() {
        return series.getApp().getTabCreator();
    }


    public ImmutableList<Skin> getSkins() {
        return skins;
    }

    private ImmutableList<Skin> initSkins() {
        ArrayList<Skin> a = new ArrayList<Skin>();
        a.add(new BytMain());
        a.add(new BytSummary());
        a.add(new SimpleSkin());
        return ImmutableList.copyOf(a);
    }

    private Skin getSkinByName(String skinName) {
        for (Skin skin : getSkins()) {
            String simpleName = getSimpleName(skin);
            if (simpleName.equals(skinName)) {
                return skin;
            }
        }
        throw new IllegalArgumentException("no such skin: " + skinName);
    }


    public RWValue<Skin> skin() {
        return skin;
    }

    public Skin getCurrentSkin() {
        return skin.get();
    }

    public RWValue<Profile> profile() {
        return viewsSession.profile();
    }

    public Profiles getProfiles() {
        return profiles;
    }

    public ViewsSession getViewsSession() {
        return viewsSession;
    }


    public RWValue<ImageMode> imageMode() {
        return viewsSession.imageMode();
    }

    public void tagCommitButtonOnClick() {
        final CreateTagDialog createTagDialog = new CreateTagDialog(this);
        createTagDialog.center();
        createTagDialog.show();
    }

    public void vtcButtonOnClick() {
        boolean confirm = Window.confirm("Are you sure?");
        if (!confirm) return;
        makeVtcRemote();
    }

    public void setTag(final String newTagName) {
        if (commitHistory.get().isTagged()) {
            throw new IllegalStateException();
        }
        log("Creating tag[" + newTagName + "] ...");

        CommitKey commitKey = commitHistory.get().getCommitKey();
        if (commitKey == null) {
            throw new IllegalStateException();
        }
        Req<CommitHistory> r = threedAdminClient.tagCommit(seriesKey, newTagName, commitKey.getCommitId());
        r.onSuccess = new SuccessCallback<CommitHistory>() {
            @Override
            public void call(Req<CommitHistory> request) {
                log("Tag[" + newTagName + "] created!");
                commitHistory.set(request.result);
            }
        };

    }

    private void makeVtcRemote() {
        if (commitHistory.get().isVtc()) {
            throw new IllegalStateException();
        }

        log("Marking vtc...");
        CommitKey commitKey = commitHistory.get().getCommitKey();

        Req<CommitHistory> r = threedAdminClient.setVtc(seriesKey, commitKey);

        r.onSuccess = new SuccessCallback<CommitHistory>() {
            @Override
            public void call(Req<CommitHistory> request) {
                log("Marking vtc complete!");
                commitHistory.set(request.result);
            }
        };
    }

    public RWValue<CommitHistory> commitHistory() {
        return commitHistory;
    }

    public Series getSeries() {
        return series;
    }
}
