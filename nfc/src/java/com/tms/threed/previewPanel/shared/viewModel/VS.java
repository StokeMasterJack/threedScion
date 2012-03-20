package com.tms.threed.previewPanel.shared.viewModel;

public class VS {

    private final int viewCount;

    private int viewForPanelZero = 0;

    public VS(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getViewCount() {
        return viewCount;
    }

    public int getPanelCount() {
        return viewCount;
    }

    public void setViewForPanelZero(int viewForPanelZero) {
        if (viewForPanelZero < 0 || viewForPanelZero > viewCount) throw new IllegalArgumentException();
        this.viewForPanelZero = viewForPanelZero;
    }

    public int getView(int panel) {
        return (viewForPanelZero + panel) % viewCount;
    }

    public int getPanel(int view) {
        return ((view - viewForPanelZero) +  viewCount)  % viewCount;
    }

    public void printViews() {
        System.out.println("viewCount = [" + viewCount + "]");
        System.out.println("viewForPanelZero = [" + viewForPanelZero + "]");

        System.out.println("Views");
        for (int view = 0; view < getViewCount(); view++) {
            System.out.println("\t view[" + view + "]  =>  panel[" + getPanel(view) + "]");
        }

    }

    public void printPanels() {
        System.out.println("viewCount = [" + viewCount + "]");
        System.out.println("viewForPanelZero = [" + viewForPanelZero + "]");

        System.out.println("Panels");
        for (int panel = 0; panel < getPanelCount(); panel++) {
            System.out.println("\t panel[" + panel + "]  =>  view[" + getView(panel) + "]");
        }
    }

}
