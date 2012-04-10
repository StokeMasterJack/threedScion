package threed.skin.common.client;

import com.google.common.collect.ImmutableList;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.*;
import smartsoft.util.lang.shared.ImageSize;
import smartsoft.util.lang.shared.Path;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays multiple images vertically stacked.
 * Collects the LoadEvent's and ErrorEvent's into a single event
 */
public class ThreedImagePanel extends Composite {

    private ThreedImagePanelListener listener;

    private final AbsolutePanel absPanel = new AbsolutePanel();

    private final int panelIndex;

    private ImageSize imageSize;

    private ImageBatchLoaderDead batchLoader;

    public ThreedImagePanel(int panelIndex, ImageSize imageSize) {
        this.panelIndex = panelIndex;
        this.imageSize = imageSize;
        absPanel.setPixelSize(imageSize.getWidth(), imageSize.getHeight());
        initWidget(absPanel);
        getElement().setId("gwt-debug-ThreedImagePanel");

    }

    public void setListener(ThreedImagePanelListener listener) {
        this.listener = listener;
    }

    public int getPanelIndex() {
        return panelIndex;
    }

    public void setImageUrls(@Nonnull ImmutableList<Path> urls) {

        batchLoader = new ImageBatchLoaderDead(urls, imageSize, new ImageBatchLoaderDead.BatchLoadListener() {

            @Override
            public void onAllImagesComplete() {
                if (batchLoader.isFatal()) {
                    List<Path> errors = batchLoader.getErrors();
                    show404Message(errors);
                }
                if (listener != null) {
                    listener.allImagesComplete(batchLoader.getErrors(), batchLoader.isFatal());
                }
                batchLoader.showAll();
            }

            @Override
            public void onFirstImageComplete(ImageLoaderOld.FinalOutcome finalOutcome) {
                clearNonActiveImages();
            }

        });

        List<ImageLoaderOld> loaders = batchLoader.getLoaders();
        for (ImageLoaderOld loader : loaders) {
            absPanel.add(loader.getImage());
        }

    }


    public void setPlaceHolder(Widget widget) {
        absPanel.clear();
        widget.setSize("100%", "100%");
        absPanel.add(widget, 0, 0);
    }

    public void refreshImageSizes() {
        int widgetCount = absPanel.getWidgetCount();
        for (int i = 0; i < widgetCount; i++) {
            Widget widget = absPanel.getWidget(i);
            if (widget instanceof Image) {
                Image image = (Image) widget;
                image.setPixelSize(imageSize.getWidth(), imageSize.getHeight());
            }
        }
    }


    public boolean isMain() {
        return panelIndex == 0;
    }


    private void show404Message(String msg) {
        showMessage("404", msg, "#dddddd");
    }

    private void show404Message(List<Path> badUrls) {
        showMessage("404", badUrls.toString(), "#dddddd");
    }


    public void showMessage(String shortMessage, final String longMessage, String color) {
        final InlineLabel label = new InlineLabel(shortMessage);
        label.getElement().getStyle().setPadding(.2, Style.Unit.EM);
        label.getElement().getStyle().setCursor(Style.Cursor.POINTER);
//        label.getElement().getStyle().setBackgroundColor("yellow");
        final PopupPanel pp = new PopupPanel(true);
        pp.setWidget(new Label(longMessage));
        pp.getElement().getStyle().setZIndex(1000);

        label.addClickHandler(new ClickHandler() {


            @Override
            public void onClick(ClickEvent event) {
                pp.showRelativeTo(label);
                event.preventDefault();
                event.stopPropagation();
            }
        });

        Grid grid = new Grid(1, 1);
        grid.setWidget(0, 0, label);
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER);
        Style style = grid.getElement().getStyle();
        style.setBackgroundColor(color);
        this.setPlaceHolder(grid);
    }


    public void setImageSize(ImageSize imageSize) {
        this.imageSize = imageSize;
        setPixelSize(imageSize.getWidth(), imageSize.getHeight());
        refreshImageSizes();
    }


    private void clearNonActiveImages() {
        int L = absPanel.getWidgetCount();
        ArrayList<Widget> toBeRemoved = new ArrayList<Widget>();
        for (int i = 0; i < L; i++) {
            Widget w = absPanel.getWidget(i);
            if (w instanceof Image) {
                Image img = (Image) w;
                String url = img.getUrl();
                Path urlPath = new Path(url);
                if (!isActive(urlPath)) {
                    toBeRemoved.add(w);
                }
            }
        }

        for (Widget w : toBeRemoved) {
            absPanel.remove(w);
        }
    }

    public static interface ThreedImagePanelListener {
        void allImagesComplete(List<Path> errors, boolean fatal);
    }

    public boolean isActive(Path imageUrl) {
        return batchLoader.containsUrl(imageUrl);
    }


}
