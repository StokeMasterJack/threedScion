package c3i.imgGen.external;

import javax.annotation.Nonnull;

public interface ImgGenContextFactory {

    @Nonnull
    ImgGenContext getImgGenContext(Object imgGenContextKey);

}
