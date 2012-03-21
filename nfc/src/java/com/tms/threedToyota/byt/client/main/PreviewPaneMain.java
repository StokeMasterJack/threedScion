package com.tms.threedToyota.byt.client.main;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.tms.threed.previewPanel.client.main.PreviewPanelMainContext;
import com.tms.threed.previewPanel.client.main.chatPanel.ChatInfo;
import com.tms.threed.threedAdmin.client.toMove.PreviewPaneContext;
import com.tms.threed.threedCore.featureModel.shared.Assignments;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.featureModel.shared.Fixer;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.threedCore.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.threedCore.threedModel.client.ThreedModelClient;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import com.tms.threedToyota.byt.client.PreviewPane;
import com.tms.threedToyota.byt.client.externalState.ExternalState;
import com.tms.threedToyota.byt.client.externalState.raw.ExternalStateSnapshot;
import com.tms.threedToyota.byt.client.notification.AccessoryWithFlashOrientationHandler;
import com.tms.threedToyota.byt.client.notification.NotificationCenterBridge;
import smartsoft.util.gwt.client.Console;

import java.util.List;
import java.util.Set;

import static smartsoft.util.date.shared.StringUtil.isEmpty;

public class PreviewPaneMain extends PreviewPane {

    private final ExternalState externalState;
    private final PreviewPaneContext previewPaneContext;


    public PreviewPaneMain() {

        externalState = new ExternalState(new ThreedModelClient(null));

        final ThreedModel threedModel = externalState.getThreedModel();
        final FeatureModel featureModel = threedModel.getFeatureModel();

        final PreviewPanelMainContext previewPanelContext = new PreviewPanelMainContext(threedModel);

        previewPaneContext = new PreviewPaneContext(previewPanelContext, threedModel);

        externalState.addPicksChangeHandler(new PicksChangeHandler() {
            @Override
            public void onPicksChange(PicksChangeEvent e) {
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
            @Override
            public void onValueChange(ValueChangeEvent<ChatInfo> event) {
                ChatInfo chatInfo = event.getValue();
                previewPaneContext.setChatInfo(chatInfo);
            }
        });

        externalState.addMsrpChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> event) {
                previewPaneContext.setMsrp(event.getValue());
            }
        });

        NotificationCenterBridge.addAccessoryWithFlashOrientationHandler(new AccessoryWithFlashOrientationHandler() {
            @Override
            public void handleEvent(int orientation) {
                if (previewPaneContext == null) return;
                previewPaneContext.setViewAndAngle(orientation);
            }
        });

        initWidget(previewPanelContext.getPreviewPanelMain());
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
