package com.tms.threed.threedFramework.previewPane.client.previewPanelModel;

import com.tms.threed.threedFramework.imageModel.shared.IImageStack;
import com.tms.threed.threedFramework.threedCore.shared.Slice;

public interface ImageUrlProvider {
    IImageStack getImageUrl(Slice viewState);
}
