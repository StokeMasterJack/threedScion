package com.tms.threed.threedFramework.repo.server;

import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.collect.MapMaker;
import com.tms.threed.threedFramework.featureModel.server.FeatureModelBuilderXml;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.imageModel.server.ImVNodeHeaderFilter;
import com.tms.threed.threedFramework.imageModel.server.ImageModelBuilder;
import com.tms.threed.threedFramework.imageModel.shared.ImSeries;
import com.tms.threed.threedFramework.repo.server.rt.RtRepo;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.shared.RevisionParameter;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.threedModel.shared.SeriesId;
import com.tms.threed.threedFramework.threedModel.shared.*;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;
import com.tms.threed.threedFramework.threedModel.shared.Slice;
import com.tms.threed.threedFramework.util.lang.shared.Path;
import com.tms.threed.threedFramework.util.vnode.server.FileSystemVNodeBuilder;
import com.tms.threed.threedFramework.util.vnode.server.VNode;
import com.tms.threed.threedFramework.util.vnode.server.VNodeBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevCommit;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

public class SeriesRepo {

    private final ConcurrentMap<RootTreeId, ThreedModel> threedModelCache;

    private final File repoBaseDir;
    private final SeriesKey seriesKey;

    private final File seriesDir;

    private final SrcRepo srcRepo;
    private final SrcWork srcWork;
    private final RtRepo rtRepo;

    SeriesRepo(final File repoBaseDir, final SeriesKey seriesKey) {
        Preconditions.checkNotNull(repoBaseDir);
        Preconditions.checkNotNull(seriesKey);

        this.repoBaseDir = repoBaseDir;
        this.seriesKey = seriesKey;
        this.seriesDir = initSeriesDir();

        this.srcWork = new SrcWork(getSrcWorkDir(), seriesKey);
        this.srcRepo = new SrcRepo(getSrcRepoDir(), seriesKey);
        this.rtRepo = new RtRepo(getGenRepoDir(), seriesKey);

        threedModelCache = new MapMaker()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .makeComputingMap(
                        new Function<RootTreeId, ThreedModel>() {
                            public ThreedModel apply(RootTreeId rootTreeId) {
                                return createThreedModel(rootTreeId);
                            }
                        });

    }

