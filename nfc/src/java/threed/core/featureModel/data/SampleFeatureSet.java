package threed.core.featureModel.data;

import java.util.HashSet;

public class SampleFeatureSet extends HashSet<String> {

    public SampleFeatureSet() {
//        add("2WD");
        add("2820");
//        add("6-Speed Auto - 4-Cyl ");
//        add("2.7L 4-Cyl");
        add("202");
        add("FA01");
        add("3T");
        add("LP");
        add("DIO1");
        add("DIO2");
    }

    public SampleFeatureSet(String featuresCodesCommaDelimited) {
        String[] a = featuresCodesCommaDelimited.split(",");
        for (String featureCode : a) {
            add(featureCode.trim());
        }
    }

    

}
