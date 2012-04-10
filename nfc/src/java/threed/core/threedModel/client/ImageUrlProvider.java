package threed.core.threedModel.client;

import threed.core.imageModel.shared.ImImageStack;
import threed.core.threedModel.shared.Slice;

public interface ImageUrlProvider {
    ImImageStack getImageUrl(Slice viewState);
}
