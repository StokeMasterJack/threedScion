package c3i.core.imageModel.shared;

import smartsoft.util.lang.shared.Path;

public interface ImImage {

    Path getUrl(Path repoBase);
    Profile getProfile();
    boolean isLayerPng();
}
