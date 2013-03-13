package c3i.admin.shared;

import c3i.core.threedModel.shared.CommitId;
import c3i.core.threedModel.shared.CommitKey;
import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.repo.shared.CommitHistory;
import c3i.repo.shared.RepoHasNoHeadException;
import com.google.gwt.user.client.rpc.RemoteService;

/**
 * These are the ThreedAdminService remote calls that use gwt-rpc
 */
public interface ThreedAdminService extends RemoteService {

    BrandInit getInitData(BrandKey brandKey);

    /**
     * Redundant with rest-json call: /configurator-content/avalon/2011/vtc.txt
     */
    RootTreeId getVtcRootTreeId(SeriesKey seriesKey);

    CommitHistory setVtc(SeriesKey seriesKey, CommitKey commitKey);

    CommitHistory getCommitHistory(SeriesKey seriesKey) throws RepoHasNoHeadException;

    CommitHistory tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId);

    CommitHistory addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag) throws Exception;

    void purgeRepoCache(BrandKey brandKey);
}
