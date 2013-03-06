package c3i.imageModel.shared.api;

public interface ImageSpec {

    String getImageModelKey();

    String getView();

    int getAngle();

    PngSpec[] getAllPngs();

    PngSpec[] getBasePngs();

    PngSpec[] getZPngs();


    /*
     System.out.println(imageStack.getAllPngs1());
        System.out.println(imageStack.getBasePngs1());
        System.out.println(imageStack.getZPngs1());

        System.out.println();

        System.out.println(imageStack.getAllPngs2());
        System.out.println(imageStack.getBasePngs2());
        System.out.println(imageStack.getZPngs2());

        System.out.println();

        System.out.println(imageStack.getBasePngs3());

        System.out.println(imageStack.getContextPath());

     */
}
