package c3i.imgGen.repoImpl;

import c3i.featureModel.shared.common.SeriesKey;
import c3i.imgGen.api.SrcPngLoader;
import c3i.repo.server.BrandRepos;
import c3i.repo.server.SeriesRepo;
import com.google.common.io.InputSupplier;
import org.eclipse.jgit.lib.ObjectLoader;

import java.io.IOException;
import java.io.InputStream;

public class SrcPngLoaderRepo implements SrcPngLoader {

    private final BrandRepos brandRepos;

    public SrcPngLoaderRepo(BrandRepos brandRepos) {
        this.brandRepos = brandRepos;
    }

    @Override
    public InputSupplier<? extends InputStream> getPng(final SeriesKey seriesKey, final String pngShortSha) {
        return new InputSupplier<InputStream>() {
            @Override
            public InputStream getInput() throws IOException {
                SeriesRepo seriesRepo = brandRepos.getSeriesRepo(seriesKey);
                ObjectLoader objectLoader = seriesRepo.getSrcPngByShortSha(pngShortSha);
                return objectLoader.openStream();
            }
        };
    }
}