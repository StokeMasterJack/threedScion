package c3i.imageModel.shared;


import java.util.Set;

public interface ViewSlice {

    Slice getSlice();

    Set<Object> getPngVars();

    int getAngle();

//    RawImageStack getRawImageStack(SimplePicks product);
}
