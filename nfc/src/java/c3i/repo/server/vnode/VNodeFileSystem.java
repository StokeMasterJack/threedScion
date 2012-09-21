package c3i.repo.server.vnode;

import org.eclipse.jgit.lib.ObjectId;

import java.util.List;
import java.util.Properties;

public class VNodeFileSystem extends VNode {

    public VNodeFileSystem(String name, List<VNode> childNodes, int depth) {
        super(name, childNodes, depth);
    }

    @Override
    public Properties readFileAsProperties() {
        throw new IllegalStateException();
    }

    public VNodeFileSystem(String name, ObjectId fullSha, int depth) {
        super(name, fullSha, depth);
    }
}
