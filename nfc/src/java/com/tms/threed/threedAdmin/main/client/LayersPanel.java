package com.tms.threed.threedAdmin.main.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.tms.threed.threedFramework.imageModel.shared.ILayer;
import com.tms.threed.threedFramework.imageModel.shared.IPng;
import com.tms.threed.threedFramework.util.lang.shared.Path;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class LayersPanel extends Composite {

    private FlowPanel buttonPanel = createButtonPanel();
    private FlowPanel layerPanel = createLayerPanel();
    private FlowPanel mainPanel = createMainPanel();

    private final LayersPanelModel model;

    public LayersPanel(LayersPanelModel model) {
        this.model = model;


//            t.setBorderWidth(1);

//            HTML title = new HTML();
//            title.setHTML("<div style='font-weight:bold;margin-left:.2em;text-align:center;'>Layers</div>");
//            vp.add(title);


//        refresh();


//            vp.setBorderWidth(1);

        initWidget(mainPanel);
    }

    private FlowPanel createMainPanel() {
        FlowPanel p = new FlowPanel();
        p.getElement().getStyle().setPaddingLeft(.5, Style.Unit.EM);
        p.getElement().getStyle().setPaddingRight(.5, Style.Unit.EM);
        p.getElement().getStyle().setPaddingTop(.2, Style.Unit.EM);
        p.getElement().getStyle().setPaddingBottom(.5, Style.Unit.EM);

        ScrollPanel scrollPanel = new ScrollPanel(layerPanel);
        scrollPanel.setHeight("100%");

        p.add(buttonPanel);
        p.add(scrollPanel);

//        p.setWidth("50%");
        return p;
    }

    private FlowPanel createButtonPanel() {
        FlowPanel p = new FlowPanel();

//        p.getElement().getStyle().setBackgroundColor("pink");

        Anchor selectAllLink = new Anchor("Select All");
        Anchor selectNoneLink = new Anchor("Select None");

        selectAllLink.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                    @Override
                    public void execute() {
                        model.selectAll();
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
                        model.selectNone();
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

        if (model.isInvalidBuild()) {
            refreshInvalidBuild();
        } else {
            refreshValidBuild();
        }
    }

    public void refreshValidBuild() {
        assert model.isValidBuild();


        if (model.isPngMode()) {
            this.setVisible(true);
        } else {
            this.setVisible(false);
        }


        List<ILayer> layers = model.getLayers();

        Collections.sort(layers, new Comparator<ILayer>() {
            @Override
            public int compare(ILayer L1, ILayer L2) {
                return L1.getSimpleName().compareTo(L2.getSimpleName());
            }
        });

        for (final ILayer layer : layers) {

            final IPng png = model.getPngForLayer(layer);


            boolean hasPng = png != null;

            String helpImageUrl = GWT.getModuleBaseURL() + "help_16.png";

            Image helpIcon = new Image(helpImageUrl);
            helpIcon.setPixelSize(10, 10);
            helpIcon.getElement().getStyle().setMarginRight(3, Style.Unit.PX);
            helpIcon.addClickHandler(new ClickHandler() {
                @Override public void onClick(ClickEvent event) {
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
                checkBox.setValue(layer.isVisible());
                checkBox.setEnabled(true);
                checkBox.setTitle(png.getUrl(new Path()) + "/" + png.getFeatures() + "");
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


    public void refreshInvalidBuild() {
        assert model.isInvalidBuild();
        this.setVisible(false);
    }

}

