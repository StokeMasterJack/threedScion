package c3i.threedModel.client;

import c3i.imageModel.shared.CoreImageStack;
import c3i.imageModel.shared.Slice;

public interface ImageUrlProvider {
    CoreImageStack getImageUrl(Slice viewState);
}