    protected static BufferedImage readImage(InputStream is) {
        try {


            BufferedImage bi = ImageIO.read(is);

            return bi;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public File getSrcWorkDir() {
        return seriesDir;
    }

    public File getSrcRepoDir() {
        return new File(seriesDir, ".git");
    }

    public File getGenRepoDir() {
        return new File(getSrcRepoDir(), ".gen");
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    private File initSeriesDir() {

        File seriesNameDir = new File(repoBaseDir, seriesKey.getName());
        File seriesDir = new File(seriesNameDir, seriesKey.getYear() + "");

        if (!seriesDir.exists()) throw new RepoException("Can't find seriesDir [" + seriesDir + "]");
        if (!seriesDir.isDirectory()) throw new RepoException("SeriesDir not a directory [" + seriesDir + "]");
        if (!seriesDir.canRead()) throw new RepoException("SeriesDir not readable [" + seriesDir + "]");
        return seriesDir;
    }


    public SrcRepo getSrcRepo() {
        srcRepo.initGitDirIfNecessary();
        return srcRepo;
    }


    public SrcWork getSrcWork() {
        return srcWork;
    }

    public RtRepo getRtRepo() {
        return rtRepo;
    }


    public void close() {
        srcRepo.close();
    }

    public ThreedModel getThreedModelHead() {
        RootTreeId rootTreeId = srcRepo.resolveHeadRootTreeId();
        return getThreedModel(rootTreeId);
    }

//    public ThreedModel getThreedModel(RevisionParameter revisionParameter) {
//        RevCommit revCommit = srcRepo.getRevCommitFromRevisionParameter(revisionParameter);
//        ObjectId oid = revCommit.getId();
//        String fullCommitSha = oid.getName();
//        CommitId commitId = new CommitId(fullCommitSha);
//        return getThreedModel(commitId);
//    }


    public ThreedModel getThreedModel(RootTreeId rootTreeId) {
        return threedModelCache.get(rootTreeId);
    }

//    public void printThreedModelCacheKeys() {
//        for (CommitId commitId : threedModelCache.keySet()) {
//            ThreedModel threedModel = threedModelCache.get(commitId);
//            System.out.println("\t" + commitId + " - " + threedModel.getSeriesKey());
//        }
//    }


    public byte[] readModelXmlBytes(@Nonnull RootTreeId rootTreeId) {
        RevisionParameter revisionParameter = new RevisionParameter(rootTreeId, new Path("model.xml"));
        log.info("Resolving revisionParameter for model.xml [" + revisionParameter.stringValue() + "]...");
        ObjectId objectId = srcRepo.resolve(revisionParameter);
        log.info("Resolved revisionParameter [" + revisionParameter.stringValue() + "] to [" + objectId.getName() + "]");
        log.info("Loading bytes from repo from objectId [" + objectId.getName() + "]...");
        byte[] repoObjectAsBytes = srcRepo.getRepoObjectAsBytes(objectId);
        log.info("Object [" + objectId.getName() + "] loaded from repo");
        return repoObjectAsBytes;
    }

    public Document readModelXmlToDom(@Nonnull RootTreeId rootTreeId) {
        byte[] bytes = readModelXmlBytes(rootTreeId);
        SAXReader r = new SAXReader();
        try {
            return r.read(new ByteArrayInputStream(bytes));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public Document readModelXmlToDomFromWork() {
        SAXReader r = new SAXReader();
        try {
            File modelXmlFile = srcWork.getModelXmlFile();
            return r.read(modelXmlFile);
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public ModelXml readModelXmlFromWork() {
        Document document = readModelXmlToDomFromWork();
        return new ModelXml(seriesKey, document);
    }

    public ModelXml readModelXml(@Nonnull RootTreeId rootTreeId) {
        Document document = readModelXmlToDom(rootTreeId);
        return new ModelXml(seriesKey, document);
    }


    private FeatureModel createFeatureModel(ModelXml modelXml) {
        Element featureModel = modelXml.getFeatureModelElement();
        String seriesDisplayName = modelXml.getSeriesDisplayName();
        int seriesYear = modelXml.getYear();
        return FeatureModelBuilderXml.create(seriesKey, seriesDisplayName, seriesYear, featureModel);
    }

    public FeatureModel createFeatureModelFromWork() {
        ModelXml modelXml = readModelXmlFromWork();
        Element featureModel = modelXml.getFeatureModelElement();
        String seriesDisplayName = modelXml.getSeriesDisplayName();
        int seriesYear = modelXml.getYear();
        return FeatureModelBuilderXml.create(seriesKey, seriesDisplayName, seriesYear, featureModel);
    }

    private ImSeries createImageModel(RootTreeId rootTreeId, FeatureModel fm) {
        ObjectId gitObjectId = srcRepo.toGitObjectId(rootTreeId);
        RevCommit revCommit = srcRepo.getRevCommit(gitObjectId);
        RepoVNodeBuilder b = new RepoVNodeBuilder(this, revCommit, rtRepo);
        b.setVNodeHeaderFilter(new ImVNodeHeaderFilter(fm));
        VNode seriesVDir = b.buildVNode();


        ImageModelBuilder imNodeBuilder = new ImageModelBuilder(fm, seriesVDir, rtRepo);
        return imNodeBuilder.buildImageModel();
    }

    private ImSeries createImageModelFromWork(FeatureModel fm) {
        VNodeBuilder vNodeBuilder = new FileSystemVNodeBuilder(srcWork.getSrcWorkDir(), rtRepo);
        vNodeBuilder.setVNodeHeaderFilter(new ImVNodeHeaderFilter(fm));
        VNode vNode = vNodeBuilder.buildVNode();

        ImageModelBuilder imNodeBuilder = new ImageModelBuilder(fm, vNode, rtRepo);
        return imNodeBuilder.buildImageModel();
    }

    public ThreedModel createThreedModelFromWork() {
        log.info("\tBuilding server-side ThreedModel for [" + seriesKey + " - from srcWork...");
        FeatureModel fm = createFeatureModelFromWork();
        ImSeries im = createImageModelFromWork(fm);
        ThreedModel threedModel = new ThreedModel(fm, im);

        log.info("\tServer-side ThreedModel for [" + seriesKey + "] complete");
        return threedModel;
    }

    public ThreedModel createThreedModel(@Nonnull RootTreeId rootTreeId) {
        Preconditions.checkNotNull(rootTreeId);

        log.info("\tBuilding server-side ThreedModel for [" + seriesKey + " - " + rootTreeId.getName() + "] ...");
        ModelXml modelXml = readModelXml(rootTreeId);

        FeatureModel fm = createFeatureModel(modelXml);
        ImSeries im = createImageModel(rootTreeId, fm);
        ThreedModel threedModel = new ThreedModel(fm, im);

        log.info("\tServer-side ThreedModel for [" + seriesKey + "] complete");
        return threedModel;
    }

//     private ThreedModel createThreedModel(@Nonnull String fullCommitSha) {
//        Preconditions.checkNotNull(fullCommitSha);
//        Preconditions.checkArgument(fullCommitSha.length() == 40, "commitSha must be 40 characters long");
//
//        SrcRepo.validateFullSha(fullCommitSha);
//
//        srcRepo.getRevCommitFromRevisionParameter()
//
//        verifyThatThisIsCommitShaAndNotRef(fullCommitSha);
//        FeatureModel fm = createFeatureModel(fullCommitSha);
//        ImSeries im = createImageModel(fullCommitSha, fm);
//        return new ThreedModel(fm, im);
//    }


    public ObjectLoader getPngByShortSha(String pngShortSha) {
        ObjectId objectId = srcRepo.resolve(new RevisionParameter(pngShortSha));
        return srcRepo.getRepoObject(objectId);
    }


    public void createGenVersionDir(SeriesId seriesId, JpgWidth jpgWidth) {
        ThreedModel threedModel = this.getThreedModel(seriesId.getRootTreeId());
        List<Slice> slices = threedModel.getSlices();
        rtRepo.createJpgGenCacheDir(seriesId.getRootTreeId(), slices, jpgWidth);
    }

    public boolean isEmptyPng(String fullPathName, ObjectId objectId) {
        return rtRepo.isEmptyPng(fullPathName, objectId, srcRepo.getInputSupplier(objectId));
    }

    private static Log log = LogFactory.getLog(SeriesRepo.class);
}
