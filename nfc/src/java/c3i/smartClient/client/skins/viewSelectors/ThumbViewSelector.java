package c3i.smartClient.client.skins.viewSelectors;

import c3i.core.imageModel.shared.ViewKey;
import c3i.smartClient.client.model.ViewModel;
import c3i.smartClient.client.model.event.ViewChangeListener;
import c3i.smartClient.client.widgets.ViewPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import smartsoft.util.lang.shared.RectSize;

import java.util.List;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

public class ThumbViewSelector extends ViewSelector {

    public static final RectSize DEFAULT_THUMB_SIZE = RectSize.STD_PNG.scaleToWidth(108);

    private final ViewModel viewPanelModel;
    private final FlowPanel ap;
    private final RectSize thumbSize;

    private final ViewPanel[] viewPanels;
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


        List<? extends ViewModel> thumbModels = viewPanelModel.getViewModels();

        ap = new FlowPanel();
        initWidget(ap);

        viewPanels = new ViewPanel[thumbModels.size()];

        for (final ViewModel thumbModel : thumbModels) {
            System.out.println("thumbModel = " + thumbModel);
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


            ViewPanel viewPanel = viewPanels[thumbViewIndex];
            viewPanel.setVisible(true);

            ap.add(viewPanel);
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



    }

    private static RectSize getPreferredSize(int viewCount, RectSize thumbSize) {
        int w = viewCount * thumbSize.getWidth();
        int h = thumbSize.getHeight();
        return new RectSize(w, h);
    }

    public RectSize getPreferredSize() {
        return preferredSize;
    }


    class ThumbViewPanel extends ViewPanel {

        ThumbViewPanel(ViewModel thumbModel, RectSize fixedSize) {
            super(thumbModel, fixedSize, "ThumbViewPanel");
        }

        @Override
        public void setVisible(boolean visible) {
            super.setVisible(visible);
        }
    }

    private void refresh() {

//        ImageStack imageStack = viewPanelModel.getImageStack();
//
//        if (imageStack == null || imageStack.getFixedPicks() == null || imageStack.getFixedPicks().isInvalidBuild()) {
//            asWidget().setVisible(true);
//        } else {
//            asWidget().setVisible(true);
//        }

    }

}
