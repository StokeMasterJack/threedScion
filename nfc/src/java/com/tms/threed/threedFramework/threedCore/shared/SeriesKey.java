package com.tms.threed.threedFramework.threedCore.shared;

import com.tms.threed.threedFramework.util.lang.shared.Path;
import com.tms.threed.threedFramework.util.lang.shared.Strings;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

public class SeriesKey implements Comparable<SeriesKey>, Serializable {

    private int year;
    private String name;

    public SeriesKey(final int year, @Nonnull final String seriesName) throws IllegalArgumentException {
        if (Strings.isEmpty(seriesName)) throw new IllegalArgumentException("name is required");
        this.year = year;

        String s1 = seriesName.trim();
        if (Strings.containsWhitespace(s1)) {
            s1 = s1.replace(" ", "");
        }


        this.name = s1.toLowerCase();
    }

    public SeriesKey(@Nonnull final String seriesYear, @Nonnull final String seriesName) throws IllegalArgumentException {
        if (Strings.isEmpty(seriesName)) throw new IllegalArgumentException("seriesName is required");
        if (Strings.isEmpty(seriesYear)) throw new IllegalArgumentException("seriesYear is required");

        if (seriesName.length() < 2) throw new IllegalArgumentException("seriesName must be at least 2 digits long");
        if (seriesYear.length() != 4) throw new IllegalArgumentException("seriesYear must be at 4 digits long");

        try {
            this.year = Integer.parseInt(seriesYear);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("seriesYear must be a 4 digit number");
        }

        String s1 = seriesName.trim();
        if (Strings.containsWhitespace(s1)) {
            s1 = s1.replace(" ", "");
        }


        this.name = s1.toLowerCase();
    }

    protected SeriesKey() {
    }

    public static int parserModelYear(String modelYear) {
        return Integer.parseInt(modelYear);
    }

    private static String getKeyAsString(int year, String name) {
        return year + "-" + name.toLowerCase();
    }

    public int getYear() {
        return year;
    }

    public String getName() {
        return name;
    }

    @Override public String toString() {
        return year + " " + name;
    }

    public String toStringPretty() {
        return getNamePretty() + " " + getYear();
    }

    public String getShortName() {
        return name + "-" + year;
    }

    public String getNamePretty() {
        return Strings.capFirstLetter(name);
    }


    public String getRepoName() {
        return name + "-" + year;
    }

    @Override
    public int hashCode() {
        int result = year;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (SeriesKey.class != o.getClass()) return false;
        SeriesKey that = (SeriesKey) o;
        return this.year == that.year && this.name.equals(that.name);
    }


    public static final String AVALON = "avalon";
    public static final String CAMRY = "camry";
    public static final String FJ_CRUISER = "fj";
    public static final String FOUR_RUNNER = "4runner";
    public static final String LAND_CRUISER = "landcruiser";
    public static final String RAV4 = "rav4";
    public static final String SIENNA = "sienna";
    public static final String SEQUOIA = "sequoia";
    public static final String TACOMA = "tacoma";
    public static final String TUNDRA = "tundra";
    public static final String VENZA = "venza";
    public static final String YARIS = "yaris";

    public static final int VENZA_ID = 28;
    public static final int CAMRY_ID = 3;
    public static final int SIENNA_ID = 14;
    public static final int TUNDRA_ID = 17;
    public static final int FOUR_RUNNER_ID = 1;

    private static String[] seriesNames = {
            SeriesKey.AVALON,
            SeriesKey.CAMRY,
            SeriesKey.FJ_CRUISER,
            SeriesKey.LAND_CRUISER,
            SeriesKey.RAV4,
            SeriesKey.SEQUOIA,
            SeriesKey.SIENNA,
            SeriesKey.TACOMA,
            SeriesKey.TUNDRA,
            SeriesKey.VENZA,
            SeriesKey.YARIS};


    public static List<SeriesKey> getAll() {
        return Arrays.asList(seriesKeys);
    }

    public static List<SeriesKey> getSeriesKeys() {
        return Arrays.asList(seriesKeys);
    }

    public static List<String> getSeriesNames() {
        return Arrays.asList(seriesNames);
    }

    public static final SeriesKey YARIS_2010 = new SeriesKey(2010, YARIS);
    public static final SeriesKey RAV4_2010 = new SeriesKey(2010, RAV4);
    public static final SeriesKey RAV4_2011 = new SeriesKey(2011, RAV4);
    public static final SeriesKey TACOMA_2011 = new SeriesKey(2011, TACOMA);
    public static final SeriesKey TUNDRA_2011 = new SeriesKey(2011, TUNDRA);
    public static final SeriesKey AVALON_2011 = new SeriesKey(2011, AVALON);
    public static final SeriesKey AVALON_2010 = new SeriesKey(2010, AVALON);
    public static final SeriesKey CAMRY_2011 = new SeriesKey(2011, CAMRY);
    public static final SeriesKey FJ_CRUISER_2010 = new SeriesKey(2010, FJ_CRUISER);
    public static final SeriesKey LAND_CRUISER_2010 = new SeriesKey(2010, LAND_CRUISER);
    public static final SeriesKey SEQUOIA_2011 = new SeriesKey(2011, SEQUOIA);
    public static final SeriesKey SIENNA_2011 = new SeriesKey(2011, SIENNA);
    public static final SeriesKey VENZA_2011 = new SeriesKey(2011, VENZA);
    public static final SeriesKey VENZA_2010 = new SeriesKey(2010, VENZA);
    public static final SeriesKey VENZA_2009 = new SeriesKey(2009, VENZA);

    private static SeriesKey[] seriesKeys = {
            SeriesKey.AVALON_2011,
            SeriesKey.CAMRY_2011,
            SeriesKey.FJ_CRUISER_2010,
            SeriesKey.LAND_CRUISER_2010,
            SeriesKey.RAV4_2010,
            SeriesKey.SEQUOIA_2011,
            SeriesKey.SIENNA_2011,
            SeriesKey.TACOMA_2011,
            SeriesKey.TUNDRA_2011,
            SeriesKey.YARIS_2010};


    public static String getSeriesName(int seriesCategoryId) {
        switch (seriesCategoryId) {
            case VENZA_ID:
                return VENZA;
            case CAMRY_ID:
                return CAMRY;
            case SIENNA_ID:
                return SIENNA;
            default:
                throw new IllegalArgumentException();
        }
    }

    public Path getSeriesPngRoot(Path pngRootDir) {
        return pngRootDir.append(year + "").append(name);
    }

    public Path getSeriesJpgRoot(Path jpgRootDir) {
        return jpgRootDir.append(year + "").append(name);
    }

    public boolean isa(String seriesName) {
        return name.equalsIgnoreCase(seriesName);
    }

    @Override public int compareTo(SeriesKey that) {
        if (this.getYear() != that.getYear()) {
            Integer thisYear = new Integer(this.getYear());
            Integer thatYear = new Integer(that.getYear());
            return thisYear.compareTo(thatYear);
        } else {
            return this.name.compareTo(that.name);
        }
    }

    public static SeriesKey get(int year, String series) {
        return SeriesKey.getByYearAndSeries(year, series);
    }

    public static SeriesKey getByYearAndSeries(int year, String name) {
        for (SeriesKey sk : getAll()) {
            if (sk.year == year && sk.name.equals(name)) return sk;
        }
        return null;
    }

    public static final SeriesKey DUMMY = new SeriesKey(2010, "dummy");

    public String getKey() {
        return name + "-" + year;
    }

    public String getId(String version) {
        if (Strings.isEmpty(version)) return getKey();
        else return getKey() + "-" + version;
    }


}
