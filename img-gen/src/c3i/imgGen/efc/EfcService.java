package c3i.imgGen.efc;

import c3i.featureModel.shared.common.SimplePicks;
import com.google.common.io.InputSupplier;

import javax.servlet.http.HttpServletRequest;
import java.io.InputStream;
import java.util.Set;

public interface EfcService {

    Object parseFmKeyFromHttpRequest(HttpServletRequest httpServletRequest);

    String serializeFmKeyToString(Object fmKey);

    //the reverse of serializeFmKey
    Object parseFmKeyFromString(String serializedFmKey);

    String getImageModelJsonText(Object fmKey);

    InputSupplier<? extends InputStream> getPng(Object fmKey, String pngShortSha);

    Iterable<SimplePicks> getProducts(Object fmKey, Set<String> outVars);

}
