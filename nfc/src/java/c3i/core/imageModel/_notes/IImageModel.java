package c3i.core.imageModel._notes;

/**
 * Zero deps
 */
public interface IImageModel {

    void init(String imageModelJson);

    IImageStack getImageStack(
            String viewId,
            int angle,
            String[] picks,
            String profileId
    );

}
