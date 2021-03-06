package c3i.repo.server;

import c3i.featureModel.shared.FeatureModel;
import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.imageModel.shared.BaseImageType;
import c3i.imageModel.shared.Profile;
import c3i.imageModel.shared.Profiles;
import c3i.repo.RepoConfig;
import c3i.repo.shared.CommitHistory;
import c3i.repo.shared.Series;
import c3i.threedModel.client.ThreedModel;
import c3i.threedModel.shared.Brand;
import c3i.threedModel.shared.CommitKey;
import c3i.threedModel.shared.VtcMap;
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
import smartsoft.util.shared.RectSize;
import smartsoft.util.shared.Strings;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static smartsoft.util.shared.Strings.isEmpty;

public class BrandRepo {

    public static final String VTC_LOCAL_DIR_NAME = ".vtc";

//    private final static HashMap<BrandKey, File> repoBaseDirs = new HashMap<BrandKey, File>();

    private final LoadingCache<SeriesKey, SeriesRepo> seriesRepoCache;


    private final BrandKey brandKey;
    private final File repoBaseDir;

    private ProfilesCache profilesCache;

    private final RepoConfig repoConfig;

    public BrandRepo(BrandKey brandKey, File baseDir) {
        this(new RepoConfig(brandKey, baseDir));
    }

    public BrandRepo(RepoConfig repoConfig) {
        Preconditions.checkNotNull(repoConfig);
        this.repoConfig = repoConfig;
        this.brandKey = repoConfig.getBrandKey();
        this.repoBaseDir = repoConfig.getBaseDir();


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
                                return new SeriesRepo(BrandRepo.this, BrandRepo.this.repoBaseDir, seriesKey);
                            }
                        });


        FileUtil.createDirNotExists(getVtcBaseDir());
        File cacheDir = getCacheDir();
        FileUtil.createDirNotExists(cacheDir);

    }


//    public static BrandRepo get() {
//        return get(BrandKey.TOYOTA);
//    }
//
//    public static BrandRepo get(BrandKey brandKey) {
//        File staticRepoBaseDir = repoBaseDirs.get(brandKey);
//        if (staticRepoBaseDir == null) {
//            throw new IllegalStateException("Must call setRepoBaseDir(..) before calling get()");
//        }
//        if (INSTANCE == null) {
//            INSTANCE = new BrandRepo(brandKey, staticRepoBaseDir);
//        }
//        return INSTANCE;
//    }


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

    public RepoConfig getRepoConfig() {
        return repoConfig;
    }

    public File getRepoBaseDir() {
        return getRepoConfig().getBaseDir();
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

    public BrandKey getBrandKey() {
        return getRepoConfig().getBrandKey();
    }

    public Brand getBrandInitData() {
        VtcMap vtcMap = getVtcMap();
        Profiles profiles = getProfiles();
        return new Brand(brandKey, vtcMap, profiles);
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
            return mapper.readValue(jsonString, ObjectNode.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

        System.out.println("seriesNameDirs.length = " + seriesNameDirs.length);
        System.out.println("seriesNameDirs = " + Arrays.toString(seriesNameDirs));

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

    public static BrandRepo testRepoToyota() {
        RepoConfig cfg = RepoConfig.testRepoToyota();
        return new BrandRepo(cfg);
    }

    public static BrandRepo testRepoScion() {
        RepoConfig cfg = RepoConfig.testRepoScion();
        return new BrandRepo(cfg);
    }

    public static BrandRepo get() {
        return testRepoToyota();
    }
}
