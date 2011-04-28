package com.tms.threed.threedFramework.repo.server;

import com.google.common.io.Files;
import com.tms.threed.threedFramework.repo.server.RepoUtil;
import com.tms.threed.threedFramework.repo.server.Repos;
import com.tms.threed.threedFramework.repo.server.SeriesRepo;
import com.tms.threed.threedFramework.repo.server.SrcRepo;
import com.tms.threed.threedFramework.repo.shared.CommitId;
import com.tms.threed.threedFramework.repo.shared.RootTreeId;
import com.tms.threed.threedFramework.threedCore.config.ThreedConfig;
import com.tms.threed.threedFramework.threedCore.shared.SeriesKey;
import org.eclipse.jgit.lib.ObjectId;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

import static com.tms.threed.threedFramework.util.lang.shared.Strings.isEmpty;

public class VtcService {

    private final Repos repos;
    private final File vtcBaseDir;

    public VtcService(final Repos repos) {
        this.repos = repos;
        this.vtcBaseDir = ThreedConfig.getVtcBaseDir();
        RepoUtil.createDirNotExists(vtcBaseDir);
    }

    public RootTreeId getVtcRootTreeId(SeriesKey seriesKey) {
        File vtcFile = getVtcFile(seriesKey);

        if (vtcFile.exists()) {
            String sha;
            try {
                sha = Files.toString(vtcFile, Charset.defaultCharset());
                if (isEmpty(sha))
                    throw new RuntimeException("vtcVersion file [" + vtcFile.getAbsolutePath() + "] contains and empty sha");
                if (!ObjectId.isId(sha))
                    throw new RuntimeException("vtcVersion file [" + vtcFile.getAbsolutePath() + "] contains an invalid sha [" + sha + "].");
                return new RootTreeId(sha);
            } catch (IOException e) {
                throw new RuntimeException("Problem reading vtcVersion from file [" + vtcFile.getAbsolutePath() + "]", e);
            }
        } else {
            SeriesRepo seriesRepo = repos.getSeriesRepo(seriesKey);
            SrcRepo srcRepo = seriesRepo.getSrcRepo();
            ObjectId rootTreeId = srcRepo.resolveCommitHead();
            RootTreeId rootTreeId1 = new RootTreeId(rootTreeId.getName());
            setVtcCommitId(seriesKey, rootTreeId1);
            return rootTreeId1;
        }

    }

    public File getVtcFile(SeriesKey seriesKey) {
        String fileName = seriesKey.getName() + "-" + seriesKey.getYear() + ".txt";
        return new File(vtcBaseDir, fileName);
    }


    public void setVtcCommitId(SeriesKey seriesKey, RootTreeId rootTreeId) {
        File vtcFile = getVtcFile(seriesKey);
        try {
            Files.write(rootTreeId.getName(), vtcFile, Charset.defaultCharset());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
