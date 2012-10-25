package c3i.repoWebService;

import c3i.core.common.shared.BrandKey;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.Repos;
import com.google.common.base.Preconditions;

import javax.servlet.ServletContext;

public abstract class ReposHandler<T extends RepoRequest> {

    protected final BrandRepos brandRepos;
    protected final ServletContext application;

    protected ReposHandler(BrandRepos brandRepos, ServletContext application) {
        Preconditions.checkNotNull(brandRepos);
        Preconditions.checkNotNull(application);

        this.brandRepos = brandRepos;
        this.application = application;
    }

    public Repos getRepos(BrandKey brandKey) {
        return brandRepos.getRepos(brandKey);
    }


    public BrandRepos getBrandRepos() {
        return brandRepos;
    }

    public ServletContext getApplication() {
        return application;
    }

    public abstract void handle(T reposRequest);

}
