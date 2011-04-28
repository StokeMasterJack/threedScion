package com.tms.threed.threedFramework.previewPane.client;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;

import java.util.Date;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.isEmpty;

public class UrlParameters {

    public static final String SERIES_NAME_KEY = "s";
    public static final String YEAR_KEY = "y";
    public static final String SUMMARY_MODE_KEY = "summary";

    public static boolean isSummaryMode() {
        String sSummary = Window.Location.getParameter(SUMMARY_MODE_KEY);
        if (isEmpty(sSummary)) return false;
        if (sSummary.equals("true")) return true;
        return false;
    }

    public static SeriesKey getSeriesKey(SeriesKey fallback) {
        SeriesKey sk = getSeriesKey();
        return sk == null ? fallback : sk;
    }

    public static SeriesKey getSeriesKey() {
        String seriesName = Window.Location.getParameter(SERIES_NAME_KEY);
        if (seriesName == null) {
            return null;
        }

        String sYear = Window.Location.getParameter(YEAR_KEY);
        int iYear;
        if (sYear == null) {
            iYear = getCurrentYear() + 1;
        } else {
            iYear = Integer.parseInt(sYear);
        }
        return SeriesKey.get(iYear, seriesName);
    }

    public static int getCurrentYear() {
        return Integer.parseInt(DateTimeFormat.getFormat("yyyy").format(new Date()));
    }
}
