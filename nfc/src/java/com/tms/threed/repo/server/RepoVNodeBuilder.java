package com.tms.threed.repo.server;

import com.google.common.base.Preconditions;
import com.tms.threed.repo.server.rt.RtRepo;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.repo.server.vnode.Rejection;
import com.tms.threed.repo.server.vnode.VNode;
import com.tms.threed.repo.server.vnode.VNodeBuilder;
import com.tms.threed.repo.server.vnode.VNodeHeader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jgit.lib.FileMode;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.TreeWalk;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class RepoVNodeBuilder extends VNodeBuilder {

    private final SeriesRepo seriesRepo;
    private final SeriesKey seriesKey;
    private final Repository repo;
    private final RevCommit revCommit;
    private final RtRepo rtRepo;

    private final SrcRepo srcRepo;

    private ObjectReader objectReader;
    private RevWalk revWalk;

    private final Stack<String> stack = new Stack<String>();


    public RepoVNodeBuilder(SeriesRepo seriesRepo, @Nonnull RevCommit revCommit, RtRepo rtRepo) {

        assert seriesRepo != null;
        assert revCommit != null;
        assert rtRepo != null;


        this.seriesRepo = seriesRepo;
        this.seriesKey = seriesRepo.getSeriesKey();
        this.revCommit = revCommit;
        this.rtRepo = rtRepo;


        this.srcRepo = seriesRepo.getSrcRepo();
        this.repo = srcRepo.getGitRepo();
    }


    private VNode buildTreeNode(ObjectId treeId, String name, int depth) throws IOException {
        stack.push(name);
//        System.err.println(stack);
        RevTree revTree = revWalk.parseTree(treeId);

        CanonicalTreeParser treeParser = new CanonicalTreeParser();
        treeParser.reset(objectReader, revTree);

        final TreeWalk treeWalk = new TreeWalk(repo);

        treeWalk.reset(); // drop the first empty tree, which we do not need here
        treeWalk.setRecursive(false);
        treeWalk.addTree(revTree);

        ArrayList<VNode> childNodes = new ArrayList<VNode>();
        while (treeWalk.next()) {

            ObjectId childObjectId = treeWalk.getObjectId(0);
            String childNodeName = treeWalk.getNameString();

            FileMode childMode = treeWalk.getFileMode(0);
            boolean childIsDirectory = childMode == FileMode.TREE;
            int childDepth = depth + 1;

            VNodeHeader vNodeHeader = new VNodeHeader(childDepth, childNodeName, childIsDirectory);

            assert vNodeHeaderFilter != null;

            Rejection rejection = vNodeHeaderFilter.accept(vNodeHeader);
            if (rejection == null) {
                VNode childVNode;
                if (childIsDirectory) {
                    childVNode = buildTreeNode(childObjectId, childNodeName, childDepth);
                    if (childVNode == null) {
//                        log.info("skipping treeNode " + stack + "\t" + childNodeName);
                    } else {
                        childNodes.add(childVNode);
                    }
                } else {
                    childVNode = buildBlobNode(childObjectId, childNodeName, stack, childDepth);
                    if (childVNode == null) {
//                        log.info("skipping blobNode " + stack + "\t" + childNodeName );
                    } else {
                        childNodes.add(childVNode);
                    }
                }


            } else {
//                rejection.print();
            }
        }

        stack.pop();

        if (childNodes.size() == 0) {
            log.debug("ImageModel folder[" + name + "] has no child nodes");
            return null;
        }

        return new VNode(name, childNodes, depth);

    }

    private static Log log = LogFactory.getLog(RepoVNodeBuilder.class);

    private VNode buildBlobNode(ObjectId objectId, String name, Stack<String> path, int depth) {
        boolean emptyPng = seriesRepo.isEmptyPng(path.toString() + "\t" + name, objectId);
        if (emptyPng) {
//             log.debug("Empty png[" + stack + "\t" + name);
            return null;
        }
        return new VNode(name, objectId, depth);
    }

//     private ContentDetail getFileDetails(File f) {
//        String fingerprint = ImageUtil.getFingerprint(f);
//        boolean emptyPng =  rtRepo.isEmptyPng(f.getAbsolutePath(),fingerprint, Files.newInputStreamSupplier(f));
//        return new ContentDetail(fingerprint,emptyPng);
//    }

    @Override public VNode buildVNode() {
        Preconditions.checkNotNull(vNodeHeaderFilter);
        try {

            objectReader = repo.newObjectReader();

            revWalk = new RevWalk(repo);
            RevTree revTree = revWalk.parseTree(revCommit);
            ObjectId treeId = revTree.getId();

            return buildTreeNode(treeId, seriesKey.getName(), 0);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (objectReader != null) objectReader.release();
            if (revWalk != null) revWalk.release();
        }


    }


}
