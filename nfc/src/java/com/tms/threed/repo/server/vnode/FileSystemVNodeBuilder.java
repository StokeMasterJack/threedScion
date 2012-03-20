package com.tms.threed.repo.server.vnode;

import com.google.common.io.Files;
import com.tms.threed.threedCore.imageModel.server.ImageUtil;
import com.tms.threed.repo.server.rt.RtRepo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.ObjectId;

import javax.annotation.Nullable;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

public class FileSystemVNodeBuilder extends VNodeBuilder {

    private final RtRepo rtRepo;

    private FileFilter fileFilter = EXCLUDE_HIDDEN_FILE_FILTER;
    private final File rootFile;

    public FileSystemVNodeBuilder(File rootFile, RtRepo rtRepo) {
        assert rootFile != null;
        assert rtRepo != null;

        this.rootFile = rootFile;
        this.rtRepo = rtRepo;
    }

    @Nullable
    @Override public VNode buildVNode() {
        return createVNode(rootFile, 0);
    }

    private VNode createVNode(File f, int depth) {
        if (f.isDirectory()) {
            return createDirVNode(f, depth);
        } else {
            return createFileVNode(f, depth);
        }
    }

    private VNode createFileVNode(File f, int depth) {
        ContentDetail detail = getFileDetails(f);
        if (detail.isEmptyPng()) return null;

        return new VNode(f.getName(), detail.getFullSha(), depth);
    }

    private VNode createDirVNode(File f, int depth) {

        List<VNode> childNodes = createChildNodes(f, depth + 1);

        if (childNodes == null) return null;


        return new VNode(f.getName(), childNodes, depth);
    }

    @Nullable
    private List<VNode> createChildNodes(File parent, int depth) {
        File[] childFiles = parent.listFiles(fileFilter);
        if (childFiles == null || childFiles.length == 0) {
            log.info(parent + " has no children A");
            return null;
        }

        List<VNode> a = new ArrayList<VNode>();
        for (File childFile : childFiles) {
            VNodeHeader vNodeHeader = new VNodeHeader(depth, childFile.getName(), childFile.isDirectory());
            Rejection rejection = vNodeHeaderFilter.accept(vNodeHeader);
            if (rejection == null) {
                VNode childVNode = createVNode(childFile, depth );
                if (childVNode != null) {
                    a.add(childVNode);
                }
            } else {
//                rejection.print();
            }
        }

        if (a.size() == 0) {
            log.info(parent + " has no children B");
            return null;
        }

        return a;
    }

    private static Log log = LogFactory.getLog(FileSystemVNodeBuilder.class);

    private ContentDetail getFileDetails(File f) {
        ObjectId fullSha = ImageUtil.getFingerprintGitStyle(f);
        boolean emptyPng =  rtRepo.isEmptyPng(f.getAbsolutePath(),fullSha, Files.newInputStreamSupplier(f));
        return new ContentDetail(fullSha,emptyPng);
    }

    public static final VNodeHeaderFilter INCLUDE_ALL_VFILE_FILTER = new VNodeHeaderFilter() {
        @Override public Rejection accept(VNodeHeader vNodeHeader) {
            return null;
        }
    };

    public static final FileFilter EXCLUDE_HIDDEN_FILE_FILTER = new FileFilter() {
        @Override public boolean accept(File f) {
            return !isHidden(f);
        }
    };

    public static boolean isHidden(File f) {
        return f.isHidden() || f.getName().charAt(0) == '.';
    }

}
