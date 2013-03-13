package c3i.repoWebService;

import c3i.featureModel.shared.common.BrandKey;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import com.google.common.base.Preconditions;

public abstract class ReposHandler<T extends RepoRequest> {

    protected final BrandRepos brandRepos;

    protected ReposHandler(BrandRepos brandRepos) {
        Preconditions.checkNotNull(brandRepos);
        this.brandRepos = brandRepos;
    }

    public Repos getRepos(BrandKey brandKey) {
        return brandRepos.getRepos(brandKey);
    }


    public BrandRepos getBrandRepos() {
        return brandRepos;
    }

    public abstract void handle(T reposRequest);

}
