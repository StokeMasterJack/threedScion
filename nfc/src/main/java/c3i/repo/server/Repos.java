package c3i.repo.server;

import c3i.core.common.shared.BrandKey;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.imageModel.shared.BaseImageType;
import c3i.core.imageModel.shared.IBaseImageKey;
import c3i.core.imageModel.shared.PngSegment;
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
import com.google.common.base.Preconditions;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Files;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.eclipse.jgit.lib.ObjectId;
import smartsoft.util.FileUtil;
import smartsoft.util.Props;
import smartsoft.util.shared.RectSize;
import smartsoft.util.shared.Strings;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static smartsoft.util.shared.Strings.isEmpty;

public class Repos {

    public static final String VTC_LOCAL_DIR_NAME = ".vtc";

    private final static HashMap<BrandKey, File> repoBaseDirs = new HashMap<BrandKey, File>();

    private static Repos INSTANCE;

    private final BrandKey brandKey;
    private final File repoBaseDir;

    private ProfilesCache profilesCache;

    public static void setRepoBaseDir(BrandKey brandKey, File repoBaseDir) {
        File existingValue = repoBaseDirs.get(brandKey);
        if (existingValue == null) {
            repoBaseDirs.put(brandKey, repoBaseDir);
        } else if (existingValue.equals(repoBaseDir)) {
            //ignore
        } else {
            throw new IllegalStateException("repoBaseDir for " + brandKey + " already set to " + existingValue);
        }

    }


    public static Repos get() {
        return get(BrandKey.TOYOTA);
    }

    public static Repos get(BrandKey brandKey) {
        File staticRepoBaseDir = repoBaseDirs.get(brandKey);
        if (staticRepoBaseDir == null) {
            throw new IllegalStateException("Must call setRepoBaseDir(..) before calling get()");
        }
        if (INSTANCE == null) {
            INSTANCE = new Repos(brandKey, staticRepoBaseDir);
        }
        return INSTANCE;
    }

    private final LoadingCache<SeriesKey, SeriesRepo> seriesRepoCache;

    public Repos(final BrandKey brandKey, File repoBaseDir) {
        Preconditions.checkNotNull(brandKey);
        Preconditions.checkNotNull(repoBaseDir);
        this.brandKey = brandKey;
        this.repoBaseDir = repoBaseDir;

        if (!repoBaseDir.exists()) {
            throw new IllegalStateException("repoBaseDir[" + repoBaseDir + "] for brand[" + brandKey + "]  does not exist");
        }

        profilesCache = new ProfilesCache(new CacheLoader<BrandKey, Profiles>() {
            @Override
            public Profiles load(BrandKey key) throws Exception {
                return getProfiles();
            }
        });

        seriesRepoCache = CacheBuilder.newBuilder()
                .concurrencyLevel(4)
                .maximumSize(1000)
                .build(
                        new CacheLoader<SeriesKey, SeriesRepo>() {
                            @Override
                            public SeriesRepo load(SeriesKey seriesKey) throws Exception {
                                return new SeriesRepo(Repos.this, Repos.this.repoBaseDir, seriesKey);
                            }
                        });


        FileUtil.createDirNotExists(getVtcBaseDir());
        File cacheDir = getCacheDir();
        FileUtil.createDirNotExists(cacheDir);

    }

    public ProfilesCache getProfilesCache() {
        return profilesCache;
    }

    public void purgeCache() {
        seriesRepoCache.invalidateAll();
    }

    public SeriesRepo getSeriesRepo(String brand, String series, int year) {
        SeriesKey seriesKey = new SeriesKey(BrandKey.fromString(brand), year, series);
        return getSeriesRepo(seriesKey);
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

    public VtcMap getVtcMap() {
        ImmutableMap.Builder<SeriesKey, RootTreeId> builder = ImmutableMap.builder();
        Set<SeriesKey> seriesKeys = getSeriesKeys();
        for (SeriesKey seriesKey : seriesKeys) {
            try {
                RootTreeId vtcRootTreeId = getVtcRootTreeId(seriesKey);
                builder.put(seriesKey, vtcRootTreeId);
            } catch (UnableToResolveRevisionParameterException e) {
                log.warning("Problem getting vtc RootTreeId for seriesKey[" + seriesKey + "]  - unable to resolve HEAD");
            } catch (Exception e) {
                log.log(Level.WARNING, "Problem getting vtc RootTreeId for seriesKey[" + seriesKey + "]", e);
            }

        }
        return new VtcMap(builder.build());
    }


    public Brand getBrandInitData() {
        VtcMap vtcMap = getVtcMap();
        Profiles profiles = getProfiles();
        Map<String, String> config = getConfig();
        return new Brand(brandKey, vtcMap, profiles, config);
    }

    private Profiles profiles;

    public Profiles getProfiles() {

        if (profiles == null) {
            ArrayList<Profile> a = new ArrayList<Profile>();
            ObjectNode o = getProfilesAsJson();
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
                        log.log(Level.SEVERE, "Problem reading baseImageType", e);
                        baseImageType = BaseImageType.JPG;
                    }
                } else {
                    baseImageType = BaseImageType.JPG;
                }

                Profile p = new Profile(profileKey, rectSize, baseImageType);
                a.add(p);
            }

