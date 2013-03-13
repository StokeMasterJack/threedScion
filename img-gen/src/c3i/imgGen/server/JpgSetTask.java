package c3i.imgGen.server;

import c3i.core.common.shared.ProductHandler;
import c3i.featureModel.shared.CspForTreeSearch;
import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.FmSearchRequest;
import c3i.featureModel.shared.boolExpr.Var;
import c3i.imageModel.shared.RawBaseImage;
import c3i.imageModel.shared.Slice2;
import c3i.imgGen.repoImpl.FmIm;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import smartsoft.util.shared.IORuntimeException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkState;

public class JpgSetTask<ID> {

    public static enum State {NOT_STARTED, IN_PROCESS, COMPLETE}

    private final Slice2 slice2;

    private final FmIm<ID> fmIm;
    private final FmSearchRequest<JpgSet> request;
    private final JpgSetProductHandler productHandler;

    private State state;

    public JpgSetTask(FmIm<ID> fmIm, String viewName, int angle) {
        this(fmIm, fmIm.getImageModel().getSlice2(viewName, angle));
    }

    public JpgSetTask(FmIm<ID> fmIm, c3i.imageModel.shared.Slice slice) {
        this(fmIm, slice.getViewName(), slice.getAngle());
    }

    public JpgSetTask(FmIm<ID> fmIm, Slice2<Var> slice2) {
        this.fmIm = fmIm;
        this.slice2 = slice2;
        request = new FmSearchRequest<JpgSet>();
        request.setOutVars(slice2.getPngVars());
        productHandler = new JpgSetProductHandler(slice2);
        request.setProductHandler(productHandler);
        state = State.NOT_STARTED;
    }

    public void start() {
        checkState(state == State.NOT_STARTED);
        state = State.IN_PROCESS;
        FeatureModel fm = fmIm.getFeatureModel();
        fm.forEach(request);

        state = State.COMPLETE;
    }

    public Slice2 getSlice2() {
        return slice2;
    }

    public State getState() {
        return state;
    }

    public JpgSet getJpgSet() {
        return request.getResult();
    }

    public int getJpgCount() {
        return getJpgSet().size();
    }

    private static class JpgSetProductHandler implements ProductHandler<CspForTreeSearch, JpgSet> {

        //input
        private final Slice2<Var> slice2;

        //output
        private final HashSet<RawBaseImage> set = new HashSet<RawBaseImage>();
        private JpgSet jpgSet;

        private int dupCount;

        private JpgSetProductHandler(Slice2<Var> slice2) {
            this.slice2 = slice2;
        }

        @Override
        public void onProduct(CspForTreeSearch product) {
            RawBaseImage rawBaseImage = slice2.getPngSegments(product);
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
