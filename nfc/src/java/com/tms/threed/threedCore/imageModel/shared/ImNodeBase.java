package com.tms.threed.threedCore.imageModel.shared;

import smartsoft.util.lang.shared.Path;
import smartsoft.util.lang.shared.Strings;

abstract public class ImNodeBase implements ImNode {

    protected final int depth;
    protected IsParent parent;

    protected ImNodeBase(int depth) {
        this.depth = depth;
    }

    public IsParent getParent() {
        return parent;
    }

    @Override public void printTree() {
        int d = getDepth();
        String nn = getName();
        if(isPng()){
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

    @Override public boolean isChild() {
        return this instanceof IsChild;
    }

    @Override public String getType() {
        return Strings.getSimpleName(this);
    }

    @Override public boolean isParent() {
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
        return this instanceof ImSeries;
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
        return this instanceof ImPng;
    }

    @Override public ImSeries asSeries() {
        if (isSeries()) return (ImSeries) this;
        else return null;
    }

    @Override public ImView asView() {
        if (isView()) return (ImView) this;
        else return null;
    }

    @Override public ImLayer asLayer() {
        if (isLayer()) return (ImLayer) this;
        else return null;
    }

    @Override public ImFeature asFeature() {
        if (isFeature()) return (ImFeature) this;
        else return null;
    }

    @Override public ImPng asPng() {
        if (isPng()) return (ImPng) this;
        else return null;
    }

    public Path getPath(Path root) {
        return root.append(getPath());
    }

    public Path getPath() {
        if (isRoot()) return getLocalPath();
        IsParent parent = getParent();
        Path parentPath = parent.getPath();
        Path localPath = getLocalPath();
        Path path = parentPath.append(localPath);
        return path;
    }

    public Path getLocalPath() {
        return new Path(getName());
    }

    @Override public String toString() {
        return getPath().toString();
    }

    @Override public int getDepth() {
        return depth;
    }
}
