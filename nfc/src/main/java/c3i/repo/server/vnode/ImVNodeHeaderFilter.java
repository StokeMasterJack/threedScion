package c3i.repo.server.vnode;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.imageModel.shared.NodeLevel;
import c3i.core.imageModel.shared.SrcPng;
import c3i.core.threedModel.shared.SeriesInfo;
import c3i.core.threedModel.shared.SeriesInfoBuilder;
import smartsoft.util.shared.Strings;

public class ImVNodeHeaderFilter implements VNodeHeaderFilter {

    private final FeatureModel featureModel;
    private final String seriesName;

    public ImVNodeHeaderFilter(FeatureModel featureModel) {
        this.featureModel = featureModel;
        this.seriesName = featureModel.getSeriesName();
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
