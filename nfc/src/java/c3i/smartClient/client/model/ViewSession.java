package c3i.smartClient.client.model;

import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.imageModel.shared.AngleKey;
import c3i.core.imageModel.shared.CacheAheadPolicy;
import c3i.core.imageModel.shared.CoreImageStack;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.ImageMode;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.RawImageStack;
import c3i.core.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.event.AngleChangeListener;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.widgets.dragToSpin.DragToSpinModel;
import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.futures.AsyncFunction;
import c3i.util.shared.futures.AsyncKeyValue;
import c3i.util.shared.futures.Completer;
import c3i.util.shared.futures.OnSuccess;
import c3i.util.shared.futures.RValue;
import c3i.util.shared.futures.RWValue;
import c3i.util.shared.futures.Value;
import com.google.common.base.Preconditions;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Timer;
import org.timepedia.exporter.client.Export;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.lang.shared.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class ViewSession implements DragToSpinModel, ViewModel {

    private static final String FIXED_VIEW_ERROR_MESSAGE = "This viewModel impl (ViewSession) represents a single, fixed view and cannot be changed at runtime.";
    //fixed init state
    @Nonnull private final ViewsSession viewsSession;
    @Nonnull private final ImView view;
    @Nonnull private final RWValue<Boolean> dragToSpin; // doesn't actually change

    //owned state
    @Nonnull private final RWValue<AngleKey> angle; //value @Nonnull

    //computed
    @Nonnull private final AsyncKeyValue<ImageStack.Key, ImageStack> imageStack;  //value @Nullable

    private Scheduler.ScheduledCommand refreshImageStackKeyCommand;

    private final LayerState layerState;

    private boolean scrollReverse = true;

    public ViewSession(@Nonnull ViewsSession parent, @Nonnull final ImView view) {
        Preconditions.checkNotNull(parent);
        Preconditions.checkNotNull(view);

        this.viewsSession = parent;
        this.view = view;
        this.imageStack = new AsyncKeyValue<ImageStack.Key, ImageStack>(createAsyncFunction());
        this.angle = new Value<AngleKey>(view.getInitialAngleKey());
        this.dragToSpin = new Value<Boolean>(view.isDragToSpin());

        layerState = new LayerState(this);

        this.viewsSession.picks.addChangeListener(new ChangeListener<FixedPicks>() {
            @Override
            public void onChange(FixedPicks newValue) {
                recalcImageStackAsync();
            }
        });
        this.viewsSession.imageMode.addChangeListener(new ChangeListener<ImageMode>() {
            @Override
            public void onChange(ImageMode newValue) {
                recalcImageStackAsync();
            }
        });
        this.viewsSession.profile.addChangeListener(new ChangeListener<Profile>() {
            @Override
            public void onChange(Profile newValue) {
                recalcImageStackAsync();
            }
        });

        this.angle.addChangeListener(new ChangeListener<AngleKey>() {
            @Override
            public void onChange(AngleKey newValue) {
                recalcImageStackAsync();
            }
        });

        layerState.addChangeListener(new ChangeListener<LayerState>() {
            @Override
            public void onChange(LayerState newValue) {
                if (imageMode().get().isPngMode()) {
                    recalcImageStackAsync();
                }
            }
        });

        addImageStackChangeListener(new ImageStackChangeListener() {
            @Override
            public void onChange(final ImageStack newImageStack) {
                //maybe do cache ahead

                final Scheduler.ScheduledCommand cmd = new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        if (newImageStack != null) {
                            FixedPicks fixedPicks = newImageStack.getFixedPicks();
                            if (fixedPicks != null && fixedPicks.isValidBuild()) {
                                AngleKey angleKey = newImageStack.getKey().getAngleKey();
                                if (!angleKey.getViewKey().equals(getViewKey())) {
                                    throw new IllegalStateException();
                                }
                                int currentAngle = angleKey.getAngle();
                                doCacheAhead(fixedPicks, currentAngle);
                            }
                        }
                    }
                };

                if (isActive()) {
                    Scheduler.get().scheduleDeferred(cmd);
                } else {
                    Timer timer = new Timer() {
                        @Override
                        public void run() {
                            Scheduler.get().scheduleDeferred(cmd);
                        }
                    };
                    timer.schedule(200);
                }
            }
        });

    }

    private ViewSession() {
        throw new UnsupportedOperationException("required by gwt export");
    }

    public boolean isActive() {
        return this == getParent().getViewSession();
    }

    private AsyncFunction<ImageStack.Key, ImageStack> createAsyncFunction() {
        return new AsyncFunction<ImageStack.Key, ImageStack>() {
            @Override
            public void start(ImageStack.Key key, Completer<ImageStack> completer) throws Exception {

                FixedPicks fixedPicks = key.getCoreKey().getRawKey().getFixedPicks();
                if (fixedPicks == null) {
                    completer.setResult(null);
                } else {

                    AngleKey angleKey = angle.get();
                    int a = angleKey.getAngle();
                    RawImageStack rawImageStack = view.getRawImageStack(fixedPicks, a);
                    CoreImageStack coreImageStack = rawImageStack.getCoreImageStack(viewsSession.profile.get(), viewsSession.imageMode.get());

                    ImageStack imageStack;
                    if (imageMode().get().isPngMode()) {
                        imageStack = new ImageStack(key, coreImageStack, layerState);
                    } else {
                        imageStack = new ImageStack(key, coreImageStack);
                    }

                    completer.setResult(imageStack);

                }
            }
        };
    }

    @Export
    public void angleNext() {
        if (scrollReverse) {
            int newValue = view.getPrevious(angle.get().getAngle());
            setAngle(newValue);
        } else {
            int newValue = view.getNext(angle.get().getAngle());
            setAngle(newValue);
        }
    }

    @Export
    public void anglePrevious() {
        if (scrollReverse) {
            int newValue = view.getNext(angle.get().getAngle());
            setAngle(newValue);
        } else {
            int newValue = view.getPrevious(angle.get().getAngle());
            setAngle(newValue);
        }
    }

    @Override
    public void setAngle(int newValue) {
        angle.set(new AngleKey(getViewKey(), newValue));
    }

    private void recalcImageStackAsync() {
        if (this.refreshImageStackKeyCommand == null) {
            refreshImageStackKeyCommand = new Scheduler.ScheduledCommand() {
                @Override
                public void execute() {
                    ImageStack.Key imageStackKey = getImageStackKey();
                    imageStack.setKey(imageStackKey);
                    refreshImageStackKeyCommand = null;
                }
            };
            Scheduler.get().scheduleFinally(refreshImageStackKeyCommand);
        }
    }

    @Nonnull
    public ImView getView() {
        return view;
    }

    @Export
    public int getAngle() {
        return angle.get().getAngle();
    }

    public Profile getProfile() {
        return viewsSession.profile.get();
    }

    @Export
    @Nullable
    public ImageStack getImageStack() {
        return imageStack.get();
    }

    @Nullable
    public FixedPicks getFixedPicks() {
        return viewsSession.picks.get();
    }

    public boolean isPngMode() {
        return imageMode().get().isPngMode();
    }

    public Path getRepoBaseURl() {
        return viewsSession.getRepoBaseUrl();
    }


    private class PrefetchRepeatingCommand implements Scheduler.RepeatingCommand {

        private int executeCount = 0;


        private final FixedPicks prefetchPicks;
        private final int prefetchCurrentAngle;
        private final ImageMode prefetchImageMode;
        private final Profile prefetchProfile;
        private final Path prefetchRepoBaseUrl;

        private CacheAheadPolicy.AngleList anglesToCache;

        PrefetchRepeatingCommand(final FixedPicks prefetchPicks, final int prefetchCurrentAngle) {
            Preconditions.checkNotNull(view);
            Preconditions.checkNotNull(prefetchPicks);
            this.prefetchPicks = prefetchPicks;
            this.prefetchCurrentAngle = prefetchCurrentAngle;
            this.prefetchImageMode = viewsSession.imageMode.get();
            this.prefetchProfile = viewsSession.profile.get();
            this.prefetchRepoBaseUrl = viewsSession.getRepoBaseUrl();
        }

        private ImageStack.Key getPrefetchImageStackKey(final int angleToCache) {
            AngleKey angleKeyToCache = new AngleKey(getViewKey(), angleToCache);
            RawImageStack.Key k1 = new RawImageStack.Key(angleKeyToCache, prefetchPicks);
            CoreImageStack.Key k2 = new CoreImageStack.Key(k1, prefetchProfile, prefetchImageMode);
            return new ImageStack.Key(prefetchRepoBaseUrl, k2);
        }

        @Override
        public boolean execute() {
            executeCount++;
//            Console.log("executeCount = " + executeCount + " " + ViewSession.this.toString());

            if (anglesToCache == null) {
                if (prefetchPicks == null) {
                    return false;
                } else {
                    CacheAheadPolicy cacheAheadPolicy = view.getCacheAheadPolicy();
                    this.anglesToCache = cacheAheadPolicy.getAnglesToCache(prefetchCurrentAngle);
                    return true;
                }
            } else {
                if (anglesToCache.size() > 100) throw new IllegalStateException();
                if (anglesToCache.size() == 0) {
                    return false;
                } else {
                    Integer nextAngleToCache = anglesToCache.remove(0);
                    if (nextAngleToCache == null) throw new IllegalStateException();
                    if (nextAngleToCache <= 0) throw new IllegalStateException();
                    ImageStack.Key k = getPrefetchImageStackKey(nextAngleToCache);
                    CoreImageStack coreImageStack = view.getCoreImageStack(k.getCoreKey());
                    new ImageStack(k, coreImageStack);
                    //automatically starts loading
                    return true;
                }
            }
        }


    }

    private void doCacheAhead(final FixedPicks fixedPicks, final int currentAngle) {
        Console.log("doCacheAhead " + toString());
        PrefetchRepeatingCommand cmd = new PrefetchRepeatingCommand(fixedPicks, currentAngle);
        Scheduler.get().scheduleIncremental(cmd);
    }

    @Export
    @Override
    public void addImageStackChangeListener(ImageStackChangeListener listener) {
        imageStack.addChangeListener(listener);
    }

    /**
     * Same as addImageStackChangeListener except the listener isn't called until the imageStack is fully loaded.
     */
    @Export
    @Override
    public void addImageStackChangeListener2(final ImageStackChangeListener listener) {
        addImageStackChangeListener(new ImageStackChangeListener() {
            @Override
            public void onChange(ImageStack newValue) {
                newValue.ensureLoaded().success(new OnSuccess<ImageStack>() {
                    @Override
                    public void onSuccess(@Nonnull ImageStack result) {
                        listener.onChange(result);
                    }
                });
            }
        });
    }

    @Override
    public void removeImageStackChangeListener(ImageStackChangeListener listener) {
        imageStack.removeChangeListener(listener);
    }

    public void addAngleChangeListener(AngleChangeListener listener) {
        angle.addChangeListener(listener);
    }


    public void removeAngleChangeListener(AngleChangeListener listener) {
        angle.removeChangeListener(listener);
    }

    public RValue<Profile> profile() {
        return viewsSession.profile;
    }

    public RValue<ImageMode> imageMode() {
        return viewsSession.imageMode;
    }

    @Export
    public int getViewIndex() {
        return view.getIndex();
    }

    @Export
    public boolean isDragToSpin() {
        return dragToSpin().get();
    }

    @Export
    public void setDragToSpin(boolean dragToSpin) {
        this.dragToSpin.set(dragToSpin);
    }

    public ViewsSession getParent() {
        return viewsSession;
    }

    public boolean isSelected() {
        return this == viewsSession.getViewSession();
    }

    public ViewKey getViewKey() {
        return view.getViewKey();
    }

    public AngleKey getAngleKey() {
        return angle.get();
    }

