package threed.skin.common.client;

import threed.skin.previewPanel.client.chatPanel.ChatInfo;
import threed.core.threedModel.shared.SeriesInfo;
import threed.core.threedModel.shared.SeriesInfoBuilder;
import threed.core.threedModel.shared.SeriesKey;
import smartsoft.util.lang.shared.Path;

public class SampleData {

    private static Path sampleDataHttpRoot = new Path("http://localhost:8080/threed/previewPaneTestImages");
    private static Path exteriorHttpRoot = sampleDataHttpRoot.append("exterior");
    private static Path interiorHttpRoot = sampleDataHttpRoot.append("interior");


    public Path getInteriorUrl(int angle) {
        return interiorHttpRoot.append(angle + ".jpg");
    }

    public Path getExteriorUrl(int angle) {
        return exteriorHttpRoot.append(angle + ".jpg");
    }

    public ChatInfo getChatInfo() {
        Path p1 = new Path("http://localhost:8080/threed/chat-images/chatSeriesIcon.gif");
        Path p2 = new Path("http://toyota.custhelp.com/cgi-bin/toyota.cfg/php/enduser/chat.php");
        return new ChatInfo(p1, p2);
    }

    public SeriesInfo getSeriesInfo() {
        return SeriesInfoBuilder.createSeriesInfo(SeriesKey.VENZA_2010);
    }


}
