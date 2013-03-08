package c3i.imgGen.external;

import c3i.core.common.shared.SeriesId;
import c3i.imageModel.shared.SeriesKey;
import c3i.imageModel.shared.SimpleFeatureModel;

import java.util.Set;

public interface ImgGenContext extends SimpleFeatureModel {

    /**
     * @return image model in json
     */
    String getImageModelJson();

    byte[] getPng(String pngShortSha);

    void forEach(Set<Object> outVars, ProductHandlerSimple productHandler);

    Object getVar(String varCode);

    SeriesKey getSeriesKey();

    SeriesId getSeriesId();

    boolean containsVarCode(String varCode);

    String getKey();

    long getSatCount(Set<Object> outVars);
}
