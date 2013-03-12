package c3i.imageModel.shared;

import smartsoft.util.shared.Path;

public interface ImNode<V> {

    /**
     * @return the original file name from the png file/dir structure
     */
    String getName();

    boolean isRoot();

    boolean isSeries();

    boolean isView();

    boolean isLayer();

    boolean isFeature();

    boolean isPng();

    ImageModel<V> asSeries();

    ImView<V> asView();

    ImLayer<V> asLayer();

    ImFeature<V> asFeature();

    SrcPng<V> asPng();

    Path getUrl(Path repoBaseUrl);

    Path getLocalPath();

    IsParent getParent();

    int getDepth();

    void printTree();

    boolean isParent();

    boolean isChild();

    String getType();

    boolean containsAngle(int angle);

    String toString(Path repoBaseUrl);

}