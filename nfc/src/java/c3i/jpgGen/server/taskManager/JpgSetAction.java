package c3i.jpgGen.server.taskManager;

import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.AssignmentsForTreeSearch;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.ProductHandler;
import c3i.core.featureModel.shared.search.TreeSearch;
import c3i.core.imageModel.shared.BaseImage;
import c3i.core.imageModel.shared.CoreImageStack;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.ImageMode;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.RawImageStack;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.core.threedModel.shared.Slice2;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.server.Repos;
import c3i.repo.server.SeriesRepo;
import c3i.repo.server.SrcRepo;
import c3i.repo.server.rt.RtRepo;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.common.io.Files;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

/**
 * This class computes the list of jpgs for one slice
 */
public class JpgSetAction {

    private final Repos repos;
    private final SeriesId seriesId;
    private final Slice2 slice;
    private final ImView view;
    private final int angle;
    private final Profile profile;

    private final SeriesKey seriesKey;
    private final RootTreeId rootTreeId;
    private final ThreedModel threedModel;

    private final SeriesRepo seriesRepo;
    private final SrcRepo srcRepo;
    private final RtRepo genRepo;

    private final File jpgJobStartedFile;

    private final File jpgCountFile;
    private final File jpgSetFile;

    private final File jpgJobCompleteFile;

    private ImmutableSet<String> set; //lazy

//    private boolean canceled;

    public JpgSetAction(Repos repos, SeriesId seriesId, Slice2 slice, Profile profile) {

        this.repos = repos;
        this.seriesId = seriesId;
        this.slice = slice;
        this.view = slice.getView();
        this.angle = slice.getAngle();
        this.profile = profile;
        this.rootTreeId = seriesId.getRootTreeId();
        this.seriesKey = seriesId.getSeriesKey();


        this.seriesRepo = repos.getSeriesRepo(seriesKey);
        this.srcRepo = seriesRepo.getSrcRepo();
        this.genRepo = seriesRepo.getRtRepo();


        this.threedModel = repos.getThreedModel(seriesId);


        RootTreeId rootTreeId = seriesId.getRootTreeId();
        this.jpgJobStartedFile = genRepo.getJpgJobStartedFile(rootTreeId, profile, view, angle);
        this.jpgCountFile = genRepo.getJpgCountFile(rootTreeId, profile, view, angle);
        this.jpgSetFile = genRepo.getJpgSetFile(rootTreeId, profile, view, angle);
        this.jpgJobCompleteFile = genRepo.getJpgJobCompleteFile(rootTreeId, profile, view, angle);

    }


    public Set<String> getJpgSet() {
        if (this.set == null) {
            if (jpgSetFileExists()) {
                this.set = readJpgSet();
            } else {
                FeatureModel fm = threedModel.getFeatureModel();
                Set<Var> pngVars = view.getPngVars(angle);
                final CspForTreeSearch csp = fm.createCspForTreeSearch(pngVars);
                csp.propagateSimplify();
                final TreeSearch treeSearch = new TreeSearch();

                final HashSet<String> set = new HashSet<String>();
                treeSearch.setProductHandler(new ProductHandler() {
                    @Override
                    public void onProduct(AssignmentsForTreeSearch product) {
                        pukeIfCanceled();
                        FixedPicks fixedPicks = new FixedPicks(product);
                        RawImageStack rawImageStack = view.getRawImageStack(fixedPicks, angle);
                        CoreImageStack coreImageStack = rawImageStack.getCoreImageStack(profile, ImageMode.JPG);
                        String fingerprint = coreImageStack.getBaseImageFingerprint();
                        set.add(fingerprint);
                    }
                });
                long t1 = System.currentTimeMillis();
                treeSearch.start(csp);
                long t2 = System.currentTimeMillis();
                System.err.println("TreeSearch Delta[" + seriesId + "-" + slice + "-" + profile + "]: " + (t2 - t1));
                this.set = ImmutableSet.copyOf(set);
                writeJpgSetFile(this.set, jpgSetFile);
            }
        }
        return set;
    }

    private void pukeIfCanceled() {
        //todo
    }

    public boolean jpgSetFileExists() {
        return jpgSetFile.exists();
    }

    public boolean jpgCountFileExists() {
        return jpgCountFile.exists();
    }

    private static void writeJpgSetFile(ImmutableSet<String> spec, File specFileName) {
        ObjectOutputStream os = null;
        try {
            Files.createParentDirs(specFileName);
            os = new ObjectOutputStream(new FileOutputStream(specFileName));
            os.writeObject(spec);
            os.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(os);
        }
    }

    private static ImmutableSet<String> readJpgSetFile(File specFileName) {
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new FileInputStream(specFileName));
            return (ImmutableSet<String>) is.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(is);
        }
    }

    public Integer countMissingJpgs() {

        if (!jpgSetFileExists()) {
            return null;
        }

        int missingJpgs = 0;
        Set<String> spec = readJpgSet();
        for (String fingerprint : spec) {
            BaseImage parse = BaseImage.parse(profile, slice, fingerprint);
            boolean exists = genRepo.exists(parse);
            if (!exists) {
                missingJpgs++;
            }
        }
        return missingJpgs;

    }

    private static void writeJpgCountFile(int count, File f) {
        try {
            Files.createParentDirs(f);
            Files.write(count + "", f, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int readJpgCountFile(File f) {
        String s;
        try {
            s = Files.toString(f, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return new Integer(s);
    }


    public int readJpgCount() {
        return readJpgCountFile(jpgCountFile);
    }

    private ImmutableSet<String> readJpgSet() {
        return readJpgSetFile(jpgSetFile);
    }

    public String getStarted() {
        if (jpgJobStartedFile.exists()) {
            try {
                return Files.toString(jpgJobStartedFile, Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    public String getComplete() {
        if (jpgJobCompleteFile.exists()) {
            try {
                return Files.toString(jpgJobCompleteFile, Charset.defaultCharset());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }

    private static Log log = LogFactory.getLog(JpgSetAction.class);


}
