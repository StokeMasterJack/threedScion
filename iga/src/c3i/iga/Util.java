package c3i.iga;

import c3i.imageModel.shared.Slice;
import c3i.threedModel.client.ThreedModel;

public class Util {

    public static JpgSetTask createJpgSetTask(ThreedModel threedModel, Slice slice) {
        return new JpgSetTask(threedModel, slice);
    }

    public static JpgSetsTask createJpgSetsTask(ThreedModel threedModel) {
        return new JpgSetsTask(threedModel);
    }

    public static JpgSet createJpgSet(ThreedModel threedModel, Slice slice) {
        JpgSetTask task = new JpgSetTask(threedModel, slice);
        task.start();
        return task.getJpgSet();
    }

    public static JpgSets createJpgSets(ThreedModel threedModel) {
        JpgSetsTask task = new JpgSetsTask(threedModel);
        task.start();
        return task.getJpgSets();
    }


}
