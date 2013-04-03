package c3i.imageModel.shared;


import java.util.Set;

public class ViewSlice2 implements ViewSlice {

    private final ImView view;
    private final int angle;

    private final Slice slice;

    public ViewSlice2(ImView view, int angle) {
        this.view = view;
        this.angle = angle;
        slice = new Slice(view.getName(), angle);
    }

    @Override
    public Slice getSlice() {
        return slice;
    }

    @Override
    public Set<String> getPngVars() {
        return view.getPngVars(angle);
    }

    @Override
    public int getAngle() {
        return angle;
    }

//    @Override
//    public RawImageStack getRawImageStack(SimplePicks product) {
//        return view.getRawImageStack(product, angle);
//    }
}
