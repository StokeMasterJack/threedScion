package c3i.smartClient.client.model;

import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageMode;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.Slice;
import c3i.smartClient.client.model.event.AngleChangeListener;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.widgets.ViewPanel;
import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.futures.RValue;
import c3i.util.shared.futures.RWValue;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Node;
import org.timepedia.exporter.client.Export;
import org.timepedia.exporter.client.Exportable;
import org.timepedia.exporter.client.NoExport;
import smartsoft.util.gwt.client.Browser;
import smartsoft.util.shared.Path;

import java.util.List;

import static smartsoft.util.shared.Strings.isEmpty;

/**
 * A threedSession maintains current values for user picks, view and angle.
 * Changing any of these values will ultimately trigger a recalculation of image urls,
 * and an ImageStack change event. <br/>
 * <br/>
 * 3D API client's should listen for ImageStack change events and update the image accordingly.
 * For 3D Widget clients, the image will automatically update as needed.<br/>
 *
 * <br/>
 * The following methods will trigger the current configured 3D image to change:<br/>
 * <br/>
 * <code>
 *      setPicks(..)<br/>
 *      setView(..)<br/>
 *      setAngle(..)<br/>
 *      angleNext(..)<br/>
 *      anglePrevious(..)<br/>
 * </code>
 *
 * <br/>
 * Note: a threedSession also maintains the current angle for <i>each individual view</i>, so that when the user changes from
 * exterior view to interior view and back to exterior view, the user's angle is preserved.<br/>
 */
@Export
public class ThreedSession implements ViewModel, Exportable {

    private final ViewsSession viewsSession;

    private final PicksSession picksSession;

    @NoExport
    public ThreedSession(final Path repoBaseUrl, final ThreedModel threedModel, Profile initialProfile) {
        picksSession = new PicksSession(threedModel);
        this.viewsSession = new ViewsSession(repoBaseUrl, threedModel, initialProfile, picksSession);
    }

    @NoExport
    public void setPicksRaw(final Iterable<String> picksRaw) {
        picksSession.setPicksRaw(picksRaw);
    }

    @NoExport
    public void setPicksFixed(final FixedPicks fixResult) {
        picksSession.set(fixResult);
    }

    /**
     * Sets the features to be included in configured 3D image.
     * Changing the picks will cause the configured 3D image to change.<br/>
     * <br/>
     * The <b>picks</b> parameter is an array of feature codes as defined in model.xml
     */
    @Export
    public void setPicks(String[] picks) {
        ImmutableSet.Builder<String> builder = ImmutableSet.builder();
        for (String varCode : picks) {
            builder.add(varCode);
        }
        setPicksRaw(builder.build());
    }

    @NoExport
    public ImView getView() {
        return viewsSession.getViewSession().getView();
    }

    /**
     * returns the current viewName, ex: "exterior","interior"
     */
    @Export
    public String getViewName() {
        return getView().getName();
    }

    @Export
    @Override
    public String toString() {
        return viewsSession.toString();
    }

    @NoExport
    public ImageMode getImageMode() {
        return viewsSession.getImageMode();
    }

    /**
     *
     * The two valid imageModes are 'JPG' or 'PNG' (JPG is the default). <br/>
     *
     * <br/>
     *
     * PNG mode is used within the admin-tool for testing purposes.
     * It loads each layer png and combines them on the browser.<br/>
     *
     */
    @Export
    public void setImageMode(String imageModeString) {
        Preconditions.checkNotNull(imageModeString);
        ImageMode imageMode = ImageMode.valueOf(imageModeString.toUpperCase());
        setImageMode(imageMode);
    }

    @NoExport
    public void setImageMode(ImageMode imageMode) {
        viewsSession.setImageMode(imageMode);
    }

    @NoExport
    public ViewsSession getViewsSession() {
        return viewsSession;
    }

    @NoExport
    public List<ImView> getViews() {
        return viewsSession.getViews();
    }

    /**
     * Specifies which view (interior, exterior, cargo, etc.) to display.<br/>
     * <br/>
     * 0: Exterior view<br/>
     * 1: Interior view<br/>
     * 2: 3rd view (cargo, undercarriage) as defined in 3d model<br/>
     * <br/>
     * Changing the view will cause the configured 3D image to change.<br/>
     * <br/>
     *
     */
    @Export
    public void setView(int viewIndex) {
        viewsSession.setViewIndex(viewIndex);
    }

    /**
     * Same as setView
     */
    public void setViewIndex(int viewIndex) {
        viewsSession.setViewIndex(viewIndex);
    }

    @NoExport
    public ViewModel getViewSession() {
        return viewsSession.getViewSession();
    }

    @NoExport
    public ViewModel getViewSession(int viewIndex) {
        return viewsSession.getViewSession(viewIndex);
    }

    @NoExport
    public ViewModel getViewModel() {
        return viewsSession;
    }

    @Export
    public ViewModel getViewModel(int viewIndex) {
        return viewsSession.getViewModel(viewIndex);
    }

    @NoExport
    public RWValue<Profile> profile() {
        return viewsSession.profile();
    }

    @NoExport
    public RValue<ImageMode> imageMode() {
        return viewsSession.imageMode();
    }

    @Export
    public int getViewIndex() {
        return viewsSession.getViewIndex();
    }

    /**
     * Notifies listener when the current view is changed.
     */
    @Export
    public void addViewChangeListener(ViewChangeListener listener) {
        viewsSession.addViewChangeListener(listener);
    }

