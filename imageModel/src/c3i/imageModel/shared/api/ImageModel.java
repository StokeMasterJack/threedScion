package c3i.imageModel.shared.api;

public interface ImageModel {

    ImageSpec getImageSpec(String viewName, int angle, String[] picks);

}
