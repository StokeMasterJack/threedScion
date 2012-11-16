package c3i.core.imageModel.shared;

import smartsoft.util.shared.Path;

public interface ImImage {

    Path getUrl(Path repoBase);
    Profile getProfile();
    boolean isLayerPng();
}
