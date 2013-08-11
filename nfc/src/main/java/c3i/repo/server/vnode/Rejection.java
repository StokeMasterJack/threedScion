package c3i.repo.server.vnode;

public class Rejection {

    private final VNodeHeader vNode;
    private final String reason;

    public Rejection(VNodeHeader vNode, String reason) {
        this.vNode = vNode;
        this.reason = reason;
    }

    public VNodeHeader getVNodeHeader() {
        return vNode;
    }

    public String getReason() {
        return reason;
    }

    public void print() {
        System.out.println("VNodeHeader rejected [" + vNode + "]. Reason[" + getReason() + "]");
    }

    @Override public String toString() {return reason + "  " + vNode.name;}
}
