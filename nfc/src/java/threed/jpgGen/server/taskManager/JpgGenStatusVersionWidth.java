package threed.jpgGen.server.taskManager;

import threed.repo.server.Repos;
import threed.core.threedModel.shared.JpgWidth;
import threed.core.threedModel.shared.SeriesId;
import threed.core.threedModel.shared.Slice;

import java.util.ArrayList;
import java.util.List;

public class JpgGenStatusVersionWidth extends JpgVersionWidthAction {

    JpgSetAction jpgSetAction;

    List<JpgGenStatusVersionWidthSlice> slices = new ArrayList<JpgGenStatusVersionWidthSlice>();


    public JpgGenStatusVersionWidth(Repos repos, SeriesId seriesId, JpgWidth jpgWidth) {
        super(repos, seriesId, jpgWidth);

        for (Slice slice : threedModel.getSlices()) {
            JpgGenStatusVersionWidthSlice st = new JpgGenStatusVersionWidthSlice(repos, seriesId, slice, jpgWidth);
            slices.add(st);
        }

    }

    public List<JpgGenStatusVersionWidthSlice> getSlices() {
        return slices;
    }


    public String getJpgCountMessage() {
        int t = 0;
        boolean partial = false;
        for (JpgGenStatusVersionWidthSlice slice : slices) {
            Integer c = slice.getJpgCount();
            if (c == null) {
                partial = true;
            } else {
                t += c;
            }
        }

        if (partial) {
            return t + "*";
        } else {
            return t + "";
        }


    }


}
