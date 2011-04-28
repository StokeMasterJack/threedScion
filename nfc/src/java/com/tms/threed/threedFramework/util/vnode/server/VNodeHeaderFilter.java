package com.tms.threed.threedFramework.util.vnode.server;

public interface VNodeHeaderFilter {

    Rejection accept(VNodeHeader vNodeHeader);

}
