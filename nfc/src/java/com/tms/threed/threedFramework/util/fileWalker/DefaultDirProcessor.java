package com.tms.threed.threedFramework.util.fileWalker;

import java.io.File;

public class DefaultDirProcessor implements DirProcessor {
    
    protected FileWalker walker;

    public DefaultDirProcessor(FileWalker walker) {this.walker = walker;}

    @Override public void processDir(File dir) {
        for (File child : dir.listFiles()) {
            walker.processNode(child);
        }
    }
}