            profiles = new Profiles(a);
        }

        return profiles;

    }

    public ObjectNode getProfilesAsJson() {
        ObjectMapper mapper = new ObjectMapper();
        File repoBaseDir = getRepoBaseDir();
        if (repoBaseDir == null) throw new IllegalStateException();
        File brandBaseDir = repoBaseDir;
        if (!brandBaseDir.exists()) return mapper.createObjectNode();
        File profileFile = new File(brandBaseDir, ".profiles/profiles.json");

        if (!profileFile.exists()) throw new IllegalStateException("Missing profiles file: " + profileFile);

        try {
            String jsonString = Files.toString(profileFile, Charset.defaultCharset());
            try {
                return mapper.readValue(jsonString, ObjectNode.class);
            } catch (IOException e) {
                System.err.println("----");
                System.err.println(jsonString);
                System.err.println("----");
                throw new RuntimeException("Problem parsing file: " + profileFile, e);
            }
        } catch (IOException e) {
            throw new RuntimeException("Problem parsing file: " + profileFile, e);
        }
    }

    public Map<String, String> getConfig() {
        File repoBaseDir = getRepoBaseDir();
        if (repoBaseDir == null) throw new IllegalStateException();
        File brandBaseDir = repoBaseDir;
        if (!brandBaseDir.exists()) {
            log.warning("Missing config brandBaseDir: " + brandBaseDir);
            return ImmutableMap.of();
        }

        File configFile = new File(brandBaseDir, ".profiles/config.properties");
        if (!configFile.exists()) {
            log.warning("Missing config file: " + configFile);
            return ImmutableMap.of();
        }

        return Props.readFromFile(configFile);

    }

    public Set<SeriesKey> getSeriesKeys() {
        HashSet<SeriesKey> seriesKeys = new HashSet<SeriesKey>();
        ArrayList<Series> seriesNamesWithYears = getSeriesNamesWithYears();
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

    public ArrayList<Series> getSeriesNamesWithYears() {
        File repoBaseDir = getRepoBaseDir();

        if (repoBaseDir == null) throw new IllegalStateException();

        File brandBaseDir = repoBaseDir;

        File[] seriesNameDirs = brandBaseDir.listFiles(seriesDirFilter);

        if (seriesNameDirs == null) {
            throw new IllegalStateException("repoBaseDir[" + repoBaseDir + "] which is defined in web.xml contains no child directories. ");
        }

//        System.out.println("seriesNameDirs.length = " + seriesNameDirs.length);
//        System.out.println("seriesNameDirs = " + Arrays.toString(seriesNameDirs));

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
                    log.warning("Found a non-int directory name [" + sSeriesYear + "] for a seriesYear in folder [" + seriesNameDir + "]");
                }
            }

            seriesNamesWithYears.add(seriesNameWithYears);


        }
        return seriesNamesWithYears;
    }

    private static Logger log = Logger.getLogger("c3i");

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

    public File getFileForZPng(SeriesKey sk, int width, PngSegment pngKey) {
        SeriesRepo seriesRepo = getSeriesRepo(sk);
        RtRepo genRepo = seriesRepo.getRtRepo();

        File pngFile = genRepo.getZPngFileName(pngKey);

        if (!pngFile.exists()) {
            log.warning("Creating fallback zPng on the fly: " + pngFile);
            createZPngOnTheFly(width, sk, pngKey);

            if (!pngFile.exists()) {
                throw new RuntimeException("Failed to create fallback zPng[" + pngFile + "]");
            }
        }

        return pngFile;
    }

    private void createZPngOnTheFly(int width, SeriesKey sk, PngSegment pngKey) {
        ZPngGenerator zPngGenerator = new ZPngGenerator(this, width, sk, pngKey);
        zPngGenerator.generate();
    }

    public File getFileForJpg(IBaseImageKey jpgKey) {
        SeriesRepo seriesRepo = getSeriesRepo(jpgKey.getSeriesKey());
        RtRepo genRepo = seriesRepo.getRtRepo();
        File jpgFile = genRepo.getBaseImageFileName(jpgKey);

        if (!jpgFile.exists()) {
            log.warning("Creating fallback jpg on the fly: " + jpgFile);
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


    public File getFileNameForCachedThreedModelJson(SeriesId seriesId) {
        File cacheDir = getCacheDir();
        File threedModelsDir = new File(cacheDir, "threedModels");
        File seriesDir = new File(threedModelsDir, seriesId.getSeriesKey().getKey());
        return new File(seriesDir, seriesId.getRootTreeId().toString() + ".json");
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

    public File getVtcBaseDir() {
        File f = new File(getRepoBaseDir(), VTC_LOCAL_DIR_NAME);
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

    public File getCacheDir() {
        return new File(repoBaseDir, ".cache");
    }

    public static Repos getToyotaRepoTest() {
        return new Repos(BrandKey.TOYOTA, new File("/configurator-content-toyota"));
    }

    public static Repos getScionRepoTest() {
        return new Repos(BrandKey.SCION, new File("/configurator-content-scion"));
    }
}
