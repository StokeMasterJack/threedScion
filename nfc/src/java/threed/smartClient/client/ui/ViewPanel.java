package threed.smartClient.client.ui;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.FlowPanel;
import threed.smartClient.client.api.ImageChangeListener;
import threed.smartClient.client.api.ImageStack;
import threed.smartClient.client.api.ViewSession;
import smartsoft.util.gwt.client.events3.ChangeEvent;
import smartsoft.util.lang.shared.ImageSize;

public class ViewPanel extends FlowPanel {

    private final AbsolutePanel absolutePanel = new AbsolutePanel();

    private static int zIndex = 100;
    private static int imageCount = 10;

    private final ViewPanelModel model;

    private final ImmutableList<ImageWidget> imageWidgets;

    private ImageSize imageSize;
    private ImageStack imageStack;

    public ViewPanel(ViewPanelModel model) {
        this.model = model;
        imageWidgets = initImageList();
        maybeRefresh();

        model.addImageChangeListener2(new ImageChangeListener() {
            @Override
            public void onEvent(ChangeEvent<ViewSession, ImageStack> ev) {
                maybeRefresh();
            }
        });

        add(absolutePanel);
    }

    private ImmutableList<ImageWidget> initImageList() {
        ImmutableList.Builder<ImageWidget> builder = ImmutableList.builder();
        for (int i = 0; i < imageCount; i++) {
            ImageWidget imageWidget = new ImageWidget();
            imageWidget.setZIndex(zIndex + i);
            builder.add(imageWidget);
            absolutePanel.add(imageWidget, 0, 0);
        }
        return builder.build();
    }

    private void maybeRefreshImageSize() {
        if (!Objects.equal(this.imageSize, model.getImageSize())) {
            this.imageSize = model.getImageSize();
            setPixelSize(imageSize);

            for (ImageWidget imageWidget : imageWidgets) {
                imageWidget.setPixelSize(model.getImageSize());
            }
        }
    }

    private void maybeRefreshImageStack() {

        ImageStack imageStack1 = this.imageStack;
        ImageStack imageStack2 = model.getImageStack();

        if (!Objects.equal(imageStack1, imageStack2)) {
            this.imageStack = model.getImageStack();
            for (int i = 0; i < imageCount; i++) {
                try {
                    imageWidgets.get(i).setUrl(imageStack.get(i).getUrl());
                    imageWidgets.get(i).setVisible(true);
                } catch (IndexOutOfBoundsException e) {
                    imageWidgets.get(i).setVisible(false);
                }
            }
        }
    }

    private void maybeRefresh() {
        if (model.isVisible()) {
            setVisible(true);
            maybeRefreshImageSize();
            maybeRefreshImageStack();

        } else {
            setVisible(false);
        }
    }


    public void setPixelSize(ImageSize imageSize) {
        absolutePanel.setPixelSize(imageSize.getWidth(), imageSize.getHeight());

    }

}
