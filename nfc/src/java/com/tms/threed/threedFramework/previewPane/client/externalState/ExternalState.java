package com.tms.threed.threedFramework.previewPane.client.externalState;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedFramework.featureModel.shared.picks.PicksChangeHandler;
import com.tms.threed.threedFramework.previewPane.client.ThreedModelServiceJson;
import com.tms.threed.threedFramework.previewPane.client.externalState.picks.PicksChangeHandlers;
import com.tms.threed.threedFramework.previewPane.client.externalState.raw.ExternalStateChangeEvent;
import com.tms.threed.threedFramework.previewPane.client.externalState.raw.ExternalStateSnapshot;
import com.tms.threed.threedFramework.previewPanel.client.ChatInfo;
import com.tms.threed.threedFramework.threedModel.client.RawPicksSnapshot;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;
import com.tms.threed.threedFramework.util.gwtUtil.client.Console;
import com.tms.threed.threedFramework.util.gwtUtil.client.events2.ValueChangeHandlers;

public class ExternalState {

    private final ValueChangeHandlers chatChangeHandlers;
    private final ValueChangeHandlers msrpChangeHandlers;
    private final PicksChangeHandlers picksChangeHandlers;

    private final ThreedModel threedModel;

    private ExternalStateSnapshot currentState;

    public ExternalState(ThreedModelServiceJson threedModelService) {
        threedModel = threedModelService.fetchThreedModelFromPage();
        if (threedModel == null) {
            throw new IllegalStateException("fetchThreedModelFromPage() returned null. See html comments for more details.");
        }

        chatChangeHandlers = new ValueChangeHandlers(this);
        msrpChangeHandlers = new ValueChangeHandlers(this);
        picksChangeHandlers = new PicksChangeHandlers(threedModel.getFeatureModel());
    }

    public void updateExternalState(ExternalStateSnapshot newState) {
        if (newState == null) return;

        ExternalStateChangeEvent ev = new ExternalStateChangeEvent(currentState, newState);
        this.currentState = newState;

        if (!ev.hasChanged()) return;

        if (ev.chatInfoChanged()) {
            Console.log("ChatInfoChange: " + newState.getChatInfo());
            try {
                chatChangeHandlers.fire(newState.getChatInfo());
            } catch (Exception e) {
                Console.log("\t Unexpected exception processing ChatInfoChange [" + e + "]");
                e.printStackTrace();
            }
        }

        if (ev.msrpChanged()) {
            Console.log("MsrpChange: " + newState.getMsrp());
            try {
                msrpChangeHandlers.fire(newState.getMsrp());
            } catch (Exception e) {
                Console.log("\t Unexpected exception processing MsrpChange [" + e + "]");
                e.printStackTrace();
            }
        }

        if (ev.rawPicksChanged()) {
            Console.log("PicksChange: " + newState.getPicksRaw());
            try {
                picksChangeHandlers.fire(newState.getPicksRaw());
            } catch (Exception e) {
                Console.log("\t Unexpected exception processing PicksChange [" + e + "]");
                e.printStackTrace();
            }
        }

    }

    public ThreedModel getThreedModel() {
        return threedModel;
    }

    public HandlerRegistration addChatInfoChangeHandler(ValueChangeHandler<ChatInfo> handler) {
        return chatChangeHandlers.addHandler(ValueChangeEvent.getType(), handler);
    }

    public HandlerRegistration addMsrpChangeHandler(ValueChangeHandler<String> handler) {
        return msrpChangeHandlers.addHandler(ValueChangeEvent.getType(), handler);
    }

    public HandlerRegistration addPicksChangeHandler(PicksChangeHandler handler) {
        return picksChangeHandlers.addPicksChangeHandler(handler);
    }

//    public SeriesInfo getSeries() {
//        return currentState == null ? null : currentState.getSeriesInfo();
//    }

    public String getMsrp() {
        return currentState == null ? null : currentState.getMsrp();
    }

    public RawPicksSnapshot getPicksRaw() {
        return currentState == null ? null : currentState.getPicksRaw();
    }

    public ChatInfo getChatInfo() {
        return currentState == null ? null : currentState.getChatInfo();
    }

}