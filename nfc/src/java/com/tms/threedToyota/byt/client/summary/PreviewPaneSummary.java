package com.tms.threedToyota.byt.client.summary;

import com.tms.threed.threedAdmin.client.toMove.SummarySeriesContext;
import com.tms.threed.threedCore.threedModel.client.ThreedModelClient;
import com.tms.threed.threedCore.featureModel.shared.FeatureModel;
import com.tms.threed.threedCore.featureModel.shared.FixResult;
import com.tms.threed.threedCore.featureModel.shared.Fixer;
import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedCore.featureModel.shared.picks.PicksChangeEvent;
import com.tms.threed.threedCore.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threedToyota.byt.client.PreviewPane;
import com.tms.threedToyota.byt.client.externalState.ExternalState;
import com.tms.threedToyota.byt.client.externalState.raw.ExternalStateSnapshot;
import com.tms.threed.previewPanel.client.summary.PreviewPanelSummary;
import com.tms.threed.previewPanel.client.summary.PreviewPanelSummaryContext;
import com.tms.threed.threedCore.threedModel.shared.SeriesKey;
import com.tms.threed.threedCore.threedModel.shared.ThreedModel;
import smartsoft.util.gwt.client.Console;

import java.util.Set;

public class PreviewPaneSummary extends PreviewPane {

    private ExternalState externalState;
    private SummarySeriesContext previewPaneContext;
    private FeatureModel featureModel;
    private ThreedModel threedModel;

    public PreviewPaneSummary() {

        externalState = new ExternalState(new ThreedModelClient());

        threedModel = externalState.getThreedModel();
        featureModel = externalState.getThreedModel().getFeatureModel();


        previewPaneContext = new SummarySeriesContext(threedModel);

        PreviewPanelSummaryContext summaryPanelContext = previewPaneContext.getPreviewPanel();

        PreviewPanelSummary summaryPanel = summaryPanelContext.getSummaryPanel();




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