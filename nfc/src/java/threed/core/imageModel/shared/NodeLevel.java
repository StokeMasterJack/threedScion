package threed.core.imageModel.shared;

public class NodeLevel {

    private final String name;
    private final String depth;

    private NodeLevel(String name, String depth) {
        this.name = name;
        this.depth = depth;
    }

    public static final NodeLevel SERIES = new NodeLevel("SERIES", "0");
    public static final NodeLevel VIEW = new NodeLevel("VIEW", "1");
    public static final NodeLevel LAYER = new NodeLevel("LAYER", "2");
    public static final NodeLevel FEATURE_OR_PNG = new NodeLevel("FEATURE_OR_PNG", "3+");

    public static NodeLevel get(int depth) {
        if (depth == 0) return SERIES;
        else if (depth == 1) return VIEW;
        else if (depth == 2) return LAYER;
        else if (depth > 2) return FEATURE_OR_PNG;
        else throw new IllegalStateException();
    }

//    public static NodeLevel get(VNode vNode) {
//        return getByDepth(vNode.getDepth());
//    }

    public String getName() {
        return name;
    }

    @Override public String toString() {
        return depth + ": " + name;
    }

    public boolean isSeries() {
        return this == SERIES;
    }

    public boolean isView() {
        return this == VIEW;
    }

    public boolean isLayer() {
        return this == LAYER;
    }

    public boolean isFeatureOrPng() {
        return this == FEATURE_OR_PNG;
    }

}
