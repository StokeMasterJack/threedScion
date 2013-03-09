package c3i.imgGen.external;

import javax.annotation.Nonnull;

public interface ImgGenContextFactory<K> {

    @Nonnull
    ImgGenContext getImgGenContext(K key);

}
