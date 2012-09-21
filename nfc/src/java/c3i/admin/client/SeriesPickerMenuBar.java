package c3i.admin.client;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import c3i.repo.shared.Series;
import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SeriesPickerMenuBar extends MenuBar {

    public SeriesPickerHandler onSeriesPicked;
    private BrandKey brandKey;

    public SeriesPickerMenuBar() {
        super(true);
    }

    public void populate(SeriesPickList seriesPickList) {
        this.brandKey = seriesPickList.getBrandKey();
        populate(seriesPickList.getSeriesList());
    }

    public void populate(ArrayList<Series> seriesNames) {
        clearItems();

        Collections.sort(seriesNames, new Comparator<Series>() {
            @Override
            public int compare(Series o1, Series o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        for (Series seriesName : seriesNames) {
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
            if(onSeriesPicked == null){
               throw new IllegalStateException("No onSeriesPicked handler");
            }
            onSeriesPicked.onSeriesPicked(seriesKey);
        }

    }


    private MenuItem createMenuItem(String seriesName, ArrayList<Integer> years) {
        MenuBar yearsSubMenu = new MenuBar();
        for (Integer year : years) {
            SeriesKey sk = new SeriesKey(brandKey, year, seriesName);
            yearsSubMenu.addItem(year + "", new OpenSeriesCommand(sk));
        }
        return new MenuItem(seriesName, yearsSubMenu);
    }
    


}
