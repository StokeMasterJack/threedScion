package com.tms.threed.threedFramework.jpgGen.server.taskManager;

import com.google.common.io.Closeables;
import com.google.common.io.Files;
import com.tms.threed.threedFramework.featureModel.shared.AssignmentsForTreeSearch;
import com.tms.threed.threedFramework.featureModel.shared.CspForTreeSearch;
import com.tms.threed.threedFramework.featureModel.shared.FeatureModel;
import com.tms.threed.threedFramework.featureModel.shared.boolExpr.Var;
import com.tms.threed.threedFramework.featureModel.shared.search.ProductHandler;
import com.tms.threed.threedFramework.featureModel.shared.search.TreeSearch;
import com.tms.threed.threedFramework.imageModel.shared.slice.ImageSlice;
import com.tms.threed.threedFramework.imageModel.shared.slice.Jpg;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.repo.server.JpgId;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.threedCore.shared.SeriesId;
import com.tms.threed.threedFramework.threedCore.shared.Slice;
import com.tms.threed.threedFramework.threedModel.shared.ThreedModel;

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
            boolean exists = genRepo.exists(new JpgId(seriesKey, jpgWidth, fingerprint));
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
