package c3i.admin.server;

import c3i.core.common.shared.SeriesId;
import c3i.core.featureModel.shared.Assignments;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.ProductHandler;
import c3i.core.featureModel.shared.search.TreeSearch;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.imageModel.shared.ImView;
import c3i.imageModel.shared.RawBaseImage;
import c3i.repo.server.Repos;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.common.io.Files;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import static c3i.core.threedModel.shared.ImFeatureModel.toSimplePicks;

public class JpgSet implements Serializable {

    private static final long serialVersionUID = 8042356853513828480L;

    private JpgSetKey key;
    private ImmutableSet<RawBaseImage> jpgSpecs;

    public JpgSet(ImmutableSet<RawBaseImage> jpgSpecs, JpgSetKey key) {
        this.jpgSpecs = jpgSpecs;
        this.key = key;
    }

    private JpgSet() {
    }

    public ImmutableSet<RawBaseImage> getJpgSpecs() {
        return jpgSpecs;
    }

    public int size() {
        return jpgSpecs.size();
    }

    public static JpgSet readJpgSetFile(File cacheDir, JpgSetKey key) {
        File specFileName = key.getFileName(cacheDir);
        BufferedReader is = null;
        HashSet<RawBaseImage> hashSet = new HashSet<RawBaseImage>();
        try {
            is = new BufferedReader(new FileReader(specFileName));
            String fingerprint = is.readLine();
            RawBaseImage rawBaseImage = new RawBaseImage(fingerprint);
            hashSet.add(rawBaseImage);
            return new JpgSet(ImmutableSet.copyOf(hashSet), key);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(is);
        }
    }

    public void writeToFile(File cacheDir) {
        File specFileName = key.getFileName(cacheDir);
        PrintWriter out = null;
        try {
            Files.createParentDirs(specFileName);
            out = new PrintWriter(new FileWriter(specFileName));
            for (RawBaseImage jpgSpec : jpgSpecs) {
                out.println(jpgSpec.getFingerprint());
            }
            out.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(out);
        }
    }


    public static JpgSet createJpgSet(Repos repos, JpgSet.JpgSetKey key) {
        ThreedModel threedModel = repos.getThreedModel(key.getSeriesId());
        return createJpgSet(threedModel, key);
    }

    public static JpgSet createJpgSet(ThreedModel threedModel, final JpgSetKey key) {

        FeatureModel fm = threedModel.getFeatureModel();
        final ImView view = threedModel.getView(key.getView());
        Set<Object> pngVars = view.getPngVars(key.getAngle());

        Set<Var> pngVarsAsVar = ThreedModel.objectSetToVarSet(pngVars);

        final CspForTreeSearch csp = fm.createCspForTreeSearch(pngVarsAsVar);
        csp.propagateSimplify();
        final TreeSearch treeSearch = new TreeSearch();

        final HashSet<RawBaseImage> set = new HashSet<RawBaseImage>();
        treeSearch.setProductHandler(new ProductHandler() {

            @Override
            public void onProduct(Assignments product) {
                RawBaseImage rawBaseImage = view.getPngSegments(toSimplePicks(product), key.getAngle());
                set.add(rawBaseImage);
            }
        });

        treeSearch.start(csp);
        return new JpgSet(ImmutableSet.copyOf(set), key);
    }


    @Override
    public String toString() {
        return super.toString();
    }


    public static class JpgSetKey {

        private static final String JPG_SET = "JpgSet";

        private final SeriesId seriesId;
        private final String view;
        private final int angle;

        public JpgSetKey(SeriesId seriesId, String view, int angle) {
            this.seriesId = seriesId;
            this.view = view;
            this.angle = angle;
        }

        public SeriesId getSeriesId() {
            return seriesId;
        }

        public String getView() {
            return view;
        }

        public int getAngle() {
            return angle;
        }

        public String getKey() {
            return JPG_SET + "-" + seriesId.serialize() + "-" + view + "-" + angle;
        }

        public File getFileName(File cacheDir) {
            return new File(cacheDir, getKey() + ".txt");
        }
    }
}
