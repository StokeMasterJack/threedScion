package c3i.imgGen.api;

import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.ImContextKey;
import com.google.common.io.InputSupplier;

import java.io.InputStream;

public interface SrcPngLoader {

    InputSupplier<? extends InputStream> getPng(SeriesKey imageModelKey, String pngShortSha);

}
