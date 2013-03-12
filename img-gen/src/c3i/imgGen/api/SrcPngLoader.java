package c3i.imgGen.api;

import c3i.imageModel.shared.ImageModelKey;
import com.google.common.io.InputSupplier;

import java.io.InputStream;

public interface SrcPngLoader {

    InputSupplier<? extends InputStream> getPng(ImageModelKey imageModelKey, String pngShortSha);

}
