package c3i.core.common.server;

import com.google.common.io.InputSupplier;

import java.io.InputStream;

public interface SrcPngLoader {

    InputSupplier<? extends InputStream> getPng(String pngShortSha);

}
