package c3i.smartClient.client.skins.viewSelectors;

import c3i.core.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.ViewModel;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import java.util.logging.Level;import java.util.logging.Logger;
import smartsoft.util.shared.RectSize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ThumbViewSelector extends ViewSelector {

    public static final RectSize DEFAULT_THUMB_SIZE = RectSize.STD_PNG.scaleToWidth(108);

    private final ViewModel viewPanelModel;
    private final FlowPanel ap;
    private final RectSize thumbSize;

    private final ThumbViewPanel[] viewPanels;
    private final RectSize preferredSize;


    public ThumbViewSelector(final ViewModel viewPanelModel) {
        this(viewPanelModel, null);
    }

    public ThumbViewSelector(final ViewModel viewPanelModel, final RectSize pThumbSize) {
        this.viewPanelModel = viewPanelModel;

        if (pThumbSize == null) {
            this.thumbSize = DEFAULT_THUMB_SIZE;
        } else {
            this.thumbSize = pThumbSize;
        }

        if (this.thumbSize == null) throw new IllegalStateException();
        int viewCount = viewPanelModel.getViews().size();
        this.preferredSize = getPreferredSize(viewCount, this.thumbSize);

//        List<? extends ViewModel> thumbModels = viewPanelModel.getViewModels();


        ArrayList<? extends ViewModel> thumbModels = new ArrayList<ViewModel>(viewPanelModel.getViewModels());

        Collections.sort(thumbModels,new Comparator<ViewModel>() {
            @Override
            public int compare(ViewModel o1, ViewModel o2) {
                Integer i1 = o1.getView().getIndex();
                Integer i2 = o2.getView().getIndex();
                return i1.compareTo(i2);
            }
        });

        Collections.reverse(thumbModels);

        log.log(Level.INFO, "thumbModels = " + thumbModels);

        ap = new FlowPanel();
        initWidget(ap);

        viewPanels = new ThumbViewPanel[thumbModels.size()];

        for (final ViewModel thumbModel : thumbModels) {
            int thumbViewIndex = thumbModel.getViewIndex();

            viewPanels[thumbViewIndex] = new ThumbViewPanel(thumbModel, this.thumbSize);
            viewPanels[thumbViewIndex].getElement().setId("viewPanel" + thumbViewIndex);
            viewPanels[thumbViewIndex].addStyleName("Thumb");
            viewPanels[thumbViewIndex].addDomHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    int newViewIndex = thumbModel.getViewIndex();
                    viewPanelModel.setViewIndex(newViewIndex);
                }
            }, ClickEvent.getType());


            ThumbViewPanel viewPanel = viewPanels[thumbViewIndex];
            viewPanel.setVisible(true);

            ap.add(viewPanel);

//            int viewIndex = viewPanelModel.getViewIndex();
//            if (thumbViewIndex == viewIndex) {
//                viewPanels[thumbViewIndex].getElement().getStyle().setDisplay(Style.Display.NONE);
//            }

        }
        ap.addStyleName("ViewSelector");
        ap.addStyleName("Thumb");

        viewPanelModel.addViewChangeListener(new ViewChangeListener() {
            @Override
            public void onChange(ViewKey newValue) {
                refresh();
            }
        });

        refresh();

        ap.getElement().getStyle().setFloat(Style.Float.RIGHT);


    }

    private static RectSize getPreferredSize(int viewCount, RectSize thumbSize) {
        int w = viewCount * thumbSize.getWidth();
        int h = thumbSize.getHeight();
        return new RectSize(w, h);
    }

    public RectSize getPreferredSize() {
        return preferredSize;
    }


    class ThumbViewPanel extends FlowPanel {

        ViewPanel viewPanel;
        HTML label;

        ThumbViewPanel(ViewModel thumbModel, RectSize fixedSize) {
            viewPanel = new ViewPanel(thumbModel, fixedSize);
            label = new HTML(thumbModel.getView().getLabel());

            add(viewPanel);
            add(label);


            label.getElement().getStyle().setWidth(fixedSize.getWidth(), Style.Unit.PX);
            label.getElement().getStyle().setProperty("textAlign", "center");

            getElement().getStyle().setWidth(fixedSize.getWidth(), Style.Unit.PX);
//            getElement().getStyle().setBackgroundColor("green");
            getElement().getStyle().setFloat(Style.Float.RIGHT);
            getElement().getStyle().setMarginLeft(5, Style.Unit.PX);
//            getElement().getStyle().setMarginRight(5, Style.Unit.PX);
        }

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);
        }
    }

    private void refresh() {


        List<? extends ViewModel> thumbModels = viewPanelModel.getViewModels();
        for (final ViewModel thumbModel : thumbModels) {

            int thumbViewIndex = thumbModel.getViewIndex();
            ThumbViewPanel viewPanel = viewPanels[thumbViewIndex];

            boolean selected = thumbModel.getViewIndex() == viewPanelModel.getViewIndex();
//            viewPanel.setVisible(!selected);

            if (selected) {
                viewPanel.addStyleName("selected");
            } else {
                viewPanel.removeStyleName("selected");
            }


        }


//        ImageStack imageStack = viewPanelModel.getImageStack();
//
//        if (imageStack == null || imageStack.getFixedPicks() == null || imageStack.getFixedPicks().isInvalidBuild()) {
//            asWidget().setVisible(true);
//        } else {
//            asWidget().setVisible(true);
//        }

    }

    private static Logger log = Logger.getLogger(ThumbViewSelector.class.getName());

}
