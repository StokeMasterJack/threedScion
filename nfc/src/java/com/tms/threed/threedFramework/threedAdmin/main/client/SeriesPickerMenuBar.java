package com.tms.threed.threedFramework.threedAdmin.main.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.tms.threed.threedFramework.repo.shared.SeriesNamesWithYears;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SeriesPickerMenuBar extends MenuBar {

    private final SeriesPickerHandler seriesPickerHandler;

    public SeriesPickerMenuBar(SeriesPickerHandler seriesPickerHandler) {
        super(true);
        this.seriesPickerHandler = seriesPickerHandler;
    }

    public void populate(ArrayList<SeriesNamesWithYears> seriesNames) {
        clearItems();

        Collections.sort(seriesNames, new Comparator<SeriesNamesWithYears>() {
            @Override public int compare(SeriesNamesWithYears o1, SeriesNamesWithYears o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (SeriesNamesWithYears seriesName : seriesNames) {
            String name = seriesName.getName();
            MenuItem menuItem = createMenuItem(name, seriesName.getYears());
            addItem(menuItem);
        }
    }


    private class OpenSeriesCommand implements Command {

        private final SeriesKey seriesKey;

        private OpenSeriesCommand(SeriesKey seriesKey) {
            this.seriesKey = seriesKey;
        }

        @Override
        public void execute() {
            seriesPickerHandler.onSeriesPicked(seriesKey);
        }

    }



    private MenuItem createMenuItem(String seriesName, ArrayList<Integer> years) {


        MenuBar yearsSubMenu = new MenuBar();
        for (Integer year : years) {
            SeriesKey sk = new SeriesKey(year, seriesName);
            yearsSubMenu.addItem(year + "", new OpenSeriesCommand(sk));
        }


        return new MenuItem(seriesName, yearsSubMenu);
    }

    public static interface SeriesPickerHandler {
        void onSeriesPicked(SeriesKey seriesKey);
    }

}
