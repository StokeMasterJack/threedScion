package c3i.imgGen.external;

import c3i.core.common.server.SrcPngLoader;
import c3i.core.common.shared.ProductHandler;
import c3i.core.common.shared.SeriesId;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.SimpleFeatureModel;

import java.util.Set;

public interface ImgGenContext<PRODUCT_TYPE> extends SimpleFeatureModel, SrcPngLoader {

    /**
     * @return image model in json
     */
    String getImageModelJson();

    void forEach(Set<Object> outVars, ProductHandler<PRODUCT_TYPE> productHandler);

    SeriesId getSeriesId();

    String getKey();

    long getSatCount(Set<Object> outVars);

    ImageModel getImageModel();

    int getSliceCount();
}
