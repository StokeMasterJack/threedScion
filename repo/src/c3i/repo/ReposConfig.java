package c3i.repo;

import c3i.featureModel.shared.common.BrandKey;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class ReposConfig {

    private final ImmutableSet<RepoConfig> repoConfigs;

    public ReposConfig(RepoConfig... repoConfig) {
        this.repoConfigs = ImmutableSet.copyOf(repoConfig);
    }

    public ReposConfig(RepoConfig repoConfig) {
        this.repoConfigs = ImmutableSet.of(repoConfig);
    }

    public ReposConfig(File... baseDirs) {
        HashSet<RepoConfig> repoConfigs = new HashSet<RepoConfig>();
        for (File baseDir : baseDirs) {
            RepoConfig repoConfig = new RepoConfig(baseDir);
            repoConfigs.add(repoConfig);
        }
        this.repoConfigs = ImmutableSet.copyOf(repoConfigs);
    }

    public ReposConfig(ImmutableMap<BrandKey, File> repoBaseDirs) {
        HashSet<RepoConfig> s = new HashSet<RepoConfig>();
        for (Map.Entry<BrandKey, File> entry : repoBaseDirs.entrySet()) {
            s.add(new RepoConfig(entry.getKey(), entry.getValue()));
        }
        this.repoConfigs = ImmutableSet.copyOf(s);
    }

    public ReposConfig(BrandKey brandKey, File baseDir) {
        this.repoConfigs = ImmutableSet.of(new RepoConfig(brandKey, baseDir));
    }

    public ReposConfig(Collection<RepoConfig> repoConfigs) {
        this.repoConfigs = ImmutableSet.copyOf(repoConfigs);
    }

    public ImmutableSet<RepoConfig> getRepoConfigs() {
        return repoConfigs;
    }

    public RepoConfig getRepoConfig(BrandKey brandKey) {
        for (RepoConfig repoConfig : repoConfigs) {
            if (repoConfig.getBrandKey().equals(brandKey)) {
                return repoConfig;
            }
        }
        throw new IllegalArgumentException("No RepoConfig found for[" + brandKey + "]");
    }

    public static ReposConfig testRepos() {
        return new ReposConfig(RepoConfig.testRepoToyota(), RepoConfig.testRepoScion());
    }

    public static ReposConfig testRepos(BrandKey brandKey) {
        return new ReposConfig(RepoConfig.testRepo(brandKey));
    }

    public static ReposConfig testReposToyota() {
        return testRepos(BrandKey.TOYOTA);
    }

    public static ReposConfig testReposScion() {
        return testRepos(BrandKey.SCION);
    }

    public void test1() throws Exception {
//        args.putRaw(Args.REPO_BASE, "/configurator-content-toyota");
    }

    public File getRepoBaseDir(BrandKey brandKey) {
        return getRepoConfig(brandKey).getBaseDir();
    }
}
