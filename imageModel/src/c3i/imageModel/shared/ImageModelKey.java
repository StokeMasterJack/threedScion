package c3i.imageModel.shared;

import smartsoft.util.shared.Path;

public interface ImageModelKey {

    String getName();

    Path getLocalPath();

    String getBrand();

    int getYear();
}
