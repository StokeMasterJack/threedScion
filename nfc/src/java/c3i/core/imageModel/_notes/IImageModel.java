package c3i.core.imageModel._notes;

/**
 * Zero deps
 */
public interface IImageModel {

    void init(String imageModelJson);


    String[] getImageStack(
            String viewId,
            int angle,
            String[] picks,
            String profileId ,//size,PngOrJpg
            String imageMode , //Collapse | Expanded
            boolean skipZLayers,
            String repoBase


    );

}
