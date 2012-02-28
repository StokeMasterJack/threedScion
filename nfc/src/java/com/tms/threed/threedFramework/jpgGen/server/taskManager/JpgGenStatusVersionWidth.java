package com.tms.threed.threedFramework.jpgGen.server.taskManager;

import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedModel.shared.SeriesId;
import com.tms.threed.threedFramework.threedModel.shared.Slice;

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
