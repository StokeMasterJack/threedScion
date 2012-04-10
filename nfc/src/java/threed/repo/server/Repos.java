package threed.repo.server;

import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import threed.jpgGen.server.singleJpg.JpgGeneratorPureJava;
import threed.repo.server.rt.RtRepo;
import threed.repo.shared.Series;
import threed.repo.shared.Settings;
import threed.core.featureModel.shared.FeatureModel;
import threed.core.threedModel.shared.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import smartsoft.util.FileUtil;
import smartsoft.util.lang.shared.Strings;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;

public class Repos {

    public static final String VTC_LOCAL_DIR_NAME = ".vtc";

    private static Repos INSTANCE;
    private static File staticRepoBaseDir;

    public static void setRepoBaseDir(File repoBaseDir) {
        Repos.staticRepoBaseDir = repoBaseDir;
    }

    public static Repos get() {
        if (staticRepoBaseDir == null) {
            throw new IllegalStateException("Must call setRepoBaseDir(..) before calling get()");
        }
        if (INSTANCE == null) {
            INSTANCE = new Repos(staticRepoBaseDir);
        }
        return INSTANCE;
    }

    private File repoBaseDir;
    private final LoadingCache<SeriesKey, SeriesRepo> seriesRepoCache;
    private final SettingsHelper settingsHelper;

    public Repos(final File repoBaseDir) {
        Preconditions.checkNotNull(repoBaseDir);
        this.repoBaseDir = repoBaseDir;

        if (!repoBaseDir.exists()) {
            throw new IllegalStateException("repoBaseDir [" + repoBaseDir + "] does not exist");
        }

        seriesRepoCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<SeriesKey, SeriesRepo>() {
                            @Override
                            public SeriesRepo load(SeriesKey seriesKey) throws Exception {
                                return new SeriesRepo(repoBaseDir, seriesKey);
                            }
                        });

        settingsHelper = new SettingsHelper(repoBaseDir);


