package smartClient.client;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gwt.core.client.Scheduler;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.imageModel.shared.ImView;
import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import smartsoft.util.gwt.client.events3.ChangeListener;
import smartsoft.util.gwt.client.events3.ChangeTopic;
import smartsoft.util.lang.shared.Path;

public class ViewSession {

    private final ChangeTopic<ViewSession, ImageBatch> imageChange1 = new ChangeTopic(this);
    private final ChangeTopic<ViewSession, ImageBatch> imageChange2 = new ChangeTopic(this);
    private final ChangeTopic<ViewSession, Integer> angleChange = new ChangeTopic(this);

    private final ThreedModel threedModel;
    private final ImView view;
    private final Profile profile;

    private FixResult fixedPicks;
    private int currentAngle;

    private ImmutableList<Path> imageUrls;
    private ImageBatch imageBatch;

    public ViewSession(final ThreedModel threedModel, final ImView view, final Profile profile) {
        Preconditions.checkNotNull(threedModel);
        Preconditions.checkNotNull(view);
        Preconditions.checkNotNull(profile);
        this.threedModel = threedModel;
        this.view = view;
        this.profile = profile;
        currentAngle = view.getViewKey().getInitialAngle();
    }

    /**
     * copy
     */
    ViewSession(final ViewSession source) {
        this.threedModel = source.threedModel;
        this.view = source.view;
        this.currentAngle = source.currentAngle;
        this.profile = source.profile;
    }


    public void setAngle(int currentAngle) {
        setCurrentAngleInternal(currentAngle);
    }

    public void setFixedPicks(FixResult newValue) {
        if (this.fixedPicks != newValue) {
            this.fixedPicks = newValue;
            recalcImageListFinally();
        }
    }

    public int getAngle() {
        return currentAngle;
    }

    public Profile getProfile() {
        return profile;
    }

    public ThreedModel getThreedModel() {
        return threedModel;
    }


    public void previousAngle() {
        setCurrentAngleInternal(view.getPrevious(currentAngle));
    }

    public void nextAngle() {
        setCurrentAngleInternal(view.getNext(currentAngle));
    }

    private void setCurrentAngleInternal(int newValue) {
        int oldValue = this.currentAngle;
        if (oldValue != newValue) {
            this.currentAngle = newValue;
            recalcImageListFinally();
            angleChange.fire(oldValue, newValue);
        }
    }

    public ImView getView() {
        return view;
    }

    private final DoOnceScheduler doOnceScheduler = new DoOnceScheduler();

    private void recalcImageListFinally() {
        doOnceScheduler.schedule(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                recalcImageList();
            }
        });
    }

    private void recalcImageList() {
        if (fixedPicks == null) {
            imageUrls = null;
        } else {
            ImageStack imageStack = view.getImageStack(fixedPicks, currentAngle);
            ImmutableList<Path> newImageUrls = imageStack.getUrlListSmart(profile.getJpgWidth());
            setImageUrls(newImageUrls);
        }
    }

    private void setImageUrls(ImmutableList<Path> newValue) {

        ImmutableList<Path> oldValue = this.imageUrls;
        if (!Objects.equal(oldValue, newValue)) {
            this.imageUrls = newValue;
            imageBatch = new ImageBatch(newValue);
            imageChange1.fire(imageBatch);
            imageBatch.getLoadFuture().complete(new OnComplete() {
                @Override
                public void call() {
                    imageChange2.fire(imageBatch);
                }
            });
        }
    }

    public void addImageChangeListener1(ChangeListener<ViewSession, ImageBatch> listener) {
        imageChange1.addListener(listener);
    }

    public void addImageChangeListener2(ChangeListener<ViewSession, ImageBatch> listener) {
        imageChange2.addListener(listener);
    }

    public void addAngleChangeListener(ChangeListener<ViewSession, Integer> listener) {
        angleChange.addListener(listener);
    }

    public ImageBatch getImageBatch() {
        return imageBatch;
    }
}
