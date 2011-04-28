package com.tms.threed.threedFramework.previewPane.client;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.FixResult;
import com.tms.threed.threedFramework.featureModel.shared.Fixer;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.threedFramework.previewPane.client.externalState.raw.ExternalStateSnapshot;
import com.tms.threed.threedFramework.previewPane.client.nonFlashConfig.ExternalState;
import com.tms.threed.threedFramework.previewPane.client.notification.AccessoryWithFlashOrientationHandler;
import com.tms.threed.threedFramework.previewPane.client.notification.NotificationCenterBridge;
import com.tms.threed.threedFramework.previewPane.client.series.PreviewPaneContext;
import com.tms.threed.threedFramework.previewPane.client.threedServiceClient.ThreedModelServiceJson;
import com.tms.threed.threedFramework.previewPanel.client.ChatInfo;
import com.tms.threed.threedFramework.previewPanel.client.PreviewPanelContext;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;

import java.util.Set;

import static com.tms.threed.threedFramework.util.date.shared.StringUtil.isEmpty;

public class PreviewPaneImpl extends PreviewPane {

    private final ExternalState externalState;
    private final PreviewPaneContext previewPaneContext;


    public PreviewPaneImpl() {

        externalState = new ExternalState(new ThreedModelServiceJson());

        final ThreedModel threedModel = externalState.getThreedModel();
        final FeatureModel featureModel = threedModel.getFeatureModel();

        final PreviewPanelContext previewPanelContext = new PreviewPanelContext(threedModel);

        previewPaneContext = new PreviewPaneContext(previewPanelContext, threedModel);

        externalState.addPicksChangeHandler(new PicksChangeHandler() {
            @Override public void onPicksChange(PicksChangeEvent e) {
                try {

                    Var blinkVar = e.getBlinkAccessory();


                    Set<Var> currentTrueUiVars = e.getCurrentTrueUiVars();
                    FixResult fixResult = Fixer.fix(featureModel, currentTrueUiVars);

                    previewPaneContext.setPicks(fixResult);
                    previewPaneContext.setMaybeBlinkVar(blinkVar);
                    previewPaneContext.setJpgWidth(JpgWidth.W_STD);
                    previewPaneContext.setPngMode(false);

                    if (fixResult.isValidBuild()) {
                        previewPaneContext.refreshImagePanels();
                    } else {
                        Console.log("\t\tUnexpected exception processing PicksChangeEvent");
                        Console.log("\t\t\tNewPicks[" + fixResult.getErrorMessage() + "]");
                    }


                } catch (Exception e1) {
                    Console.log("\t\tUnexpected exception processing PicksChangeEvent");
                    Console.log("\t\t\t " + e1);
                    e1.printStackTrace();
                }
            }
        });

        externalState.addChatInfoChangeHandler(new ValueChangeHandler<ChatInfo>() {
            @Override public void onValueChange(ValueChangeEvent<ChatInfo> event) {
                ChatInfo chatInfo = event.getValue();
                previewPaneContext.setChatInfo(chatInfo);
            }
        });

        externalState.addMsrpChangeHandler(new ValueChangeHandler<String>() {
            @Override public void onValueChange(ValueChangeEvent<String> event) {
                previewPaneContext.setMsrp(event.getValue());
            }
        });

        NotificationCenterBridge.addAccessoryWithFlashOrientationHandler(new AccessoryWithFlashOrientationHandler() {
            @Override public void handleEvent(int orientation) {
                if (previewPaneContext == null) return;
                previewPaneContext.setViewAndAngle(orientation);
            }
        });

        initWidget(previewPanelContext.getPreviewPanel());
    }

    public void test() throws Exception {

    }

    public SeriesKey getSeriesKey() {
        return externalState.getThreedModel().getSeriesKey();
    }

    @Override
    public void updateImage(
            String flash_key,
            String modelCode,
            String option,
            String exteriorColor,
            String interiorColor,
            String accessory,
            String msrp,
            String seriesname,
            String helpimgid,
            String helpimgurl,
            String flashdescription) {

        if (isEmpty(modelCode)) throw new IllegalArgumentException("modelCode must not be null");

        ExternalStateSnapshot stateSnapshot = new ExternalStateSnapshot(modelCode, exteriorColor, interiorColor, option, accessory, msrp, helpimgid, helpimgurl, flash_key, flashdescription);
        externalState.updateExternalState(stateSnapshot);

    }

    public void updateImage2(ExternalStateSnapshot externalStateSnapshot) {
        externalState.updateExternalState(externalStateSnapshot);
    }


}
