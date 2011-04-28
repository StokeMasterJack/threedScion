package com.tms.threed.threedFramework.previewPane.client.notification;

import com.tms.threed.threedFramework.util.gwtUtil.client.Console;

/**
 * This class receives events from various parts of the system and
 * dispatches them as needed
 */
public class NotificationCenterBridge {

    public static final String TabChanged = "Tab Changed";
    public static final String MSRPUpdated = "MSRP Updated";
    public static final String AccessoryWithFlashOrientation = "Accessory With Flash Orientation";
    public static final String AccessoryWithoutFlashKey = "Accessory Without Flash Key";
    public static final String ColorWithFlashOrientation = "Color With Flash Orientation";
    public static final String ModelChanged = "Model Changed";
    public static final String BuildPageReady = "Build Page Ready";
    public static final String ErrorDialogTriggered = "Error Dialog Triggered";
    public static final String PanelCompleteStatusChange = "PanelComplete Status Change";
    public static final String ConfirmPanelConfirmed = "Confirm Panel Confirmed";
    public static final String DisplayTempExtColor = "Display Temp Ext Color";
    public static final String RemoveTempExtColor = "Remove Temp Ext Color";
    public static final String PackageOverlayTrigger = "Package Overlay: Trigger";
    public static final String ShowMaps = "Show Maps";

    private static final String[] eventNames = {
            TabChanged,
            MSRPUpdated,
            AccessoryWithFlashOrientation,
            AccessoryWithoutFlashKey,
            ColorWithFlashOrientation,
            ModelChanged,
            BuildPageReady,
            ErrorDialogTriggered,
            PanelCompleteStatusChange,
            ConfirmPanelConfirmed,
            DisplayTempExtColor,
            RemoveTempExtColor,
            PackageOverlayTrigger,
            ShowMaps
    };


    public static void subscribe() {
        for (String eventNames : NotificationCenterBridge.eventNames) {
            addDefaultHandler(eventNames);
        }
    }

    public static void addDefaultHandler(final String eventName) {
        addHandler(eventName, new Command() {
            @Override public void execute() {
                Console.log("Event[" + eventName + "]");
            }
        });
    }

    public static void addMsrpUpdateHandler(Command command) {
        addHandler(MSRPUpdated, command);
    }

    public static void addModelChangedHandler(Command command) {
        addHandler(ModelChanged, command);
    }

    public static void addTabChangedHandler(Command command) {
        addHandler(TabChanged, command);
    }

//    public static void addAccessoryWithFlashOrientationHandler(Command command) {
//        addHandler(AccessoryWithFlashOrientation, command);
//    }

    public static void addAccessoryWithoutFlashKeyHandler(Command command) {
        addHandler(AccessoryWithoutFlashKey, command);
    }

    public static void addColorWithFlashOrientationHandler(Command command) {
        addHandler(ColorWithFlashOrientation, command);
    }

    public static void addBuildPageReadyHandler(Command command) {
        addHandler(BuildPageReady, command);
    }

    public static void addErrorDialogTriggeredHandler(Command command) {
        addHandler(ErrorDialogTriggered, command);
    }

    public static void addPanelCompleteStatusChangeHandler(Command command) {
        addHandler(PanelCompleteStatusChange, command);
    }

    public static void addHandler(String eventName, Command command) {
        addSubscriber2(eventName, command);
    }

    private static native void addSubscriber2(String eventName, Command command) /*-{
        var ff1 = command.@com.google.gwt.user.client.Command::execute();
        var ff2 = $entry(ff1);
        $wnd.NotificationCenter.addSubscriber(eventName, command, ff2);
    }-*/;

    public static native void addAccessoryWithFlashOrientationHandler(AccessoryWithFlashOrientationHandler handler) /*-{
        var ff1 = handler.@com.tms.threed.threedFramework.previewPane.client.notification.AccessoryWithFlashOrientationHandler::handleEvent(Ljava/lang/String;I);
        $wnd.NotificationCenter.addSubscriber("Accessory With Flash Orientation", handler, ff1);
    }-*/;

    public static interface Command extends com.google.gwt.user.client.Command {

    }

    private NotificationCenterBridge() {}

    private static native void foo() /*-{
        var ff1 = App.vehicle
        var ff2 = $entry(ff1);
        $wnd.NotificationCenter.addSubscriber(eventName, command, ff2);
    }-*/;

}
