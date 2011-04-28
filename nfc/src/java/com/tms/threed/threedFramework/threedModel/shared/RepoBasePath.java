package com.tms.threed.threedFramework.threedModel.shared;

import com.tms.threed.threedFramework.util.lang.shared.Path;

public class RepoBasePath implements RepoBase {

    private final Path dir;
    private final Path url;

    public RepoBasePath(Path dir, Path url) {
        this.dir = dir;
        this.url = url;
    }

    public RepoBasePath(String dir, String url) {
        this.dir = new Path(dir);
        this.url = new Path(url);
    }

    @Override public Path getUrl() {
        return url;
    }

    @Override public Path getDir() {
        return dir;
    }

    @Override public String toString() {
        return dir + "  -  " + url;
    }

}
