package c3i.smartClient.client.model;

import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.AssignmentException;
import c3i.core.imageModel.shared.AngleKey;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.ImageMode;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.ViewKey;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.smartClient.client.model.event.AngleChangeListener;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.widgets.dragToSpin.DragToSpinModel;
import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.events.ValueChangeTopic;
import c3i.util.shared.futures.OnSuccess;
import c3i.util.shared.futures.RValue;
import c3i.util.shared.futures.RWValue;
import c3i.util.shared.futures.Value;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import smartsoft.util.shared.Path;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ViewsSession implements DragToSpinModel, ViewModel {

    private final ValueChangeTopic<ImageStack> imageStackChange = new ValueChangeTopic<ImageStack>("ImageStackChangeTopic");
    private final ValueChangeTopic<LayerState> layerStateChange = new ValueChangeTopic<LayerState>("LayerStateChangeTopic");
    private final ValueChangeTopic<AngleKey> angleChange = new ValueChangeTopic<AngleKey>("AngleChangeTopic");

    //fixed init state
    private final Path repoBaseUrl;
    private final ThreedModel threedModel;


    final RValue<FixedPicks> picks;
    final RWValue<ImageMode> imageMode;
    final RWValue<Profile> profile;

    final RWValue<Boolean> dragToSpin;

    //index is the viewIndex
    private final ImmutableList<ViewSession> viewSessions;

    private final Value<ViewKey> viewKey;

    @Nonnull
    private ViewSession viewSession; //current (aka selected aka main) view


    public ViewsSession(final Path repoBaseUrl, final ThreedModel threedModel, Profile initialProfile, RValue<FixedPicks> picks) {

        Preconditions.checkNotNull(initialProfile);
        initialProfile.getBaseImageType();

        this.repoBaseUrl = repoBaseUrl;
        this.threedModel = threedModel;

        this.picks = picks;

        this.picks.addChangeListener(new ChangeListener<FixedPicks>() {
            @Override
            public void onChange(FixedPicks newValue) {
                if (newValue.isInvalidBuild()) {
                    AssignmentException ex = newValue.getException();
                    log.log(Level.SEVERE, "Invalid Picks: " + ex.getMessage());
                }
            }
        });

        imageMode = new Value<ImageMode>("imageMode");
        profile = new Value<Profile>("profile", initialProfile);


        ImmutableList.Builder<ViewSession> builder = ImmutableList.builder();
        for (int i = 0; i < threedModel.getViewCount(); i++) {
            ImView view = threedModel.getView(i);
            assert i == view.getIndex();
            ViewSession viewSession = new ViewSession(this, view);
            builder.add(viewSession);
        }
        viewSessions = builder.build();

        profile.set(initialProfile);
        imageMode.set(ImageMode.JPG);


        viewSession = getViewSession(threedModel.getInitialViewIndex());
        viewKey = new Value<ViewKey>("viewKey", viewSession.getViewKey());

        dragToSpin = new Value<Boolean>("dragToSpin", viewSession.isDragToSpin());

        for (ViewSession viewSession : viewSessions) {

            viewSession.addImageStackChangeListener(new ImageStackChangeListener() {
                @Override
                public void onChange(ImageStack newImageStack) {
                    ViewKey imageStackViewKey = newImageStack.getViewKey();
                    ViewKey currentViewKey = getViewKey();
                    boolean isCurrentView = imageStackViewKey.equals(currentViewKey);
                    if (isCurrentView) {
                        imageStackChange.fire(newImageStack);
                    }
                }
            });

            viewSession.addAngleChangeListener(new AngleChangeListener() {
                @Override
                public void onChange(AngleKey newAngle) {
                    boolean isCurrentView = newAngle.getViewKey().equals(getViewKey());
                    if (isCurrentView) {
                        angleChange.fire(newAngle);
                    }
                }
            });

            viewSession.addLayerStateListener(new ChangeListener<LayerState>() {
                @Override
                public void onChange(LayerState newValue) {
                    ViewKey imageStackViewKey = newValue.getViewKey();
                    ViewKey currentViewKey = getViewKey();
                    boolean isCurrentView = imageStackViewKey.equals(currentViewKey);
                    if (isCurrentView) {
                        layerStateChange.fire(newValue);
                    }
                }
            });


        }

        viewKey.addChangeListener(new ChangeListener<ViewKey>() {
            @Override
            public void onChange(ViewKey newValue) {
                imageStackChange.fire(viewSession.getImageStack());
                dragToSpin.set(viewSession.isDragToSpin());
            }
        });


    }


    public void anglePrevious() {
        viewSession.anglePrevious();
    }

    public void angleNext() {
        viewSession.angleNext();
    }


    public int getAngle() {
        return viewSession.getAngle();
    }

    @Override
    public String toString() {
        return "[ViewsSession] " + threedModel.getSeriesKey() + "";
    }

    public int getViewCount() {
        return threedModel.getImageModel().getViewCount();
    }

    public int getViewIndex() {
        return viewSession.getViewIndex();
    }

    public ImView getView() {
        return viewSession.getView();
    }

    @Nonnull
    public ViewSession getViewSession() {
        return viewSession;
    }

    public ViewSession getViewSession(int viewIndex) {
        return viewSessions.get(viewIndex);
    }

    public ViewSession getViewSession(ViewKey viewKey) {
        return viewSessions.get(viewKey.getViewIndex());
    }

    public ViewSession getViewSession(String viewName) {
        ImView view = threedModel.getView(viewName);
        return viewSessions.get(view.getIndex());
    }

    public void setImageMode(ImageMode imageMode) {
        this.imageMode.set(imageMode);
    }

    public ImageMode getImageMode() {
        return imageMode.get();
    }

    public ImmutableList<ViewSession> getViewSessions() {
        return viewSessions;
    }

    public int getInitialViewIndex() {
        return threedModel.getInitialViewIndex();
    }


    public ThreedModel getThreedModel() {
        return threedModel;
    }

    public List<ImView> getViews() {
        return threedModel.getViews();
    }

    @Override
    public List<? extends ViewModel> getViewModels() {
        return viewSessions;
    }

    public ViewKey getViewKey() {
        return viewSession.getViewKey();
    }


    public void setViewIndex(int newValue) {
        ViewKey newViewKey = threedModel.getView(newValue).getViewKey();
        viewSession = getViewSession(newValue);
        viewKey.set(newViewKey);
    }

    public void setView(String viewName) {
        System.out.println("ViewsSession.setView");
        ViewKey newViewKey = threedModel.getView(viewName).getViewKey();
        viewSession = getViewSession(viewName);
        viewKey.set(newViewKey);
    }

    public AngleKey getAngleKey() {
        return viewSession.getAngleKey();
    }

    public void addViewChangeListener(ViewChangeListener listener) {
        viewKey.addChangeListener(listener);
    }

    public void removeViewChangeListener(ViewChangeListener listener) {
        viewKey.removeChangeListener(listener);
    }


    public Path getRepoBaseUrl() {
        return repoBaseUrl;
    }

    public RValue<FixedPicks> fixedPicks() {
        return picks;
    }

    public RWValue<Profile> profile() {
        return profile;
    }

    public RWValue<ImageMode> imageMode() {
        return imageMode;
    }

    public RWValue<Boolean> dragToSpin() {
        return dragToSpin;
    }

//    public void forceImageStackChangeEvent() {
//        for (ViewSession viewSession : viewSessions) {
//            viewSession.forceImageStackChangeEvent();
//        }
//    }

    public boolean isPngMode() {
        return imageMode.get().isPngMode();
    }

    public void setAngle(int angle) {
        viewSession.setAngle(angle);
    }


    @Override
    public void addLayerStateListener(ChangeListener<LayerState> listener) {
        layerStateChange.add(listener);
    }

    public void addImageStackChangeListener(ImageStackChangeListener listener) {
        imageStackChange.add(listener);
    }

    /** Same as addImageStackChangeListener except the listener isn't called until the imageStack is fully loaded. */
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

    public void addAngleChangeListener(AngleChangeListener listener) {
        angleChange.add(listener);
    }

    public void removeImageStackChangeListener(ImageStackChangeListener listener) {
        imageStackChange.remove(listener);
    }

    public void removeAngleChangeListener(AngleChangeListener listener) {
        angleChange.remove(listener);
    }

    @Override
    public ImageStack getImageStack() {
        return viewSession.getImageStack();
    }

    @Override
    public ViewModel getViewModel(int viewIndex) {
        return getViewSession(viewIndex);
    }

    public boolean isValidViewName(String viewName) {
        return threedModel.getImageModel().isValidViewName(viewName);
    }

    private static Logger log = Logger.getLogger(ViewsSession.class.getName());
}
