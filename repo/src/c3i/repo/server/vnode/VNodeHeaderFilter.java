package c3i.repo.server.vnode;

public interface VNodeHeaderFilter {

    Rejection accept(VNodeHeader vNodeHeader);

}
