package com.tms.threed.repo.server;

import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import org.dom4j.Document;
import org.dom4j.Element;

public class ModelXml {

    private static final String DISPLAY_NAME_TAG_NAME = "name";
    private static final String FEATURE_MODEL_TAG_NAME = "features";
    private static final String IMAGE_MODEL_TAG_NAME = "image-model";

    private final SeriesKey seriesKey;

    private final String seriesDisplayName;

    private final Element featureModelElement;
    private final Element imageModelElement;


    public ModelXml(SeriesKey seriesKey, Document document) {
        this.seriesKey = seriesKey;

        Element modelElement = document.getRootElement();

        this.seriesDisplayName = modelElement.attributeValue(DISPLAY_NAME_TAG_NAME);

        this.featureModelElement = modelElement.element(FEATURE_MODEL_TAG_NAME);
        this.imageModelElement = modelElement.element(IMAGE_MODEL_TAG_NAME);

    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public String getSeriesDisplayName() {
        return seriesDisplayName;
    }

    public Element getFeatureModelElement() {
        return featureModelElement;
    }

    public Element getImageModelElement() {
        return imageModelElement;
    }

    public int getYear() {
        return seriesKey.getYear();
    }
}