    @Export
    public void removeViewChangeListener(ViewChangeListener listener) {
        viewsSession.removeViewChangeListener(listener);
    }

    @NoExport
    public ThreedModel getThreedModel() {
        return viewsSession.getThreedModel();
    }

    @Export
    public Path getRepoBaseUrl() {
        return viewsSession.getRepoBaseUrl();
    }

    @Export
    public int getViewCount() {
        return getViewsSession().getViewCount();
    }

    @NoExport
    public ImmutableList<ViewSession> getViewSessions() {
        return viewsSession.getViewSessions();
    }

    @NoExport
    public Slice getCurrentSlice() {
        String viewName = getView().getName();
        int a = viewsSession.getViewSession().getAngle();
        return new Slice(viewName, a);
    }

    /**
     * Notifies listener when the configured 3D image changes. This happens after the picks, view or angle is changed.
     * This event will never be fired more than once per event loop. This means that if the
     * client calls setPicks, setView and setAngle all in succession this event will only fire once.
     *
     * <br/><br/>
     *
     * This is where the ui will refresh the image.
     *
     * <br/><br/>
     *
     * If using the ViewPanel widget, this is handled automatically and the client typically can ignore this event.
     */
    @Override
    public void addImageStackChangeListener(ImageStackChangeListener listener) {
        viewsSession.addImageStackChangeListener(listener);
    }

    @Override
    public void removeImageStackChangeListener(ImageStackChangeListener listener) {
        viewsSession.removeImageStackChangeListener(listener);
    }

    /**
     * Same as addImageStackChangeListener except the listener isn't called until the imageStack is fully loaded.
     */
    @Override
    public void addImageStackChangeListener2(final ImageStackChangeListener listener) {
        viewsSession.addImageStackChangeListener2(listener);
    }

    /**
     * returns the image(s) required to display the current picks/view/angle.
     * The returned imageStack reflects only a single angle.
     */
    @Override
    public ImageStack getImageStack() {
        return viewsSession.getImageStack();
    }

    /**
     * This is used to create multiple side-by-side ViewPanels (each with its own model). Rather than a single ViewPanel, whose view can change.
     * The viewPanels returned by this method are fixed to a single, fixed view. An example is the byt summary page.
     */
    @Override
    public List<? extends ViewModel> getViewModels() {
        return viewsSession.getViewModels();
    }


    /**
     * Sets the image angle to be displayed in configured 3D image.<br/>
     * <br/>
     * Changing the angle will cause the configured 3D image to change.<br/>
     * <br/>
     * angle a number between 1 and maxAngle inclusive. Where max angle is defined by the 3D model by the 3D admin.
     */
    @Override
    public void setAngle(int angle) {
        viewsSession.setAngle(angle);
    }

    /**
     * Increments the current angle<br/>
     * <br/>
     * Changing the angle will cause the configured 3D image to change.<br/>
     */
    @Override
    public void angleNext() {
        viewsSession.angleNext();
    }

    /**
     * Decrements the current angle<br/>
     * <br/>
     * Changing the angle will cause the configured 3D image to change.<br/>
     */
    @Override
    public void anglePrevious() {
        viewsSession.anglePrevious();
    }

    @NoExport
    @Override
    public RValue<Boolean> dragToSpin() {
        return viewsSession.dragToSpin();
    }

    /**
     * Searches current html page for html element with class ViewPanel and turns it into a
     * configured 3D image (with drag-to-spin and swipe-to-spin) that updates its image any
     * time the view, angle or picks change.<br/>
     */
    @Export
    public void scan() {
        scanInternal(this);
    }

    @NoExport
    public native static Node querySelector(String selectorExpression)/*-{
        return $wnd.document.querySelector(selectorExpression);
    }-*/;

    @NoExport
    public native static JsArray<Node> querySelectorAll(String selectorExpression)/*-{
        return $wnd.document.querySelectorAll(selectorExpression);
    }-*/;

    @NoExport
    private static void scanInternal(ThreedSession threedSession) {
        JsArray<Node> a = getViewPanelElements();
        for (int i = 0; i < a.length(); i++) {
            com.google.gwt.dom.client.Element element = a.get(i).cast();

            String sViewIndex = element.getAttribute("viewIndex");
            Integer viewIndex;
            if (isEmpty(sViewIndex)) {
                viewIndex = null;
            } else {
                viewIndex = Integer.parseInt(sViewIndex);
            }

            ViewPanel vp = new ViewPanel(element, threedSession, viewIndex, null);

        }
    }

    private static JsArray<Node> getViewPanelElements() {
        if (Browser.isIe()) {
            return getElementsByClassName(Document.get().getBody(), "ViewPanel");
        } else {
            return querySelectorAll(".ViewPanel");
        }
    }


    private native static JsArray<Node> getElementsByClassName(Node node, String className)/*-{
        var a = [];
        var re = new RegExp('(^| )' + className + '( |$)');
        var els = node.getElementsByTagName("*");
        for (var i = 0, j = els.length; i < j; i++) {
            if (re.test(els[i].className)) {
                a.push(els[i]);
            }
        }
        return a;
    }-*/;

    /**
     * Admin tool only. Layer state refers to the ability, in the admin tool, to turn individual layers on/off.
     */
    @Export
    @Override
    public void addLayerStateListener(ChangeListener<LayerState> listener) {
        viewsSession.addLayerStateListener(listener);
    }

    @Override
    public void addAngleChangeListener(AngleChangeListener listener) {
        viewsSession.addAngleChangeListener(listener);

    }

    @Override
    public int getAngle() {
        return getViewSession().getAngle();
    }
}
