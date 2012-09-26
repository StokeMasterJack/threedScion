package c3i.core.featureModel.server;

import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.repo.server.ModelXml;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.util.Collection;

public class XmlToFmJvmTest extends TestCase {

    public void test1() throws Exception {
        ModelXml modelXml = readModelXmlFromWork();
        FeatureModel fm = createFeatureModel(modelXml);

        System.out.println(fm.getSeriesKey());



    }

    SeriesKey seriesKey = SeriesKey.CAMRY_2011;

    public Document readModelXmlToDomFromWork() {


        SAXReader r = new SAXReader();
        try {
            File modelXmlFile = new File("/Users/dford/repos/csp/src/java/com/tms/featureModel/data/prod/venza-2013.xml");
            return r.read(modelXmlFile);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }


    }

    public void test() throws Exception {
//        XmlToFmJvm.create(SeriesKey.CAMRY_2011, )


        Document document = readModelXmlToDomFromWork();
        SeriesKey work = SeriesKey.CAMRY_2011;
        ModelXml modelXml = new ModelXml(work, document);

        System.out.println("modelXml = " + modelXml);

        Element featureModelElement = modelXml.getFeatureModelElement();

        try {
            String text = featureModelElement.getText();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }


//    public byte[] readModelXmlBytes(@Nonnull RootTreeId rootTreeId) {
//        RevisionParameter revisionParameter = new RevisionParameter(rootTreeId, new Path("model.xml"));
//        log.info("Resolving revisionParameter for model.xml [" + revisionParameter.stringValue() + "]...");
//        ObjectId objectId = srcRepo.resolve(revisionParameter);
//        log.info("Resolved revisionParameter [" + revisionParameter.stringValue() + "] to [" + objectId.getName() + "]");
//        log.info("Loading bytes from repo from objectId [" + objectId.getName() + "]...");
//        byte[] repoObjectAsBytes = srcRepo.getRepoObjectAsBytes(objectId);
//        log.info("Object [" + objectId.getName() + "] loaded from repo");
//        return repoObjectAsBytes;
//    }

    public FeatureModel createFeatureModelFromWork() {
        ModelXml modelXml = readModelXmlFromWork();
        Element featureModel = modelXml.getFeatureModelElement();
        String seriesDisplayName = modelXml.getSeriesDisplayName();
        int seriesYear = modelXml.getYear();
        return XmlToFmJvm.create(seriesKey, seriesDisplayName, seriesYear, featureModel);
    }

    public ModelXml readModelXmlFromWork() {
        Document document = readModelXmlToDomFromWork();
        return new ModelXml(seriesKey, document);
    }

    private FeatureModel createFeatureModel(ModelXml modelXml) {
        Element featureModel = modelXml.getFeatureModelElement();
        String seriesDisplayName = modelXml.getSeriesDisplayName();
        int seriesYear = modelXml.getYear();
        return XmlToFmJvm.create(seriesKey, seriesDisplayName, seriesYear, featureModel);
    }



    private static Log log = LogFactory.getLog(XmlToFmJvmTest.class);

}
