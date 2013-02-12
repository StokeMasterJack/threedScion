package c3i.smartClient.client.model;

import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageMode;
import c3i.imageModel.shared.Profile;
import c3i.smartClient.client.model.event.AngleChangeListener;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.widgets.dragToSpin.DragToSpinModel;
import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.futures.RValue;

import java.util.List;

public interface ViewModel extends DragToSpinModel {

    RValue<Profile> profile();

    RValue<ImageMode> imageMode();

    void addImageStackChangeListener(ImageStackChangeListener listener);

    /**
     * Same as addImageStackChangeListener except the listener isn't called until the imageStack is fully loaded.
     */
    void addImageStackChangeListener2(ImageStackChangeListener listener);

    void removeImageStackChangeListener(ImageStackChangeListener listener);

    ImageStack getImageStack();

    ImView getView();

    void setViewIndex(int value);

    int getViewIndex();
    int getAngle();

    List<ImView> getViews();

    ViewModel getViewModel(int viewIndex);

    List<? extends ViewModel> getViewModels();

    void addViewChangeListener(ViewChangeListener listener);

    void removeViewChangeListener(ViewChangeListener listener);

    void addLayerStateListener(ChangeListener<LayerState> l);

    void addAngleChangeListener(AngleChangeListener listener);
}
