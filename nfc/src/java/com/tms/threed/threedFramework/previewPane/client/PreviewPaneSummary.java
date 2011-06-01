package com.tms.threed.threedFramework.previewPane.client;

import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.FixResult;
import com.tms.threed.threedFramework.featureModel.shared.Fixer;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.threedFramework.previewPane.client.externalState.raw.ExternalStateSnapshot;
import com.tms.threed.threedFramework.previewPane.client.nonFlashConfig.ExternalState;
import com.tms.threed.threedFramework.previewPane.client.summaryPane.SummaryPanelContext;
import com.tms.threed.threedFramework.previewPane.client.summaryPane.SummarySeriesContext;
import com.tms.threed.threedFramework.previewPane.client.threedServiceClient.ThreedModelServiceJson;
import com.tms.threed.threedFramework.previewPanel.client.summaryPanel.SummaryPanel;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;

import java.util.Set;

public class PreviewPaneSummary extends PreviewPane {

    private ExternalState externalState;
    private SummarySeriesContext previewPaneContext;
    private FeatureModel featureModel;
    private ThreedModel threedModel;

    public PreviewPaneSummary() {

        externalState = new ExternalState(new ThreedModelServiceJson());

        threedModel = externalState.getThreedModel();
        featureModel = externalState.getThreedModel().getFeatureModel();


        previewPaneContext = new SummarySeriesContext(threedModel);

        SummaryPanelContext summaryPanelContext = previewPaneContext.getPreviewPanel();

        SummaryPanel summaryPanel = summaryPanelContext.getSummaryPanel();




        externalState.addPicksChangeHandler(new PicksChangeHandler() {
            @Override public void onPicksChange(PicksChangeEvent e) {

                    Set<Var> currentTrueUiVars = e.getCurrentTrueUiVars();
                    FixResult fixResult = Fixer.fix(featureModel, currentTrueUiVars);

                    previewPaneContext.setPicks(fixResult);

                    if (fixResult.isValidBuild()) {
                        previewPaneContext.refreshImagePanels();
                    } else {
                        Console.log("\t\tUnexpected exception processing PicksChangeEvent");
                        Console.log("\t\t\tNewPicks[" + fixResult.getErrorMessage() + "]");
                    }

            }
        });



        initWidget(summaryPanel);
    }

    public SeriesKey getSeriesKey() {
        return externalState.getThreedModel().getSeriesKey();
    }

    @Override
    public void updateImage(
            String flash_key,
            String modelCode,
            String optionCodes,
            String exteriorColor,
            String interiorColor,
            String accessoryCodes,
            String msrp,
            String seriesName,
            String chatVehicleIconMediaId,
            String chatActionUrl,
            String flashDescription) {

        this.externalState.updateExternalState(new ExternalStateSnapshot(modelCode, exteriorColor, interiorColor, optionCodes, accessoryCodes, msrp, chatVehicleIconMediaId, chatActionUrl, flash_key, flashDescription));

    }

    public void updateImage(ExternalStateSnapshot externalStateSnapshot) {
        this.externalState.updateExternalState(externalStateSnapshot);
    }


}