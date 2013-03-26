package c3i.imageModel.shared.api;

/**
 * Sync
 *
 * Each ImageModel is its own Fm Bundle
 *
 */
public interface ImageModel {

    /**
     *  Assumes the entire ImageModel is already loaded
     */
    ImageSpec getImageSpec(String viewName, int angle, SimplePicks picks);

}
