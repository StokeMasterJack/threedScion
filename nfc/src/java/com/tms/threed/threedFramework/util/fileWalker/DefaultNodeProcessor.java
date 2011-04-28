package com.tms.threed.threedFramework.util.fileWalker;

import java.io.File;

public class DefaultNodeProcessor implements NodeProcessor {

    private FileWalker walker;

    public DefaultNodeProcessor(FileWalker walker) {this.walker = walker;}

    @Override public void processNode(File f) {
        walker.processNode(f);
    }

}