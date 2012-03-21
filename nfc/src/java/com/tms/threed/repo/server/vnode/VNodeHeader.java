package com.tms.threed.repo.server.vnode;

public class VNodeHeader {

    public final int depth;
    public final String name;
    public final boolean directory;

    public VNodeHeader(int depth, String name, boolean directory) {
        assert name != null;
        assert name.length() != 0;

        this.depth = depth;
        this.name = name;
        this.directory = directory;
    }

    @Override public String toString() {
        return name + "[depth:" + depth + ", directory:" + directory + "]";
    }
}
