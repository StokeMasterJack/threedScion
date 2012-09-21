package c3i.repo.server;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.imageModel.shared.BaseImageType;
import c3i.core.imageModel.shared.IBaseImageKey;
import c3i.core.imageModel.shared.PngKey;
import c3i.core.imageModel.shared.Profile;
import c3i.core.imageModel.shared.Profiles;
import c3i.core.threedModel.shared.Brand;
import c3i.core.threedModel.shared.CommitKey;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.core.threedModel.shared.VtcMap;
import c3i.jpgGen.server.singleJpg.BaseImageGenerator;
import c3i.jpgGen.server.singleJpg.ZPngGenerator;
import c3i.repo.server.rt.RtRepo;
import c3i.repo.shared.CommitHistory;
import c3i.repo.shared.Series;
import c3i.repo.shared.Settings;
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.eclipse.jgit.lib.ObjectId;
import smartsoft.util.FileUtil;
import smartsoft.util.lang.shared.RectSize;
import smartsoft.util.lang.shared.Strings;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import static smartsoft.util.lang.shared.Strings.isEmpty;

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

    public SeriesRepo getSeriesRepo(BrandKey brandKey, String seriesName, int seriesYear) {
        return getSeriesRepo(new SeriesKey(brandKey, seriesYear, seriesName));
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

    public ThreedModel getThreedModel(BrandKey brandKey, String seriesName, int seriesYear, RootTreeId rootTreeId) {
        SeriesKey seriesKey = new SeriesKey(brandKey, seriesYear, seriesName);
        return getThreedModel(seriesKey, rootTreeId);
    }

    public ThreedModel getThreedModelForHead(SeriesKey seriesKey) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        return seriesRepo.getThreedModelHead();
    }

    public ThreedModel getThreedModel(BrandKey brandKey, String seriesName, int seriesYear) {
        SeriesKey seriesKey = new SeriesKey(brandKey, seriesYear, seriesName);
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


    public Brand getBrandInitData(BrandKey brandKey) {
        VtcMap vtcMap = getVtcMap(brandKey);
        Profiles profiles = getProfiles(brandKey);
        return new Brand(brandKey, vtcMap, profiles);
    }

    public Profiles getProfiles(BrandKey brandKey) {
        ArrayList<Profile> a = new ArrayList<Profile>();
        ObjectNode o = getProfilesAsJson(brandKey);
        Iterator<String> fieldNames = o.getFieldNames();
        while (fieldNames.hasNext()) {


            String profileKey = fieldNames.next();
            JsonNode profile = o.get(profileKey);

            JsonNode imageSize = profile.get("image");

            int w = imageSize.get("w").getIntValue();
            int h = imageSize.get("h").getIntValue();
            RectSize rectSize = new RectSize(w, h);

            BaseImageType baseImageType;
            JsonNode jsBaseImageType = profile.get("baseImageType");
            if (jsBaseImageType != null) {
                try {
                    String sBaseImageType = jsBaseImageType.getTextValue();
                    if (isEmpty(sBaseImageType)) {
                        baseImageType = BaseImageType.JPG;
                    } else {
                        baseImageType = BaseImageType.valueOf(sBaseImageType);
                    }
                } catch (IllegalArgumentException e) {
                    log.error("Problem reading baseImageType", e);
                    baseImageType = BaseImageType.JPG;
                }
            } else {
                baseImageType = BaseImageType.JPG;
            }

            Profile p = new Profile(profileKey, rectSize, baseImageType);
            a.add(p);
        }

        return new Profiles(a);

    }

    public ObjectNode getProfilesAsJson(BrandKey brandKey) {
        ObjectMapper mapper = new ObjectMapper();
        File repoBaseDir = getRepoBaseDir();
        if (repoBaseDir == null) throw new IllegalStateException();
        File brandBaseDir = new File(repoBaseDir, brandKey.getKey());
        if (!brandBaseDir.exists()) return mapper.createObjectNode();
        File profileFile = new File(brandBaseDir, ".profiles/profiles.json");

        if (!profileFile.exists()) throw new IllegalStateException("Missing profiles file: " + profileFile);

        try {
            String jsonString = Files.toString(profileFile, Charset.defaultCharset());
            return mapper.readValue(jsonString, ObjectNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Set<SeriesKey> getSeriesKeys(BrandKey brandKey) {
        HashSet<SeriesKey> seriesKeys = new HashSet<SeriesKey>();
        ArrayList<Series> seriesNamesWithYears = getSeriesNamesWithYears(brandKey);
        for (Series seriesNamesWithYear : seriesNamesWithYears) {
            String seriesName = seriesNamesWithYear.getName();
            ArrayList<Integer> years = seriesNamesWithYear.getYears();
            for (Integer year : years) {
                SeriesKey seriesKey = new SeriesKey(brandKey, year, seriesName);
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

    public File getFileForZPng(SeriesKey sk, int width,PngKey pngKey) {
        SeriesRepo seriesRepo = getSeriesRepo(sk);
        RtRepo genRepo = seriesRepo.getRtRepo();

        File pngFile = genRepo.getZPngFileName(pngKey);

        if (!pngFile.exists()) {
            log.warn("Creating fallback zPng on the fly: " + pngFile);
            createZPngOnTheFly(width,sk,pngKey);

            if (!pngFile.exists()) {
                throw new RuntimeException("Failed to create fallback zPng[" + pngFile + "]");
            }
        }

        return pngFile;
    }

    private void createZPngOnTheFly(int width, SeriesKey sk, PngKey pngKey) {
        ZPngGenerator zPngGenerator = new ZPngGenerator(this, width, sk, pngKey);
        zPngGenerator.generate();
    }

    public File getFileForJpg(IBaseImageKey jpgKey) {
        SeriesRepo seriesRepo = getSeriesRepo(jpgKey.getSeriesKey());
        RtRepo genRepo = seriesRepo.getRtRepo();
        File jpgFile = genRepo.getBaseImageFileName(jpgKey);

        if (!jpgFile.exists()) {
            log.warn("Creating fallback jpg on the fly: " + jpgFile);
            createJpgOnTheFly(jpgKey);

            if (!jpgFile.exists()) {
                throw new RuntimeException("Failed to create fallback jpg[" + jpgFile + "]");
            }
        }

        return jpgFile;
    }

    private void createJpgOnTheFly(IBaseImageKey jpgKey) {
        BaseImageGenerator jpgGeneratorPureJava2 = new BaseImageGenerator(this, jpgKey);
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

    public void createGenVersionDirs(SeriesId seriesId, Profile profile) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesId.getSeriesKey());
        seriesRepo.createGenVersionDir(seriesId, profile);
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

    public CommitHistory setVtcCommitId(SeriesKey seriesKey, CommitKey commitKey) {
        SeriesRepo seriesRepo = getSeriesRepo(seriesKey);
        SrcRepo srcRepo = seriesRepo.getSrcRepo();
        srcRepo.setVtcRootTreeId(commitKey.getRootTreeId());
        ObjectId commitId = srcRepo.toGitObjectId(commitKey.getCommitId());
        return srcRepo.getCommitHistory(commitId);
    }


}
