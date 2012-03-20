package com.tms.threed.threedCore.threedModel.client;

import com.tms.threed.threedCore.imageModel.shared.IImageStack;
import com.tms.threed.threedCore.threedModel.shared.Slice;

public interface ImageUrlProvider {
    IImageStack getImageUrl(Slice viewState);
}
