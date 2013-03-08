package c3i.imageModel.shared;

import smartsoft.util.shared.Path;

public interface ImImage {

    Path getUrl(Path repoBase);

    Profile getProfile();

    boolean isLayerPng();
}
