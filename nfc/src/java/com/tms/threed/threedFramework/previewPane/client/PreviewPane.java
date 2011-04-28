package com.tms.threed.threedFramework.previewPane.client;

import com.google.gwt.user.client.ui.Composite;

abstract public class PreviewPane extends Composite {

    abstract public void updateImage(
            String flash_key,
            String model,
            String option,
            String excolor,
            String incolor,
            String accessory,
            String msrp,
            String seriesname,
            String helpimgid,
            String helpimgurl,
            String flashdescription);


}
