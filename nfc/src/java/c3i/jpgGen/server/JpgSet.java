package c3i.jpgGen.server;

import c3i.core.common.shared.SeriesId;
import c3i.core.featureModel.shared.AssignmentsForTreeSearch;
import c3i.core.featureModel.shared.CspForTreeSearch;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.core.featureModel.shared.search.ProductHandler;
import c3i.core.featureModel.shared.search.TreeSearch;
import c3i.core.imageModel.shared.ImView;
import c3i.core.imageModel.shared.PngSegments;
import c3i.core.threedModel.shared.ThreedModel;
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

public class JpgSet implements Serializable {

    private static final long serialVersionUID = 8042356853513828480L;

    private JpgSetKey key;
    private ImmutableSet<PngSegments> jpgSpecs;

    public JpgSet(ImmutableSet<PngSegments> jpgSpecs, JpgSetKey key) {
        this.jpgSpecs = jpgSpecs;
        this.key = key;
    }

    private JpgSet() {
    }

    public ImmutableSet<PngSegments> getJpgSpecs() {
        return jpgSpecs;
    }

    public int size() {
        return jpgSpecs.size();
    }

    public static JpgSet readJpgSetFile(File cacheDir, JpgSetKey key) {
        File specFileName = key.getFileName(cacheDir);
        BufferedReader is = null;
        HashSet<PngSegments> hashSet = new HashSet<PngSegments>();
        try {
            is = new BufferedReader(new FileReader(specFileName));
            String fingerprint = is.readLine();
            PngSegments pngSegments = new PngSegments(fingerprint);
            hashSet.add(pngSegments);
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
            for (PngSegments jpgSpec : jpgSpecs) {
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
        Set<Var> pngVars = view.getPngVars(key.getAngle());
        final CspForTreeSearch csp = fm.createCspForTreeSearch(pngVars);
        csp.propagateSimplify();
        final TreeSearch treeSearch = new TreeSearch();

        final HashSet<PngSegments> set = new HashSet<PngSegments>();
        treeSearch.setProductHandler(new ProductHandler() {
            @Override
            public void onProduct(AssignmentsForTreeSearch product) {
                //                pukeIfCanceled();
                FixedPicks fixedPicks = new FixedPicks(product);
                PngSegments pngSegments = view.getPngSegments(fixedPicks, key.getAngle());
                set.add(pngSegments);
            }
        });

        treeSearch.start(csp);
        return new JpgSet(ImmutableSet.copyOf(set),key);
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
