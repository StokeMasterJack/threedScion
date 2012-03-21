package com.tms.threed.threedCore.threedModel.client;

import com.tms.threed.threedCore.imageModel.shared.ImageStack;
import com.tms.threed.threedCore.threedModel.shared.Slice;

public interface ImageUrlProvider {
    ImageStack getImageUrl(Slice viewState);
}
