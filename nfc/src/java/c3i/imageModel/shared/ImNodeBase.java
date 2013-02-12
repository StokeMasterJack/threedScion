package c3i.imageModel.shared;

import smartsoft.util.shared.Path;
import smartsoft.util.shared.Strings;

abstract public class ImNodeBase implements ImNode {

    protected final int depth;
    protected IsParent parent;

    protected ImNodeBase(int depth) {
        this.depth = depth;
    }

    public IsParent getParent() {
        return parent;
    }

    @Override
    public void printTree() {
        int d = getDepth();
        String nn = getName();
        if (isPng()) {
            nn += " " + asPng().getShortSha();
        }
        System.out.println(Strings.tab(d) + d + ":" + nn);
        if (isParent()) {
            IsParent p = (IsParent) this;
            for (Object o : p.getChildNodes()) {
                ImNode n = (ImNode) o;
                n.printTree();
            }
        }

    }

    @Override
    public boolean isChild() {
        return this instanceof IsChild;
    }

    @Override
    public String getType() {
        return Strings.getSimpleName(this);
    }

    @Override
    public boolean isParent() {
        return this instanceof IsParent;
    }

    public boolean isRoot() {
        return this instanceof IsRoot;
    }

    /**
     * @return the original file name from the png file/dir structure
     */
    abstract public String getName();

    public boolean isSeries() {
        return this instanceof ImageModel;
    }

    public boolean isView() {
        return this instanceof ImView;
    }

    public boolean isLayer() {
        return this instanceof ImLayer;
    }

    public boolean isFeature() {
        return this instanceof ImFeature;
    }

    public boolean isPng() {
        return this instanceof SrcPng;
    }

    @Override
    public ImageModel asSeries() {
        if (isSeries()) return (ImageModel) this;
        else return null;
    }

    @Override
    public ImView asView() {
        if (isView()) return (ImView) this;
        else return null;
    }

    @Override
    public ImLayer asLayer() {
        if (isLayer()) return (ImLayer) this;
        else return null;
    }

    @Override
    public ImFeature asFeature() {
        if (isFeature()) return (ImFeature) this;
        else return null;
    }

    @Override
    public SrcPng asPng() {
        if (isPng()) return (SrcPng) this;
        else return null;
    }

    public Path getUrl(Path repoBaseUrl) {
        if (isRoot()) return getLocalPath();
        IsParent parent = getParent();
        Path parentPath = parent.getUrl(repoBaseUrl);
        Path localPath = getLocalPath();
        Path path = parentPath.append(localPath);
        return path;
    }

    public Path getLocalPath() {
        return new Path(getName());
    }

    public String toString(Path repoBaseUrl) {
        return getUrl(repoBaseUrl).toString();
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int getDepth() {
        return depth;
    }
}
