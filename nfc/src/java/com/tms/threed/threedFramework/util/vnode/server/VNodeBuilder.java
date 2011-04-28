package com.tms.threed.threedFramework.util.vnode.server;

abstract public class VNodeBuilder {

    protected VNodeHeaderFilter vNodeHeaderFilter;

    public void setVNodeHeaderFilter(VNodeHeaderFilter vNodeHeaderFilter) {
        this.vNodeHeaderFilter = vNodeHeaderFilter;
    }

    public abstract VNode buildVNode();
}
