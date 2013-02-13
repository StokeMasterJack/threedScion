package c3i.core.threedModel.client;

public class Tracker {

    public long totalMillis = 0;
    public long totalInvokes = 0;

    public String title;

    public Tracker(String s) {
        title = s;
    }

    public void addDelta(long delta) {
        totalInvokes++;
        totalMillis += delta;
    }

    public long getAverageDelta() {
        return totalMillis / totalInvokes;
    }

    public void printReport() {
        System.out.println(title + ":");
        System.out.println("totalMillis = [" + totalMillis + "]");
        System.out.println("totalInvokes = [" + totalInvokes + "]");
        System.out.println("averageDelta = [" + getAverageDelta() + "]");
        System.out.println();
    }

    public String getHtmlReport() {
        System.out.println("<h2>" + title + "</h2>");
        String s = "<table border='1'>";
        s += "<tr><td>totalMillis</td><td>" + totalMillis + "</td></tr>";
        s += "<tr><td>totalInvokes</td><td>" + totalInvokes + "</td></tr>";
        s += "<tr><td>averageDelta</td><td>" + getAverageDelta() + "</td></tr></table>";
        return s;
    }

}
