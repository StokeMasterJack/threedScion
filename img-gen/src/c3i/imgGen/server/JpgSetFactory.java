package c3i.imgGen.server;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.imageModel.shared.RawBaseImage;
import c3i.imgGen.external.ImgGenContext;
import c3i.imgGen.external.ImgGenContextFactory;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
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

public abstract class JpgSetFactory {

    protected final BrandRepos brandRepos;
    protected final ImgGenContextFactory imgGenContextFactory;

    protected JpgSetFactory(BrandRepos brandRepos, ImgGenContextFactory imgGenContextFactory) {
        this.brandRepos = brandRepos;
        this.imgGenContextFactory = imgGenContextFactory;
    }

    public Repos getRepos(JpgSetKey jpgSetKey) {
        BrandKey brandKey = jpgSetKey.getSeriesId().getBrandKey();
        return brandRepos.getRepos(brandKey);
    }

    public File getCacheDir(JpgSetKey jpgSetKey) {
        return getRepos(jpgSetKey).getCacheDir();
    }

    public SeriesId getSeriesId(JpgSetKey jpgSetKey) {
        return jpgSetKey.getSeriesId();
    }

    public ImgGenContext getImgGenContext(JpgSetKey jpgSetKey) {
        return imgGenContextFactory.getImgGenContext(getSeriesId(jpgSetKey));
    }

    public JpgSet getOrCreateJpgSet(JpgSetKey key) {
        File file = computeFileName(key);
        JpgSet jpgSet;
        if (file.exists()) {
            jpgSet = getFromFile(file);
        } else {
            jpgSet = createJpgSet(key);
            writeToFile(jpgSet, file);
        }
        return jpgSet;
    }

    protected abstract JpgSet createJpgSet(JpgSetKey key);

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

        MyLineProcessor lp = new MyLineProcessor();
        try {
            return Files.readLines(file, Charsets.UTF_8, lp);
        } catch (IOException e) {
            throw new IORuntimeException(e);
        }
    }


    private File computeFileName(JpgSetKey key) {
        File cacheDir = getCacheDir(key);
        return key.getFileName(cacheDir);
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
