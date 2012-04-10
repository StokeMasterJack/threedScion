package com.tms.threedToyota.byt.client.summary;

import threed.skin.summaryPanel.client.PreviewPanelSummary;
import threed.skin.summaryPanel.client.PreviewPanelSummaryContext;
import threed.admin.client.toMove.SummarySeriesContext;
import threed.core.featureModel.shared.FeatureModel;
import threed.core.featureModel.shared.FixResult;
import threed.core.featureModel.shared.picks.PicksChangeEvent;
import threed.core.featureModel.shared.picks.PicksChangeHandler;
import threed.smartClient.client.api.ThreedModelClient;
import threed.core.threedModel.shared.SeriesKey;
import threed.core.threedModel.shared.ThreedModel;
import com.tms.threedToyota.byt.client.PreviewPane;
import com.tms.threedToyota.byt.client.externalState.ExternalState;
import com.tms.threedToyota.byt.client.externalState.raw.ExternalStateSnapshot;
import smartsoft.util.gwt.client.Console;

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
            @Override
            public void onPicksChange(PicksChangeEvent e) {
                FixResult fixResult = featureModel.fixup(e.getCurrentTrueUiVars());

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