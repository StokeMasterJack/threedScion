package c3i.iga;

import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.Slice;
import c3i.imageModel.shared.Slice2;
import c3i.threedModel.client.ThreedModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;

public class JpgSetsTask {

    public static enum State {NOT_STARTED, IN_PROCESS, COMPLETE}

    private final ThreedModel threedModel;

    private final HashMap<Slice, JpgSetTask> jpgSetTasks = new HashMap<Slice, JpgSetTask>();

    private State state = State.NOT_STARTED;

    public JpgSetsTask(ThreedModel threedModel) {
        this.threedModel = threedModel;
    }

    public void start() {
        checkState(state == State.NOT_STARTED);

        state = State.IN_PROCESS;
        execute();
        state = State.COMPLETE;

    }

    private void execute() {

        ImageModel imageModel = threedModel.getImageModel();

        List<ImView> views = imageModel.getViews();

        for (ImView view : views) {
            System.out.println("view = " + view);
            for (int angle = 1; angle <= view.getAngleCount(); angle++) {
                System.out.println("\t angle = " + angle);
                Slice2 slice = new Slice2(view, angle);

                JpgSetTask jpgSetTask = new JpgSetTask(threedModel, slice);
                jpgSetTasks.put(slice.getSlice(), jpgSetTask);

                jpgSetTask.start();

            }
        }

    }

    public State getState() {
        return state;
    }


    public Map<Slice, JpgSetTask> getJpgSetTasks() {
        return jpgSetTasks;
    }

    public JpgSets getJpgSets() {
        Map<Slice, JpgSet> m = new HashMap<Slice, JpgSet>();
        for (Slice slice : jpgSetTasks.keySet()) {
            JpgSetTask task = jpgSetTasks.get(slice);
            m.put(slice, task.getJpgSet());
        }
        return new JpgSets(m);
    }

    public int getJpgCount() {
        int c = 0;
        for (JpgSetTask jpgSetTask : jpgSetTasks.values()) {
            c += jpgSetTask.getJpgCount();
        }
        return c;
    }

}
