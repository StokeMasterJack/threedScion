package com.tms.threed.threedFramework.util.vnode.server;

import com.google.common.base.Preconditions;
import com.tms.threed.threedFramework.util.lang.shared.Path;
import com.tms.threed.threedFramework.util.lang.shared.Strings;
import org.eclipse.jgit.lib.ObjectId;

import java.util.List;


public class VNode {

    private VNode _parent;

    protected final int depth;
    protected final String name;
    protected final ObjectId fullSha;
    protected final boolean directory;
    protected final List<VNode> childNodes;

    /**
     * Create a directory VNode
     */
    public VNode(String name, List<VNode> childNodes, int depth) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(childNodes);
        Preconditions.checkArgument(!childNodes.isEmpty());

        this.name = name;
        this.fullSha = null;
        this.directory = true;
        this.depth = depth;

        for (VNode childNode : childNodes) {
            assert childNode != null;
            childNode.initParent(this);
        }

        this.childNodes = childNodes;
    }

//    public abstract InputSupplier<? extends InputStream> getContent();

    /**
     * Create a file VNode
     */
    public VNode(String name, ObjectId fullSha, int depth) {
        Preconditions.checkNotNull(name);
        Preconditions.checkNotNull(fullSha);

        this.name = name;
        this.fullSha = fullSha;
        this.directory = false;
        this.depth = depth;
        this.childNodes = null;
    }


    public ObjectId getFullSha() {
        return fullSha;
    }

    void initParent(VNode parent) {
        assert (!isRoot());
        assert (this._parent == null);
        assert (parent != null);
        this._parent = parent;
    }

    public String getName() {
        return name;
    }

    public List<VNode> getChildNodes() {
        return childNodes;
    }

    public boolean hasChildNodes() {
        if (childNodes == null) return false;
        if (childNodes.isEmpty()) return false;
        return true;
    }

    public boolean isDirectory() {
        return directory;
    }


    public Path getFullPath() {
        VNode parent = getParent();
        if (parent == null) return new Path(getName());
        return parent.getFullPath().append(getName());
    }

    public boolean isRoot() {
        return depth == 0;
    }

    public boolean isFile() {
        return !directory;
    }

    public void printTree() {
        int d = getDepth();
        System.out.println(Strings.tab(d) + name);
        if (!hasChildNodes()) return;
        for (VNode childNode : childNodes) {
            childNode.printTree();
        }
    }

    public int getDepth() {
        return depth;
    }

    public VNode getParent() {
        if (isParentReady()) return _parent;
        else throw new IllegalStateException("parent not set yet. Cannot access parent while node is being build");
    }

    public boolean isParentReady() {
        return isRoot() || _parent != null;
    }

//    public int getTotalNodeCount() {
//        Counter counter = new Counter();
//        count(counter);
//        return counter.count;
//    }

//    public VNode getChild(String name) {
//        for (VNode childNode : childNodes) {
//            if (childNode.getName().equals(name)) return childNode;
//        }
//        return null;
//    }

    public boolean containsChild(String name) {
        for (VNode childNode : childNodes) {
            if (childNode.getName().equals(name)) return true;
        }
        return false;
    }

//    private class Counter {
//        int count;
//
//        void incr() {count++;}
//    }

//    private void count(Counter counter) {
//        counter.incr();
//        if (hasChildNodes()) {
//            for (VNode node : childNodes) {
//                node.count(counter);
//            }
//        }
//    }

//    public void process(Command command) {
//        command.execute(this);
//        if (!hasChildNodes()) return;
//        for (VNode childNode : childNodes) {
//            childNode.process(command);
//        }
//    }

    @Override public String toString() {
        return getName();
    }

    //    @Override public void printTreeX() {
//        int d = getDepth();
//        System.out.println(tab(d) + getName());
//        if (isParent()) {
//            IsParent p = (IsParent) this;
//            for (Object o : p.getChildNodes()) {
//                ImNode n = (ImNode) o;
//                n.printTree();
//            }
//        }
//
//    }
}
