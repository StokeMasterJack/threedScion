package c3i.iga;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.featureModel.shared.common.SimplePicks;
import c3i.featureModel.shared.search.ProductHandler;
import c3i.imageModel.shared.RawBaseImage;
import c3i.imageModel.shared.Slice;
import c3i.imageModel.shared.Slice2;
import c3i.threedModel.client.ThreedModel;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import smartsoft.util.shared.IORuntimeException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

public class JpgSetTask {

    public static enum State {NOT_STARTED, IN_PROCESS, COMPLETE}

    private final Slice2 slice2;

    private final ThreedModel threedModel;
    private final JpgSetProductHandler productHandler;

    private State state;

    public JpgSetTask(ThreedModel threedModel, String viewName, int angle) {
        this(threedModel, threedModel.getImageModel().getSlice2(viewName, angle));
    }

    public JpgSetTask(ThreedModel threedModel, Slice slice) {
        this(threedModel, slice.getViewName(), slice.getAngle());
    }

    public JpgSetTask(ThreedModel threedModel, Slice2 slice2) {
        this.threedModel = threedModel;
        this.slice2 = slice2;
        productHandler = new JpgSetProductHandler(slice2);
        state = State.NOT_STARTED;
    }

    public void start() {
        Preconditions.checkState(state == State.NOT_STARTED);
        state = State.IN_PROCESS;
        FeatureModel fm = threedModel.getFeatureModel();


//        fm.forEachProduct(new ProductHandler() {
//            @Override
//            public void onProduct(SimplePicks product) {
//                System.out.println("onProduct");
//            }
//        }, slice2.getPngVars());

        Set<Var> varSet = fm.varCodesToVars(slice2.getPngVars());
        fm.forEachProduct(productHandler, varSet);


        state = State.COMPLETE;
    }

    public Slice2 getSlice2() {
        return slice2;
    }

    public State getState() {
        return state;
    }

    public JpgSet getJpgSet() {
        return productHandler.getResult();
    }

    public int getJpgCount() {
        return getJpgSet().size();
    }

    private class SimplePicksAdapter implements c3i.imageModel.shared.SimplePicks {

        private final c3i.featureModel.shared.common.SimplePicks simplePicks;

        private SimplePicksAdapter(SimplePicks simplePicks) {
            this.simplePicks = simplePicks;
        }

        @Override
        public boolean isPicked(String varCode) {
            Var var = threedModel.getFeatureModel().getVar(varCode);
            return this.simplePicks.isPicked(var);
        }

    }

    private class JpgSetProductHandler implements ProductHandler {

        //input
        private final Slice2 slice2;

        //output
        private final HashSet<RawBaseImage> set = new HashSet<RawBaseImage>();
        private JpgSet jpgSet;

        private int dupCount;

        private JpgSetProductHandler(Slice2 slice2) {
            this.slice2 = slice2;
        }


        @Override
        public void onProduct(SimplePicks product) {
            SimplePicksAdapter simplePicks = new SimplePicksAdapter(product);
            RawBaseImage rawBaseImage = slice2.getPngSegments(simplePicks);
            boolean added = set.add(rawBaseImage);
            if (added) {
//                System.out.println(set.size() + ":Added: " + product.toString());
            } else {
                dupCount++;
//                System.out.println(set.size() + ":Dup: " + product.toString());
            }
        }

        public JpgSet getResult() {
            if (jpgSet == null) {
                jpgSet = new JpgSet(set);
            }
            return jpgSet;
        }

        public int getDupCount() {
            return dupCount;
        }
    }

    public int getDupCount() {
        return productHandler.getDupCount();
    }

    protected JpgSet getFromFile(File file) throws IORuntimeException {

        class MyLineProcessor implements LineProcessor<JpgSet> {

            private Set<RawBaseImage> set = new HashSet<RawBaseImage>();

            @Override
            public boolean processLine(String fingerprint) throws IOException {
                RawBaseImage rbi = new RawBaseImage(fingerprint);
                set.add(rbi);
                return true;
            }

            @Override
            public JpgSet getResult() {
                return new JpgSet(set);
            }
        }

        LineProcessor<JpgSet> lp = new MyLineProcessor();
        try {
            JpgSet jpgSet1 = Files.readLines(file, Charsets.UTF_8, lp);
            JpgSet jpgSet = jpgSet1;
            return jpgSet;
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private String serializeToString(JpgSet jpgSet) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        serializeToWriter(jpgSet, out);
        out.flush();
        return stringWriter.toString();
    }

    private void serializeToWriter(JpgSet jpgSet, PrintWriter out) {
        for (RawBaseImage jpgSpec : jpgSet) {
            out.println(jpgSpec.getFingerprint());
        }
        out.flush();
    }

    private void writeStringToFile(String jpgSetAsString, File file) throws IORuntimeException {
        try {
            Files.createParentDirs(file);
            Files.write(jpgSetAsString, file, Charsets.UTF_8);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }

    private void writeToFile(JpgSet jpgSet, File file) {
        String jpgSetAsString = serializeToString(jpgSet);
        writeStringToFile(jpgSetAsString, file);
    }

}
