package com.tms.threed.threedFramework.util.fileWalker;

import java.io.File;
import java.io.FileFilter;

public class FileWalker {

    protected int filesProcessedCount;

    protected File startDir;
    protected NodeProcessor nodeProcessor = new DefaultNodeProcessor(this);
    protected FileProcessor fileProcessor = new DefaultFileProcessor();
    protected DirProcessor dirProcessor = new DefaultDirProcessor(this);

    private FileFilter fileFilter = new DefaultFileFilter();
    private FileFilter dirFilter = new DefaultFileFilter();

    public FileWalker() {
        this(null);
    }

    public FileWalker(File startDir) {
        this.startDir = startDir;
    }

    public FileFilter getDirFilter() {
        return dirFilter;
    }

    public void setDirFilter(FileFilter dirFilter) {
        this.dirFilter = dirFilter;
    }

    public void setFileProcessor(FileProcessor fileProcessor) {
        this.fileProcessor = fileProcessor;
    }

    public FileProcessor getFileProcessor() {
        return fileProcessor;
    }

    public DirProcessor getDirProcessor() {
        return dirProcessor;
    }

    public void setDirProcessor(DirProcessor dirProcessor) {
        this.dirProcessor = dirProcessor;
    }

    public int getFilesProcessedCount() {
        return filesProcessedCount;
    }

    public void setStartDir(File startDir) {
        this.startDir = startDir;
    }

    public File getStartDir() {
        return startDir;
    }


    public void setFileFilter(FileFilter fileFilter) {
        this.fileFilter = fileFilter;
    }

    public FileFilter getFileFilter() {
        return fileFilter;
    }

    public void start() {
        if (startDir == null) throw new IllegalStateException("startDir must be set");
        processNode(startDir);
    }


    public void processNode(File node) {
        if (!node.exists()) {
            if (node.equals(startDir)) throw new IllegalStateException("StartDir does not exist: " + node);
            return;
        }
        if (node.isFile()) {
            if (fileFilter.accept(node)) {
                processFile(node);
                filesProcessedCount++;
            }
        } else if (node.isDirectory()) {
            if (dirFilter.accept(node)) {
                processDir(node);
            }
        }
    }

    protected void processFile(File file) {
        if (fileProcessor != null) {
            fileProcessor.processFile(file);
        }
    }

    protected void processDir(File dir) {
        dirProcessor.processDir(dir);
    }


}