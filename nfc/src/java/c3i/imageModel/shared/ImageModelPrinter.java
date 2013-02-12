package c3i.imageModel.shared;


import java.util.List;

public class ImageModelPrinter {

    private ImageModel imageModel;

    public ImageModelPrinter(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    public void processImageModel() {
        List<ImView> views = imageModel.getViews();
        for (ImView view : views) {
            processView(view);
        }
    }

    public void processView(ImView view) {
        System.out.println(view.getName());
        List<ImLayer> layers = view.getLayers();
        for (ImLayer layer : layers) {
            if (layer.isAccessory()) {
                processLayer(layer);
            }
        }
    }

    public void processLayer(ImLayer layer) {
        System.out.println("\t" + layer.getName());
    }


}