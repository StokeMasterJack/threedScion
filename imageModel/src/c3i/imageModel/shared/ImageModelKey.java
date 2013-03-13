package c3i.imageModel.shared;

import smartsoft.util.shared.Path;

public interface ImageModelKey {

    String getSeries();

    Path getLocalPath();

    String getBrand();

    int getYear();
}
