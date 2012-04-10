package threed.jpgGen.server.taskManager;

import com.google.common.io.Closeables;
import com.google.common.io.Files;
import threed.core.threedModel.shared.JpgKey;
import threed.core.featureModel.shared.AssignmentsForTreeSearch;
import threed.core.featureModel.shared.CspForTreeSearch;
import threed.core.featureModel.shared.FeatureModel;
import threed.core.featureModel.shared.boolExpr.Var;
import threed.core.featureModel.shared.search.ProductHandler;
import threed.core.featureModel.shared.search.TreeSearch;
import threed.core.imageModel.shared.slice.ImageSlice;
import threed.core.imageModel.shared.slice.Jpg;
import threed.core.threedModel.shared.*;
import threed.repo.server.Repos;
import threed.core.threedModel.shared.RootTreeId;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.Set;

public class JpgSetAction extends JpgVersionWidthSliceAction {

    private final File jpgJobStartedFile;

    private final File jpgCountFile;
    private final File jpgSetFile;

    private final File jpgJobCompleteFile;

    private boolean canceled;

    public JpgSetAction(Repos repos, SeriesId seriesId, Slice slice, JpgWidth jpgWidth) {
        super(repos, seriesId, slice, jpgWidth);

        RootTreeId rootTreeId = seriesId.getRootTreeId();
        this.jpgJobStartedFile = genRepo.getJpgJobStartedFile(rootTreeId, jpgWidth, slice);
        this.jpgCountFile = genRepo.getJpgCountFile(rootTreeId, jpgWidth, slice);
        this.jpgSetFile = genRepo.getJpgSetFile(rootTreeId, jpgWidth, slice);
        this.jpgJobCompleteFile = genRepo.getJpgJobCompleteFile(rootTreeId, jpgWidth, slice);

    }

    public void writeJpgSetIfNotExists(Set<String> jpgSet) {

        if (!jpgSetFileExists()) {

            Set<String> spec = createJpgSet(threedModel, slice);
            writeJpgSetFile(spec, jpgSetFile);

        }
    }

    public Set<String> readOrCreateJpgSet() {
        pukeIfCanceled();

        if (jpgSetFileExists()) {
            return readJpgSet();
        } else {
            return createJpgSet(threedModel, slice);
        }
    }

    public synchronized void cancelDeep() {
        this.canceled = true;
    }


    public Set<String> createJpgSet(ThreedModel threedModel, Slice slice) {

        FeatureModel fm = threedModel.getFeatureModel();
        final ImageSlice imageSlice = threedModel.getImageSlice(slice);

        Set<Var> careVars = new HashSet<Var>();
        careVars.addAll(imageSlice.getJpgVars());

        final CspForTreeSearch csp = fm.createCspForTreeSearch(careVars);

        csp.propagateSimplify();

        final TreeSearch treeSearch = new TreeSearch();

        final HashSet<String> set = new HashSet<String>();
        treeSearch.setProductHandler(new ProductHandler() {
            @Override
            public void onProduct(AssignmentsForTreeSearch product) {
                pukeIfCanceled();
                Jpg jpg = imageSlice.computeJpg(product);
                String fingerprint = jpg.getFingerprint();
                set.add(fingerprint);
            }
        });

        treeSearch.start(csp);

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

    private static void writeJpgSetFile(Set<String> spec, File specFileName) {
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

    private static HashSet<String> readJpgSetFile(File specFileName) {
        ObjectInputStream is = null;
        try {
            is = new ObjectInputStream(new FileInputStream(specFileName));
            return (HashSet<String>) is.readObject();
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
            boolean exists = genRepo.exists(new JpgKey(seriesKey, jpgWidth, fingerprint));
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

    public Set<String> readJpgSet() {
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
}
