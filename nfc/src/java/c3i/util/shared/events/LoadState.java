package c3i.util.shared.events;

import org.timepedia.exporter.client.Export;

public enum LoadState {

//    NOT_STARTED,
    FAILED, LOADED, LOADING;

//    @Export
//    public boolean isStarted() {
//        return !this.equals(LoadState.NOT_STARTED);
//    }
//
//    @Export
//    public boolean notStarted() {
//        return this.equals(LoadState.NOT_STARTED);
//    }

    @Export
    public boolean isLoaded() {
        return this.equals(LoadState.LOADED);
    }

    @Export
    public boolean isFailed() {
        return this.equals(LoadState.FAILED);
    }

    @Export
    public boolean isComplete() {
        return !isLoading();
    }

    @Export
    public boolean isLoading() {
        return this.equals(LoadState.LOADING);
    }

}
