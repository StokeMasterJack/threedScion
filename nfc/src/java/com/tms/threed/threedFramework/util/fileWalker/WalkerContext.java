package com.tms.threed.threedFramework.util.fileWalker;

import java.io.File;

public class WalkerContext {

    protected FileWalker walker;
    protected final File dir;

    public WalkerContext(FileWalker walker, File dir) {
        this.walker = walker;
        this.dir = dir;
    }

    public void processChildNodes() {
        for (File child : dir.listFiles()) {
            walker.processNode(child);
        }
    }

}
