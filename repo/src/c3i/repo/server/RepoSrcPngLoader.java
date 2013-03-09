package c3i.repo.server;

import c3i.core.common.server.SrcPngLoader;
import com.google.common.io.InputSupplier;

import java.io.InputStream;

public class RepoSrcPngLoader implements SrcPngLoader {

    private final SeriesRepo seriesRepo;

    public RepoSrcPngLoader(SeriesRepo seriesRepo) {
        this.seriesRepo = seriesRepo;
    }

    @Override
    public InputSupplier<? extends InputStream> getPng(String pngShortSha) {
        return seriesRepo.getSrcPngSupplier(pngShortSha);
    }
}