//    public void forceImageStackChangeEvent() {
//        imageStack.forceFireValueChange();
//    }

    @Override
    public String toString() {
        return "ViewSession[" + getViewKey() + "]";
    }

    private ImageStack.Key getImageStackKey() {
        return getImageStackKey(angle.get());
    }

    private ImageStack.Key getImageStackKey(AngleKey angleKey) {
        if (viewsSession.picks.get() == null) {
            Console.log("picks are null");
            return null;
        }

        RawImageStack.Key rawKey = new RawImageStack.Key(angleKey, viewsSession.picks.get());
        CoreImageStack.Key coreKey = new CoreImageStack.Key(rawKey, viewsSession.profile.get(), viewsSession.imageMode.get());

        return new ImageStack.Key(viewsSession.getRepoBaseUrl(), coreKey);
    }

    public LayerState getLayerState() {
        return layerState;
    }


    @Override
    public void addViewChangeListener(ViewChangeListener listener) {
        throw new UnsupportedOperationException(FIXED_VIEW_ERROR_MESSAGE);
    }

    @Override
    public void removeViewChangeListener(ViewChangeListener listener) {
        throw new UnsupportedOperationException(FIXED_VIEW_ERROR_MESSAGE);
    }

    @Override
    public RValue<Boolean> dragToSpin() {
        return dragToSpin; //drag to spin doesn't actually change for this impl
    }

    @Override
    public void setViewIndex(int newViewIndex) {
        throw new UnsupportedOperationException(FIXED_VIEW_ERROR_MESSAGE);
    }

    @Override
    public List<ImView> getViews() {
        throw new UnsupportedOperationException(FIXED_VIEW_ERROR_MESSAGE);
    }

    @Override
    public List<? extends ViewModel> getViewModels() {
        throw new UnsupportedOperationException(FIXED_VIEW_ERROR_MESSAGE);
    }

    @Override
    public ViewModel getViewModel(int viewIndex) {
        throw new UnsupportedOperationException(FIXED_VIEW_ERROR_MESSAGE);
    }

    @Override
    public void addLayerStateListener(ChangeListener<LayerState> l) {
        layerState.addChangeListener(l);
    }
}