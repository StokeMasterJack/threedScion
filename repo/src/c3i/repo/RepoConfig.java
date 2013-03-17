package c3i.repo;

import c3i.featureModel.shared.common.BrandKey;

import java.io.File;

public class RepoConfig {


    private final BrandKey brandKey;
    private final File baseDir;

    public RepoConfig(BrandKey brandKey, String baseDir) {
        this(brandKey, new File(baseDir));
    }

    public RepoConfig(BrandKey brandKey, File baseDir) {
        this.brandKey = brandKey;
        this.baseDir = baseDir;
    }

    public RepoConfig(File baseDir) {
        BrandKey tmpBrandKey = null;
        for (BrandKey k : BrandKey.getAll()) {

            String pathAllLower = baseDir.getPath().toLowerCase();
            String keyAllLower = k.getKey().toLowerCase();

            if (pathAllLower.contains(keyAllLower)) {
                tmpBrandKey = k;
                break;
            }
        }
        if (tmpBrandKey == null) {
            tmpBrandKey = BrandKey.TOYOTA;
        }
        checkBaseDir(baseDir, tmpBrandKey);

        this.brandKey = tmpBrandKey;
        this.baseDir = baseDir;
    }

    private void checkBaseDir(File baseDir, BrandKey brandKey) {
        if (!baseDir.exists()) {
            throw new IllegalStateException("repoBaseDir[" + baseDir + "] for brand[" + brandKey + "]  does not exist");
        }
    }

    public BrandKey getBrandKey() {
        return brandKey;
    }

    public File getBaseDir() {
        return baseDir;
    }

    public static RepoConfig testRepo(BrandKey brandKey) {
        return new RepoConfig(BrandKey.TOYOTA, "/configurator-content-" + brandKey.getKey().toLowerCase());
    }

    public static RepoConfig testRepoToyota() {
        return testRepo(BrandKey.TOYOTA);
    }

    public static RepoConfig testRepoScion() {
        return testRepo(BrandKey.SCION);
    }
}
