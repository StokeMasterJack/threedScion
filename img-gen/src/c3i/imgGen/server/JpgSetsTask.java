package c3i.imgGen.server;

import c3i.imageModel.server.JsonToImJvm;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.Slice;
import c3i.imageModel.shared.Slice2;
import c3i.imgGen.external.ImgGenContext;

import java.util.HashMap;
import java.util.List;

import static com.google.common.base.Preconditions.checkState;

public class JpgSetsTask {

    public static enum State {NOT_STARTED, IN_PROCESS, COMPLETE}

    private final ImgGenContext ctx;

    private final HashMap<Slice, JpgSetTask> jpgSetTasks = new HashMap<Slice, JpgSetTask>();

    private State state = State.NOT_STARTED;

    public JpgSetsTask(ImgGenContext ctx) {
        this.ctx = ctx;
    }

    public void start() {
        checkState(state == State.NOT_STARTED);

        state = State.IN_PROCESS;
        execute();
        state = State.COMPLETE;

    }

    private void execute() {

        String imageModelJson = ctx.getImageModelJson();

        ImageModel imageModel = JsonToImJvm.parse(ctx, imageModelJson);

        List<ImView> views = imageModel.getViews();

        for (ImView view : views) {
            for (int angle = 1; angle <= view.getAngleCount(); angle++) {

                Slice2 slice = new Slice2(view, angle);

                JpgSetTask jpgSetTask = new JpgSetTask(ctx, slice);
                jpgSetTasks.put(slice.getSlice(), jpgSetTask);

                jpgSetTask.start();

            }
        }

    }

    public State getState() {
        return state;
    }


    public HashMap<Slice, JpgSetTask> getJpgSetTasks() {
        return jpgSetTasks;
    }

    public int getJpgCount() {
        int c = 0;
        for (JpgSetTask jpgSetTask : jpgSetTasks.values()) {
            c += jpgSetTask.getJpgCount();
        }
        return c;
    }

    public ImgGenContext getCtx() {
        return ctx;
    }


}
