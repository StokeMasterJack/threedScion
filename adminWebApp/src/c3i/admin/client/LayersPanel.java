package c3i.admin.client;

import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.imageModel.shared.ImLayer;
import c3i.core.imageModel.shared.ImageMode;
import c3i.core.imageModel.shared.PngSpec;
import c3i.core.imageModel.shared.Profile;
import c3i.smartClient.client.model.LayerState;
import c3i.smartClient.client.model.ViewSession;
import c3i.smartClient.client.model.ViewsSession;
import c3i.util.shared.events.ChangeListener;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.dialogs.MyDialogBox;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static smartsoft.util.lang.shared.Strings.getSimpleName;

public class LayersPanel extends FlowPanel {

    private FlowPanel buttonPanel = createButtonPanel();
    private FlowPanel layerPanel = createLayerPanel();

    private final LayersPanelModel model;
    private final ViewSession viewSession;
    private final ViewsSession viewsSession;

    public LayersPanel(LayersPanelModel model) {
        Console.log("LayersPanel.LayersPanel");
        this.model = model;
        this.viewSession = model.getViewSession();
        this.viewsSession = viewSession.getParent();

        Style style = getElement().getStyle();
        style.setPadding(10, Style.Unit.PX);

        ScrollPanel scrollPanel = new ScrollPanel(layerPanel);
        scrollPanel.setHeight("98%");

        add(buttonPanel);
        add(scrollPanel);

        refresh();

        setSize("100%", "100%");

        addStyleName(getSimpleName(this));

        viewsSession.profile().addChangeListener(new ChangeListener<Profile>() {
            @Override
            public void onChange(Profile newValue) {
                refresh();
                //                statusPanel.refresh();
            }
        });

        viewsSession.imageMode().addChangeListener(new ChangeListener<ImageMode>() {
            @Override
            public void onChange(ImageMode newValue) {
                Console.log("ImageMode changed");
                refresh();
                //                statusPanel.refresh();
            }
        });

        viewsSession.fixedPicks().addChangeListener(new ChangeListener<FixedPicks>() {
            @Override
            public void onChange(FixedPicks newValue) {
                refresh();
            }
        });


        viewSession.getLayerState().addChangeListener(new ChangeListener<LayerState>() {
            @Override
            public void onChange(LayerState newValue) {
                Console.log("LayerState changed");
                refresh();
            }
        });
    }


    private FlowPanel createButtonPanel() {
        FlowPanel p = new FlowPanel();


        Anchor selectAllLink = new Anchor("Select All");
        Anchor selectNoneLink = new Anchor("Select None");

        selectAllLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        model.enableAll();
                        refresh();
                    }
                });

            }
        });


        selectNoneLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        model.enableNone();
                        refresh();
                    }
                });
            }
        });


        selectAllLink.getElement().getStyle().setMarginLeft(.2, Style.Unit.EM);
        selectAllLink.getElement().getStyle().setMarginRight(.5, Style.Unit.EM);

        p.add(selectAllLink);
        p.add(selectNoneLink);

        return p;
    }

    private FlowPanel createLayerPanel() {
        FlowPanel p = new FlowPanel();
        return p;
    }


    public void refresh() {
        layerPanel.clear();

        if (viewSession.isPngMode()) {
            if (model.isInvalidBuild()) {
                refreshInvalidBuild();
            } else {
                refreshValidBuild();
            }
        }

    }

    private void refreshValidBuild() {

        LayerState m = model.getViewSession().getLayerState();

        List<ImLayer> layers = model.getLayers();

        Collections.sort(layers, new Comparator<ImLayer>() {
            @Override
            public int compare(ImLayer L1, ImLayer L2) {
                return L1.getSimpleName().compareTo(L2.getSimpleName());
            }
        });

        for (final ImLayer layer : layers) {

            final PngSpec png = model.getPngForLayer(layer);

            boolean hasPng = png != null;

            String helpImageUrl = GWT.getModuleBaseURL() + "help_16.png";

            Image helpIcon = new Image(helpImageUrl);
            helpIcon.setPixelSize(10, 10);
            helpIcon.getElement().getStyle().setMarginRight(3, Style.Unit.PX);
            helpIcon.addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    MyDialogBox d = new MyDialogBox("Layer " + layer.getSimpleName());
                    LayerInfoPanel p = new LayerInfoPanel(layer, png);
                    d.setWidget(p);
                    d.center();
                    d.show();
                }
            });


            CheckBox checkBox = new CheckBox(layer.getSimpleName(), true);
            Style checkboxStyle = checkBox.getElement().getStyle();

            if (hasPng) {
                helpIcon.getElement().getStyle().setVisibility(Style.Visibility.VISIBLE);

                boolean layerEnabled = m.isLayerEnabled(layer);
                checkBox.setValue(layerEnabled);

                checkBox.setEnabled(true);
                checkBox.setTitle(png.toString() + "/" + png.getFeatures() + "");
                checkboxStyle.setColor("black");
            } else {
                helpIcon.getElement().getStyle().setVisibility(Style.Visibility.HIDDEN);
                checkBox.setValue(false);
                checkBox.setEnabled(false);
                checkBox.setTitle("Layer not visible based on current picks");
                checkboxStyle.setColor("#DDDDDD");
            }


            checkBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
                @Override
                public void onValueChange(ValueChangeEvent<Boolean> e) {

                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            model.toggleLayer(layer);
                        }
                    });
                }
            });


            Style inputStyle = checkBox.getElement().getElementsByTagName("input").getItem(0).getStyle();
            Style labelStyle = checkBox.getElement().getElementsByTagName("label").getItem(0).getStyle();
            inputStyle.setMarginRight(1, Style.Unit.EM);
            checkboxStyle.setDisplay(Style.Display.BLOCK);


            FlowPanel g = new FlowPanel();

            checkboxStyle.setDisplay(Style.Display.INLINE);


            g.add(helpIcon);
            g.add(checkBox);


            layerPanel.add(g);


        }


    }


    private void refreshInvalidBuild() {
        assert model.isInvalidBuild();
    }

}

