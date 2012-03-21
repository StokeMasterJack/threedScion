package com.tms.threed.repo.server.vnode;

public interface VNodeHeaderFilter {

    Rejection accept(VNodeHeader vNodeHeader);

}
