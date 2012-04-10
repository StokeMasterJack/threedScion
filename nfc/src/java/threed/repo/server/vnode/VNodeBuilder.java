package threed.repo.server.vnode;

abstract public class VNodeBuilder {

    protected VNodeHeaderFilter vNodeHeaderFilter;

    public void setVNodeHeaderFilter(VNodeHeaderFilter vNodeHeaderFilter) {
        this.vNodeHeaderFilter = vNodeHeaderFilter;
    }

    public abstract VNode buildVNode();
}
