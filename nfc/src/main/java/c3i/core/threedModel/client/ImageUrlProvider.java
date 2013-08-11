package c3i.core.threedModel.client;

import c3i.core.imageModel.shared.CoreImageStack;
import c3i.core.threedModel.shared.Slice;

public interface ImageUrlProvider {
    CoreImageStack getImageUrl(Slice viewState);
}
