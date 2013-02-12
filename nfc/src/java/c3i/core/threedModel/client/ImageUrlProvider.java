package c3i.core.threedModel.client;

import c3i.imageModel.shared.CoreImageStack;
import c3i.imageModel.shared.Slice;

public interface ImageUrlProvider {
    CoreImageStack getImageUrl(Slice viewState);
}
