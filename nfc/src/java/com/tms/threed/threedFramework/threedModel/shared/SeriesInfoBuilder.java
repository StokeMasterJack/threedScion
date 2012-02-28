package com.tms.threed.threedFramework.threedModel.shared;


public class SeriesInfoBuilder {

    private static final ViewKey[] standard = {ViewKey.createExteriorView(0), ViewKey.createInteriorView(1)};
    private static final ViewKey[] cargo = {ViewKey.createExteriorView(0), ViewKey.createInteriorView(1), ViewKey.createCargoView(2)};
    private static final ViewKey[] undercarriage = {ViewKey.createExteriorView(0), ViewKey.createInteriorView(1), ViewKey.createUndercarriageView(2)};


    private static final ViewKey[] all = {ViewKey.createExteriorView(0), ViewKey.createInteriorView(1)};

    public static SeriesInfo createSeriesInfo(SeriesKey seriesKey) {
        return new SeriesInfo(seriesKey, getViewKeys(seriesKey));
    }

    /*
    * todo This probably needs to be more data driven
    */
    static ViewKey[] getViewKeys(SeriesKey seriesKey) {
        if (seriesKey.isa(SeriesKey.TUNDRA)) return undercarriage;
        else if (seriesKey.isa(SeriesKey.SIENNA)) return cargo;
        else if (seriesKey.isa(SeriesKey.FOUR_RUNNER)) return cargo;
        else return standard;
    }

    public static SeriesInfo createDummySeriesInfo() {
        return new SeriesInfo(SeriesKey.DUMMY, standard);
    }

}
