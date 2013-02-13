package c3i.imageModel.shared;

public enum BaseImageType {
    JPG, PNG;

    public String getFileExtension() {
        return this == JPG ? "jpg" : "png";
    }

    public String getMimeType() {
        return this == JPG ? "image/jpeg" : "image/png";
    }
}
