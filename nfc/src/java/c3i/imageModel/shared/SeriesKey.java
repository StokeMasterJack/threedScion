package c3i.imageModel.shared;

public class SeriesKey {

    private final String brand;
    private final int year;
    private final String name;

    public SeriesKey(String brand, int year, String name) {
        this.brand = brand;
        this.year = year;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getYear() {
        return year;
    }

    public String getBrand() {
        return brand;
    }
}
