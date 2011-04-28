package com.tms.threed.threedFramework.imageModel.server;

import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.imageModel.shared.ImPng;
import com.tms.threed.threedFramework.imageModel.shared.NodeLevel;
import com.tms.threed.threedFramework.threedCore.shared.SeriesInfo;
import com.tms.threed.threedFramework.threedCore.shared.SeriesInfoBuilder;
import com.tms.threed.threedFramework.util.lang.shared.Strings;
import com.tms.threed.threedFramework.util.vnode.server.Rejection;
import com.tms.threed.threedFramework.util.vnode.server.VNodeHeader;
import com.tms.threed.threedFramework.util.vnode.server.VNodeHeaderFilter;

public class ImVNodeHeaderFilter implements VNodeHeaderFilter {

    private final FeatureModel featureModel;
    private final String seriesName;
    private final SeriesInfo seriesInfo;

    public ImVNodeHeaderFilter(FeatureModel featureModel) {
        this.featureModel = featureModel;
        this.seriesName = featureModel.getSeriesName();
        this.seriesInfo = SeriesInfoBuilder.createSeriesInfo(featureModel.getSeriesKey());
    }

    Rejection veto(VNodeHeader vNode, String reason) {
        return new Rejection(vNode, reason);
    }

    @Override public Rejection accept(VNodeHeader vNode) {
        NodeLevel nl = NodeLevel.get(vNode.depth);
        if (nl.isSeries()) {
            if (!isValidSeriesName(vNode.name)) {
                return veto(vNode, "inValidLocalSeriesName["+vNode.name);
            }
        } else if (nl.isView()) {
            if (!vNode.directory) return veto(vNode, "non-dir view");
            if (!isValidViewName(vNode.name)) return veto(vNode, "inValidLocalViewName");
        } else if (nl.isLayer()) {
            if (!vNode.directory) return veto(vNode, "non-dir layer");
        } else {
            if (!vNode.directory) {
                //should be a png
                boolean validLocalName = ImPng.isValidLocalName(vNode.name);
                if (!validLocalName) return veto(vNode, "inValidLocalPngName");
                else return null;
            } else {//directory
                //should be a feature
                if (Strings.containsNonWordChar(vNode.name)) {
                    return veto(vNode, "feature code containsNonwordChar");
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

    private boolean isValidViewName(String nodeName) {
        return seriesInfo.isValidViewName(nodeName);
    }

}
