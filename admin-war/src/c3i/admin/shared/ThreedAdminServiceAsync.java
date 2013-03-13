package c3i.admin.shared;

import c3i.core.threedModel.shared.CommitId;
import c3i.core.threedModel.shared.CommitKey;
import c3i.featureModel.shared.common.BrandKey;
import c3i.featureModel.shared.common.RootTreeId;
import c3i.featureModel.shared.common.SeriesKey;
import c3i.repo.shared.CommitHistory;
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ThreedAdminServiceAsync {

    void getVtcRootTreeId(SeriesKey seriesKey, AsyncCallback<RootTreeId> async);

    void setVtc(SeriesKey seriesKey, CommitKey commitKey, AsyncCallback<CommitHistory> async);


    void tagCommit(SeriesKey seriesKey, String newTagName, CommitId commitId, AsyncCallback<CommitHistory> async);

    void addAllAndCommit(SeriesKey seriesKey, String commitMessage, String tag, AsyncCallback<CommitHistory> async);

    void getCommitHistory(SeriesKey seriesKey, AsyncCallback<CommitHistory> async);

    void purgeRepoCache(BrandKey brandKey, AsyncCallback<Void> async);

    void getInitData(BrandKey brandKey, AsyncCallback<BrandInit> async);

}