        FileUtil.createDirNotExists(getVtcBaseDir(BrandKey.TOYOTA));
        FileUtil.createDirNotExists(getVtcBaseDir(BrandKey.LEXUS));
        FileUtil.createDirNotExists(getVtcBaseDir(BrandKey.SCION));


    }

    public void purgeCache() {
        seriesRepoCache.invalidateAll();
    }

    public SeriesRepo getSeriesRepo(BrandKey brandKey,String seriesName, int seriesYear) {
        return getSeriesRepo(new SeriesKey(brandKey,seriesYear, seriesName));
    }

    public SeriesRepo getSeriesRepo(SeriesKey seriesKey) {
        Preconditions.checkNotNull(seriesKey);

        try {
            return seriesRepoCache.get(seriesKey);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

    }

    public SettingsHelper getSettingsHelper() {
        return settingsHelper;
    }

    public Settings getSettings() {
        return settingsHelper.read();
    }

    public ThreedModel getThreedModel(SeriesKey seriesKey, RootTreeId rootTreeId) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        return seriesRepo.getThreedModel(rootTreeId);
    }

    public ThreedModel getThreedModel(BrandKey brandKey,String seriesName, int seriesYear, RootTreeId rootTreeId) {
        SeriesKey seriesKey = new SeriesKey(brandKey,seriesYear, seriesName);
        return getThreedModel(seriesKey, rootTreeId);
    }

    public ThreedModel getThreedModelForHead(SeriesKey seriesKey) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        return seriesRepo.getThreedModelHead();
    }

    public ThreedModel getThreedModel(BrandKey brandKey,String seriesName, int seriesYear) {
        SeriesKey seriesKey = new SeriesKey(brandKey,seriesYear, seriesName);
        return getThreedModelForHead(seriesKey);
    }

    public FeatureModel getFeatureModel(SeriesKey seriesKey) {
        return getThreedModelForHead(seriesKey).getFeatureModel();
    }

    public File getRepoBaseDir() {
        return repoBaseDir;
    }

    public VtcMap getVtcMap(BrandKey brandKey) {
        ImmutableMap.Builder<SeriesKey, RootTreeId> builder = ImmutableMap.builder();
        Set<SeriesKey> seriesKeys = getSeriesKeys(brandKey);
        for (SeriesKey seriesKey : seriesKeys) {
            try {
                RootTreeId vtcRootTreeId = getVtcRootTreeId(seriesKey);
                builder.put(seriesKey, vtcRootTreeId);
            } catch (UnableToResolveRevisionParameterException e) {
                log.warn("Problem getting vtc RootTreeId for seriesKey[" + seriesKey + "]  - unable to resolve HEAD");
            } catch (Exception e) {
                log.warn("Problem getting vtc RootTreeId for seriesKey[" + seriesKey + "]", e);
            }

        }
        return new VtcMap(builder.build());
    }

    public Set<SeriesKey> getSeriesKeys(BrandKey brandKey) {
        HashSet<SeriesKey> seriesKeys = new HashSet<SeriesKey>();
        ArrayList<Series> seriesNamesWithYears = getSeriesNamesWithYears(brandKey);
        for (Series seriesNamesWithYear : seriesNamesWithYears) {
            String seriesName = seriesNamesWithYear.getName();
            ArrayList<Integer> years = seriesNamesWithYear.getYears();
            for (Integer year : years) {
                SeriesKey seriesKey = new SeriesKey(brandKey,year, seriesName);
                seriesKeys.add(seriesKey);
            }
        }
        return seriesKeys;
    }

    public ArrayList<Series> getSeriesNamesWithYears(BrandKey brandKey) {
        File repoBaseDir = getRepoBaseDir();

        if (repoBaseDir == null) throw new IllegalStateException();

        File brandBaseDir = new File(repoBaseDir, brandKey.getKey());

        File[] seriesNameDirs = brandBaseDir.listFiles(seriesDirFilter);

        if (seriesNameDirs == null) {
            throw new IllegalStateException("repoBaseDir[" + repoBaseDir + "] which is defined in web.xml contains no child directories. ");
        }


        ArrayList<Series> seriesNamesWithYears = new ArrayList<Series>();
        for (File seriesNameDir : seriesNameDirs) {
            String seriesName = seriesNameDir.getName();
            File[] yearDirs = seriesNameDir.listFiles(seriesDirFilter);

            Series seriesNameWithYears = new Series();
            seriesNameWithYears.setSeriesName(seriesName);

            for (File yearDir : yearDirs) {


                String sSeriesYear = yearDir.getName();
                try {
                    Integer seriesYear = new Integer(sSeriesYear);
                    seriesNameWithYears.addYear(seriesYear);
                } catch (NumberFormatException e) {
                    log.warn("Found a non-int directory name [" + sSeriesYear + "] for a seriesYear in folder [" + seriesNameDir + "]");
                }

            }

            seriesNamesWithYears.add(seriesNameWithYears);


        }
        return seriesNamesWithYears;
    }

    private static Log log = LogFactory.getLog(Repos.class);

    private static FileFilter seriesDirFilter = new FileFilter() {
        @Override
        public boolean accept(File f) {
            if (f.isHidden()) return false;
            if (f.getName().startsWith(".")) return false;
            if (!f.isDirectory()) return false;
            String name = f.getName();
            if (Strings.containsWhitespace(name)) return false;
            if (Strings.containsNonWordChar(name)) return false;
            return true;
        }
    };

    public File getFileForJpg(JpgKey jpgKey) {

        JpgWidth width = jpgKey.getWidth();

        if (!isValidJpgWidth(width)) {
            throw new IllegalArgumentException("Bad JpgWidth: " + width);
        }

        SeriesRepo seriesRepo = getSeriesRepo(jpgKey.getSeriesKey());
        RtRepo genRepo = seriesRepo.getRtRepo();
        File jpgFile = genRepo.getJpgFileName(jpgKey);

        if (!jpgFile.exists()) {
            log.warn("Creating fallback jpg on the fly: " + jpgFile);
            createJpgOnTheFly(jpgKey);

            if (!jpgFile.exists()) {
                throw new RuntimeException("Failed to create fallback jpg[" + jpgFile + "]");
            }
        }

        return jpgFile;
    }

    private void createJpgOnTheFly(JpgKey jpgKey) {
        JpgGeneratorPureJava jpgGeneratorPureJava2 = new JpgGeneratorPureJava(this, jpgKey);
        jpgGeneratorPureJava2.generate();
    }


    public SeriesId getHead(SeriesKey seriesKey) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        RootTreeId rootTreeId = seriesRepo.getSrcRepo().resolveHeadRootTreeId();
        return new SeriesId(seriesKey, rootTreeId);
    }

    public ThreedModel getThreedModel(SeriesId seriesId) {
        SeriesKey seriesKey = seriesId.getSeriesKey();
        return getThreedModel(seriesKey, seriesId.getRootTreeId());
    }

    public void createGenVersionDirs(SeriesId seriesId, JpgWidth jpgWidth) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesId.getSeriesKey());
        seriesRepo.createGenVersionDir(seriesId, jpgWidth);
    }


    public boolean isValidJpgWidth(JpgWidth width) {
        return width.isStandard() || getSettings().getJpgWidths().contains(width);
    }

    public ThreedModel getVtcThreedModel(SeriesKey seriesKey) {
        RootTreeId vtcRootTreeId = getVtcRootTreeId(seriesKey);
        SeriesId seriesId = new SeriesId(seriesKey, vtcRootTreeId);
        return getThreedModel(seriesId);
    }

    public File getVtcBaseDir(BrandKey brandKey) {
        File brandRepoBase = new File(getRepoBaseDir(), brandKey.getKey());
        File f = new File(brandRepoBase, VTC_LOCAL_DIR_NAME);
        FileUtil.createDirNotExists(f);
        return f;
    }

    public RootTreeId getVtcRootTreeId(SeriesKey seriesKey) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        return srcRepo.getVtcRootTreeId();
    }


    public File getVtcFile(SeriesKey seriesKey) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        return srcRepo.getVtcFile();
    }

    public void setVtcCommitId(SeriesKey seriesKey, RootTreeId rootTreeId) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        srcRepo.setVtcCommitId(rootTreeId);
    }
}
