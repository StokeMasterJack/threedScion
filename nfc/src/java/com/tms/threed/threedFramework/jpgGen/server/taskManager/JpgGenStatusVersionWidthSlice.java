package com.tms.threed.threedFramework.jpgGen.server.taskManager;

import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedCore.shared.SeriesId;
import com.tms.threed.threedFramework.threedCore.shared.Slice;

public class JpgGenStatusVersionWidthSlice extends JpgVersionWidthSliceAction {

    JpgSetAction jpgSetAction;


    public JpgGenStatusVersionWidthSlice(Repos repos, SeriesId seriesId, Slice slice, JpgWidth jpgWidth) {
        super(repos, seriesId, slice, jpgWidth);

        jpgSetAction = new JpgSetAction(repos, seriesId, slice, jpgWidth);
    }

    public boolean jpgCountFileExists(){
        return jpgSetAction.jpgCountFileExists();
    }
    public Integer getJpgCount() {

        if (jpgSetAction.jpgCountFileExists()) {
            return jpgSetAction.readJpgCount();
        } else {
            return null;
        }

    }

    public String getJpgCountMessage() {
        Integer jpgCount = getJpgCount();
        if (jpgCount == null) {
            return "Still Counting";
        } else {
            return jpgCount + "";
        }
    }

    public boolean jpgSetFileExists() {
        return jpgSetAction.jpgSetFileExists();
    }

    public Integer getMissingJpgCount() {
        return jpgSetAction.countMissingJpgs();
    }

    public String getMissingJpgCountMessage() {
        Integer missingJpgCount = getMissingJpgCount();
        if (missingJpgCount == null) {
            return "Still computing JpgSet";
        } else {
            return missingJpgCount + "";
        }
    }


    public String getStarted() {
        return jpgSetAction.getStarted();
    }

     public String getComplete() {
        return jpgSetAction.getComplete();
    }
}
