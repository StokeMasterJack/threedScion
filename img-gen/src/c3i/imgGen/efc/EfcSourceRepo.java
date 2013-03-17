package c3i.imgGen.efc;

import com.google.common.io.InputSupplier;

import java.io.InputStream;

public interface EfcSourceRepo {

    InputSupplier<? extends InputStream> getPng(Object fmContext, String pngShortSha);

    String getImageModelJsonText(Object fmContext);

}
