package threed.repo.server.vnode;

public interface VNodeHeaderFilter {

    Rejection accept(VNodeHeader vNodeHeader);

}
