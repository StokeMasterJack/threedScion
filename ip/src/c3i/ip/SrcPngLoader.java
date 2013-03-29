package c3i.ip;

import c3i.featureModel.shared.common.SeriesKey;
import com.google.common.io.InputSupplier;

import java.io.InputStream;

public interface SrcPngLoader {

    InputSupplier<? extends InputStream> getPng(SeriesKey imageModelKey, String pngShortSha);

}
