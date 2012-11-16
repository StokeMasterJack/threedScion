package c3i.core.imageModel.shared;

import smartsoft.util.shared.Path;

public interface ImNode {

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

    ImSeries asSeries();

    ImView asView();

    ImLayer asLayer();

    ImFeature asFeature();

    SrcPng asPng();

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