package c3i.smartClient.client.widgets.blink;

import c3i.imageModel.shared.JpgWidth;
import c3i.smartClient.client.skins.bytSkin.chatPanel.ChatInfo;
import smartsoft.util.gwt.client.Browser;
import smartsoft.util.shared.Path;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.imageModel.shared.Slice;
import c3i.core.threedModel.shared.ThreedModel;

public class Blink {

    Var maybeBlinkVar;
    ThreedModel threedModel;
    FeatureModel featureModel;
    FixedPicks fixResult;
    Path repoBaseUrl;
    Slice slice;

    BlinkOverlay blinkOverlay;

    private boolean isBlinkSeries() {
        return getSeriesKey().isa(SeriesKey.FOUR_RUNNER) || getSeriesKey().isa(SeriesKey.SIENNA);
    }

    private SeriesKey getSeriesKey() {
        return null; //stubbed
    }


    private void maybeBlink() {
        if (maybeBlinkVar == null) {
            return;
        }
        if (!isBlinkSeries()) {
            return;
        }
        if (Browser.isIe6()) {
            return;
        }
        if (!maybeBlinkVar.isAccessory(featureModel)) {
            return;  //is this redundant??
        }


        if (fixResult.isInvalidBuild()) {
            return;
        }

        Path blinkPngUrl = threedModel.getBlinkPngUrl(slice, fixResult, maybeBlinkVar, repoBaseUrl);

        //null blinkPngUrl means that accessory is not visible at the current angle
        if (blinkPngUrl == null || picksError()) {
            //skip
        } else {
            maybeBlinkVar = null;
            blinkOverlay.doFeatureBlink(blinkPngUrl);
        }
    }

    private boolean picksError() {
        return false; //stubbed
    }


    public void setFixResult(FixedPicks fixResult) {

    }

    public void setMaybeBlinkVar(Var blinkVar) {

    }

    public void setJpgWidth(JpgWidth wStd) {

    }

    public void setPngMode(boolean b) {

    }

    public void refreshImagePanels() {

    }

    public void setChatInfo(ChatInfo chatInfo) {

    }

    public void setMsrp(String value) {



    }

    public void setViewAndAngle(int orientation) {

    }
}
