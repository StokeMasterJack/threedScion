package com.tms.threed.threedFramework.threedAdmin.main.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.tms.threed.threedFramework.threedAdmin.main.client.services.SeriesKeys;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.util.gwtUtil.client.events2.SelectionHandlers;

import java.util.SortedSet;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.getSimpleName;

public class SeriesPickerPanel extends Composite {

    private SelectionHandlers<SeriesKey> seriesSelectionHandlers = new SelectionHandlers<SeriesKey>(this);

    private HTML header;
    private Tree tree;

    private final DockLayoutPanel outer = new DockLayoutPanel(Style.Unit.EM);

    public SeriesPickerPanel() {


        ScrollPanel scrollPanel = new ScrollPanel();
        scrollPanel.setWidget(getTree());

        outer.add(scrollPanel);
        Style style = outer.getElement().getStyle();
        style.setBackgroundColor("white");
        style.setBorderStyle(Style.BorderStyle.SOLID);

        initWidget(outer);

    }

    public void addSeriesSelectionHandler(SelectionHandler<SeriesKey> handler) {
        seriesSelectionHandlers.addSelectionHandler(handler);
    }

    public Tree getTree() {
        if (tree == null) {
            tree = new Tree();
//            tree.getElement().getStyle().setBackgroundColor("white");

            tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

                @Override public void onSelection(SelectionEvent<TreeItem> event) {
                    TreeItem selectedItem = event.getSelectedItem();
                    if (selectedItem instanceof SeriesKeyTreeItem) {
                        SeriesKey seriesKey = ((SeriesKeyTreeItem) selectedItem).getSeriesKey();
                        seriesSelectionHandlers.fire(seriesKey);
                    }
                }
            });


        }
        return tree;
    }

    public void setSeriesKeys(SeriesKeys seriesKeys) {
        Tree t = getTree();
        for (int year : seriesKeys.getYears()) {
            TreeItem yearTreeItem = new TreeItem(year + "");
            SortedSet<SeriesKey> seriesKeysForYear = seriesKeys.getSeriesKeys(year);
            for (SeriesKey seriesKey : seriesKeysForYear) {
                SeriesKeyTreeItem seriesNameTreeItem = new SeriesKeyTreeItem(seriesKey);
                yearTreeItem.addItem(seriesNameTreeItem);
            }
            t.addItem(yearTreeItem);
        }
    }

    private class SeriesKeyTreeItem extends TreeItem {

        private final SeriesKey seriesKey;

        private SeriesKeyTreeItem(SeriesKey seriesKey) {
            super(seriesKey.getName());
            this.seriesKey = seriesKey;
        }

        public SeriesKey getSeriesKey() {
            return seriesKey;
        }
    }


}
