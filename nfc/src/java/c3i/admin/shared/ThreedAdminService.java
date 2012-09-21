package c3i.admin.shared;

import com.google.gwt.rpc.client.RpcService;
import c3i.core.common.shared.BrandKey;
import c3i.core.threedModel.shared.CommitId;
import c3i.core.threedModel.shared.CommitKey;
import c3i.repo.shared.*;
import c3i.core.threedModel.shared.RootTreeId;
import c3i.core.common.shared.SeriesKey;

/**
 * These are the ThreedAdminService remote calls that use gwt-rpc
 */
public interface ThreedAdminService extends RpcService {

    BrandInit getInitData(BrandKey brandKey);

    /**
     * Redundant with rest-json call: /configurator-content/avalon/2011/vtc.txt
     */
    RootTreeId getVtcRootTreeId(SeriesKey seriesKey);

    CommitHistory setVtc(SeriesKey seriesKey, CommitKey commitKey);

    Settings getSettings();

    void saveSettings(Settings config);

    CommitHistory getCommitHistory(SeriesKey seriesKey) throws RepoHasNoHeadException;

    CommitHistory tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId);

    CommitHistory addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag) throws Exception;

    void purgeRepoCache();
}
