package c3i.repo.server.vnode;

import c3i.imageModel.shared.NodeLevel;
import c3i.imageModel.shared.SimpleFeatureModel;
import c3i.imageModel.shared.SrcPng;
import smartsoft.util.shared.Strings;

public class ImVNodeHeaderFilter implements VNodeHeaderFilter {

    private final SimpleFeatureModel featureModel;
    private final String seriesName;

    public ImVNodeHeaderFilter(SimpleFeatureModel featureModel) {
        this.featureModel = featureModel;
        this.seriesName = featureModel.getSeriesKey().getName();
    }

    Rejection veto(VNodeHeader vNode, String reason) {
        return new Rejection(vNode, reason);
    }

    @Override
    public Rejection accept(VNodeHeader vNode) {
        NodeLevel nl = NodeLevel.get(vNode.depth);
        if (nl.isSeries()) {
            if (!isValidSeriesName(vNode.name)) {
                return veto(vNode, "inValidLocalSeriesName[" + vNode.name);
            }
        } else if (nl.isView()) {
            if (!vNode.directory) return veto(vNode, "non-dir view");
        } else if (nl.isLayer()) {
            if (vNode.directory || vNode.name.equals("lift.txt")) {
                return null;
            } else {
                return veto(vNode, "non-dir layer");
            }
        } else {
            if (!vNode.directory) {
                //should be a png
                boolean validLocalName = SrcPng.isValidLocalName(vNode.name);
                if (!validLocalName) return veto(vNode, "inValidLocalPngName: " + vNode.name);
                else return null;
            } else {//directory
                //should be a feature
                if (Strings.containsNonWordChar(vNode.name)) {
                    return veto(vNode, "feature code contains Non-word Char");
                }
                String varCode = vNode.name;
                if (!featureModel.containsCode(varCode)) {
                    return veto(vNode, "feature code not in fm");
                }
            }
        }
        return null;
    }

    private boolean isValidSeriesName(String nodeName) {
        return seriesName.equals(nodeName);
    }


}
