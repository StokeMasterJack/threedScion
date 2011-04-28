package com.tms.threed.threedFramework.imageModel.shared;

abstract public class ImChildBase extends ImNodeBase implements IsChild {

    protected ImChildBase(int depth) {
        super(depth);
    }

    public void initParent(IsParent parent) {
        if (this.isRoot()) throw new IllegalStateException();
        if (this.getParent() != null) throw new IllegalStateException();
        if (parent == null) throw new IllegalStateException();
        this.parent = parent;
    }


}