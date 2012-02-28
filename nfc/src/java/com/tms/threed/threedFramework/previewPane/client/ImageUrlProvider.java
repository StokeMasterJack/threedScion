package com.tms.threed.threedFramework.previewPane.client;

import com.tms.threed.threedFramework.imageModel.shared.IImageStack;
import com.tms.threed.threedFramework.threedModel.shared.Slice;

public interface ImageUrlProvider {
    IImageStack getImageUrl(Slice viewState);
}
