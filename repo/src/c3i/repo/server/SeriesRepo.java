package c3i.repo.server;

import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.server.XmlToFmJvm;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.threedModel.shared.ImFeatureModel;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.imageModel.shared.ImageModel;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.SimpleFeatureModel;
import c3i.repo.server.rt.RtRepo;
import c3i.repo.server.vnode.FileSystemVNodeBuilder;
import c3i.repo.server.vnode.ImVNodeHeaderFilter;
import c3i.repo.server.vnode.VNode;
import c3i.repo.server.vnode.VNodeBuilder;
import c3i.repo.shared.RevisionParameter;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.io.InputSupplier;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.revwalk.RevCommit;
import smartsoft.util.shared.Path;

import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

public class SeriesRepo {

    private final LoadingCache<RootTreeId, ThreedModel> threedModelCache;

    private final File vtcBaseDir;
    private final File repoBaseDir;
    private final SeriesKey seriesKey;

    private final File seriesDir;

    private final SrcRepo srcRepo;
    private final SrcWork srcWork;
    private final RtRepo rtRepo;

    SeriesRepo(Repos repos, final File repoBaseDir, final SeriesKey seriesKey) {
        Preconditions.checkNotNull(repoBaseDir);
        Preconditions.checkNotNull(seriesKey);

        this.vtcBaseDir = repos.getVtcBaseDir();
        this.repoBaseDir = repoBaseDir;
        this.seriesKey = seriesKey;
        this.seriesDir = initSeriesDir();

        this.srcWork = new SrcWork(getSrcWorkDir(), seriesKey);
        this.srcRepo = new SrcRepo(vtcBaseDir, getSrcRepoDir(), getSrcWorkDir(), seriesKey);
        this.rtRepo = new RtRepo(getGenRepoDir(), seriesKey);


        threedModelCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<RootTreeId, ThreedModel>() {
                            @Override
                            public ThreedModel load(RootTreeId rootTreeId) throws Exception {
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

    public File getVtcBaseDir() {
        return vtcBaseDir;
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

        File brandDir = repoBaseDir;
        File seriesNameDir = new File(brandDir, seriesKey.getName());
        File seriesDir = new File(seriesNameDir, seriesKey.getYear() + "");

        if (!seriesDir.exists()) throw new RepoException("Can't find seriesDir [" + seriesDir + "]");
        if (!seriesDir.isDirectory()) throw new RepoException("SeriesDir not a directory [" + seriesDir + "]");
        if (!seriesDir.canRead()) throw new RepoException("SeriesDir not readable [" + seriesDir + "]");
        return seriesDir;
    }


    public SrcRepo getSrcRepo() {
        srcRepo.createGitRepoIfNeeded();
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

    public RootTreeId getHead() {
        RootTreeId rootTreeId = srcRepo.resolveHeadRootTreeId();
        return rootTreeId;
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
        try {
            return threedModelCache.get(rootTreeId);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
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
        return XmlToFmJvm.create(seriesKey, seriesDisplayName, seriesYear, featureModel);
    }

    public FeatureModel createFeatureModelFromWork() {
        ModelXml modelXml = readModelXmlFromWork();
        Element featureModel = modelXml.getFeatureModelElement();
        String seriesDisplayName = modelXml.getSeriesDisplayName();
        int seriesYear = modelXml.getYear();
        return XmlToFmJvm.create(seriesKey, seriesDisplayName, seriesYear, featureModel);
    }

    private ImageModel createImageModel(RootTreeId rootTreeId, SimpleFeatureModel fm) {
        ObjectId gitObjectId = srcRepo.toGitObjectId(rootTreeId);
        RevCommit revCommit = srcRepo.getRevCommit(gitObjectId);
        RepoVNodeBuilder b = new RepoVNodeBuilder(this, revCommit, rtRepo);
        b.setVNodeHeaderFilter(new ImVNodeHeaderFilter(fm));
        VNode seriesVDir = b.buildVNode();
        Preconditions.checkNotNull(seriesVDir);
        ImageModelBuilder imNodeBuilder = new ImageModelBuilder(fm, seriesVDir);
        return imNodeBuilder.buildImageModel();
    }

    private ImageModel createImageModelFromWork(SimpleFeatureModel fm) {
        VNodeBuilder vNodeBuilder = new FileSystemVNodeBuilder(srcWork.getSrcWorkDir(), rtRepo);
        vNodeBuilder.setVNodeHeaderFilter(new ImVNodeHeaderFilter(fm));
        VNode vNode = vNodeBuilder.buildVNode();

        ImageModelBuilder imNodeBuilder = new ImageModelBuilder(fm, vNode);
        return imNodeBuilder.buildImageModel();
    }

    public ThreedModel createThreedModelFromWork() {
        log.info("\tBuilding server-side ThreedModel for [" + seriesKey + " - from srcWork...");
        FeatureModel fm = createFeatureModelFromWork();
        ImFeatureModel imFm = new ImFeatureModel(fm);
        ImageModel im = createImageModelFromWork(imFm);
        ThreedModel threedModel = new ThreedModel(fm, im);

        log.info("\tServer-side ThreedModel for [" + seriesKey + "] complete");
        return threedModel;
    }

    public ThreedModel createThreedModel(@Nonnull RootTreeId rootTreeId) {
        Preconditions.checkNotNull(rootTreeId);

        long t1 = System.currentTimeMillis();

        log.info("\tBuilding ThreedModel for [" + seriesKey + " - " + rootTreeId.getName() + "] ...");
        ModelXml modelXml = readModelXml(rootTreeId);

        log.info("\t\t Building FeatureModel for [" + seriesKey + " - " + rootTreeId.getName() + "] ...");
        FeatureModel fm = createFeatureModel(modelXml);

        log.info("\t\t Building ImageModel for [" + seriesKey + " - " + rootTreeId.getName() + "] ...");

        ImFeatureModel imFeatureModel = new ImFeatureModel(fm);
        ImageModel im = createImageModel(rootTreeId, imFeatureModel);
        ThreedModel threedModel = new ThreedModel(fm, im);

        long t2 = System.currentTimeMillis();
        long delta = t2 - t1;

        log.info("\t\t ThreedModel complete for [" + seriesKey + "].  ThreedModel created in [" + delta + "]ms");


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


    public ObjectLoader getSrcPngByShortSha(String pngShortSha) {
        ObjectId objectId = srcRepo.resolve(new RevisionParameter(pngShortSha));
        return srcRepo.getRepoObject(objectId);
    }

    public void createGenVersionDir(SeriesId seriesId, Profile profile) {
        ThreedModel threedModel = this.getThreedModel(seriesId.getRootTreeId());
        rtRepo.createJpgGenCacheDir(seriesId.getRootTreeId(), threedModel, profile);
    }

    public boolean isEmptyPng(String fullPathName, ObjectId objectId) {
        return rtRepo.isEmptyPng(fullPathName, objectId, srcRepo.getInputSupplier(objectId));
    }

    public InputSupplier<? extends InputStream> getObject(ObjectId objectId) {
        return srcRepo.getInputSupplier(objectId);
    }

    private static Logger log = Logger.getLogger("c3i");
}
