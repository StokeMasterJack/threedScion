package c3i.imgGen;

import c3i.core.common.shared.SeriesId;
import c3i.imgGen.external.ImgGenContext;
import c3i.imgGen.external.ImgGenContextFactory;

import javax.annotation.Nonnull;

public class ImgGenContextFactoryDave implements ImgGenContextFactory {

    @Nonnull
    @Override
    public ImgGenContext getImgGenContext(Object imgGenContextKey) {
        SeriesId seriesId = (SeriesId) imgGenContextKey;
        ImgGenContextImpl ctx = new ImgGenContextImpl(seriesId);
        return ctx;
    }
}
