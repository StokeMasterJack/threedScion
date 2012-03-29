package smartClient.client;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.gwt.core.client.Scheduler;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.imageModel.shared.*;
import com.tms.threed.threedCore.imageModel.shared.slice.SimplePicks;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import smartClient.client.util.DoOnceScheduler;
import smartClient.client.util.futures.OnComplete;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.events3.ChangeEvent;
import smartsoft.util.gwt.client.events3.ChangeListener;
import smartsoft.util.gwt.client.events3.ChangeTopic;
import smartsoft.util.lang.shared.Path;

import java.util.ArrayList;

public class ViewSession implements Exportable {

    private final ChangeTopic<ViewSession, ImageBatch> imageChange1 = new ChangeTopic(this);
    private final ChangeTopic<ViewSession, ImageBatch> imageChange2 = new ChangeTopic(this);
    private final ChangeTopic<ViewSession, Integer> angleChange = new ChangeTopic(this);

    private final ThreedModel threedModel;
    private final ImView view;
    private final Profile profile;

    private final ImageModeSession imageModeSession;
    private final PicksSession picksSession;

    private int currentAngle;

    private ImmutableList<Path> imageUrls;
    private ImageBatch imageBatch;

    private boolean visible;

    public ViewSession(ThreedModel threedModel, ImView view, Profile profile, ImageModeSession imageModeSession, PicksSession picksSession, boolean visible) {
        Preconditions.checkNotNull(threedModel);
        Preconditions.checkNotNull(view);
        Preconditions.checkNotNull(profile);
        Preconditions.checkNotNull(imageModeSession);
        Preconditions.checkNotNull(picksSession);
        this.threedModel = threedModel;
        this.view = view;
        this.profile = profile;
        this.imageModeSession = imageModeSession;
        this.picksSession = picksSession;
        currentAngle = view.getViewKey().getInitialAngle();
        this.visible = visible;
        System.out.println("visible = " + visible);

        imageModeSession.addImageModeChangeListener(new ChangeListener<ImageModeSession, ImageMode>() {
            @Override
            public void onEvent(ChangeEvent<ImageModeSession, ImageMode> ev) {
                recalcImageListFinally();
            }
        });

        picksSession.addChangeListener(new ChangeListener<PicksSession, FixResult>() {
            @Override
            public void onEvent(ChangeEvent<PicksSession, FixResult> ev) {
                recalcImageListFinally();
            }
        });


    }

    public void setCurrentAngle(int currentAngle) {
        setCurrentAngleInternal(currentAngle);
    }

    @Export
    public int getCurrentAngle() {
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
        doOnceScheduler.maybeSchedule(recalcImageListCommand);
    }

    private final class RecalcImageList implements Scheduler.ScheduledCommand {
        @Override
        public void execute() {
            recalcImageList();
        }

        @Override
        public boolean equals(Object obj) {
            return obj.getClass() == RecalcImageList.class;
        }

        @Override
        public int hashCode() {
            return RecalcImageList.class.hashCode();
        }
    }

    private final RecalcImageList recalcImageListCommand = new RecalcImageList();

    private void recalcImageList() {
        SimplePicks picks = picksSession.getSimplePicks();
        if (picks == null || !visible) {
            imageUrls = null;
            return;
        }


        ImageStack imageStack = view.getImageStack(picks, currentAngle);

        ImageMode imageMode = imageModeSession.getImageMode();
        ImmutableList<Path> newImageUrls;
        if (imageMode.equals(ImageMode.JPG)) {
            newImageUrls = imageStack.getUrlsJpgMode(profile.getJpgWidth(), true);
        } else if (imageMode.equals(ImageMode.JPG_SKIP_Z_LAYERS)) {
            newImageUrls = imageStack.getUrlsJpgMode(profile.getJpgWidth(), false);
        } else if (imageMode.equals(ImageMode.PNG)) {
            newImageUrls = imageStack.getUrlsPngMode(profile.getJpgWidth());

            Console.log("newImageUrls for [" + picks + " " + view.getName() + "(" + visible + ") " + currentAngle + "]");
            for (Path newImageUrl : newImageUrls) {
                Console.log("\t" + newImageUrl);
            }


        } else {
            throw new IllegalStateException();
        }

        setImageUrls(newImageUrls);

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                doCacheAhead();
            }
        });
    }


    private void setImageUrls(ImmutableList<Path> newValue) {

        ImmutableList<Path> oldValue = this.imageUrls;
        if (!Objects.equal(oldValue, newValue)) {
            this.imageUrls = newValue;
            imageBatch = new ImageBatch(newValue);
            imageChange1.fire(imageBatch);

            imageBatch.ensureLoaded().complete(new OnComplete() {
                @Override
                public void call() {
                    imageChange2.fire(imageBatch);
                }
            });
        }
    }

    @Export
    public void addImageChangeListener1(ImageChangeListener listener) {
        imageChange1.addListener(listener);
    }

    @Export
    public void addImageChangeListener2(ImageChangeListener listener) {
        imageChange2.addListener(listener);
    }

    public void addAngleChangeListener(ChangeListener<ViewSession, Integer> listener) {
        angleChange.addListener(listener);
    }

    @Export
    public ImageBatch getImageBatch() {
        return imageBatch;
    }


    public void doCacheAhead() {
        CacheAheadPolicy cacheAheadPolicy = view.getCacheAheadPolicy();

        if (cacheAheadPolicy.isNoCacheAhead()) {
            return;
        }


        if (imageModeSession.isPngMode()) {
            return;
        }


        SimplePicks picks = picksSession.getSimplePicks();
        if (picks == null) {
            return;
        }


        ImageMode imageMode = imageModeSession.getImageMode();

        boolean includeZLayers = imageMode.equals(ImageMode.JPG);
        JpgWidth jpgWidth = profile.getJpgWidth();

        ArrayList<Integer> anglesToCache = new ArrayList<Integer>();
        if (cacheAheadPolicy instanceof NumAnglesBothEitherSide) {
            NumAnglesBothEitherSide policy = (NumAnglesBothEitherSide) cacheAheadPolicy;
            int num = policy.getNumberOfAnglesToCache();

            if (num == 0) {
                return;
            }

            int angleToCache;
            angleToCache = currentAngle;
            for (int i = 0; i < num; i++) {
                angleToCache = view.getNext(angleToCache);
                if (anglesToCache.contains(angleToCache)) {
                    throw new IllegalStateException(anglesToCache + "");
                }
                anglesToCache.add(angleToCache);
            }

            angleToCache = currentAngle;
            for (int i = 0; i < num; i++) {
                angleToCache = view.getPrevious(angleToCache);
                if (anglesToCache.contains(angleToCache)) {
                    throw new IllegalStateException(anglesToCache + "");
                }
                anglesToCache.add(angleToCache);
            }


            ArrayList<Path> urlsToPrefetch = new ArrayList<Path>();
            for (Integer angle : anglesToCache) {
                ImageStack imageStack = view.getImageStack(picks, angle);
                ImmutableList<Path> urls = imageStack.getUrlsJpgMode(jpgWidth, includeZLayers);
                for (Path url : urls) {
                    urlsToPrefetch.add(url);
                }
            }

            new Prefetcher(urlsToPrefetch);

        } else if (cacheAheadPolicy instanceof AllAngles) {
            throw new UnsupportedOperationException();
        }


    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean newValue) {
        boolean oldValue = this.visible;
        if (oldValue != newValue) {
            recalcImageListFinally();
        }

    }
}
