package c3i.admin.client;

import c3i.admin.shared.BrandInit;
import c3i.core.common.shared.SeriesId;
import c3i.core.common.shared.SeriesKey;
import c3i.core.threedModel.shared.ThreedModel;
import c3i.repo.shared.CommitHistory;
import c3i.repo.shared.SeriesCommit;
import c3i.smartClient.client.service.ThreedModelLoader;
import c3i.util.shared.futures.*;

import javax.annotation.Nonnull;

public class SeriesSession {

    private static int counter;

    private final int seriesViewId = counter++;

    private final BrandInit brand;
    private final SeriesKey seriesKey;

    private final RWValue<CommitHistory> commit;


    private final Loader<SeriesId, ThreedModel> threedModelLoader;
    private final Loader<SeriesId, Series> seriesLoader;

    private final String initView;

    //cache
    private final SeriesId seriesId;
    private final App app;

    public SeriesSession(final App app, final BrandInit brand, final SeriesCommit seriesCommit, final String initView) {

        //core state
        this.app = app;
        this.brand = brand;
        this.seriesKey = seriesCommit.getSeriesKey();
        this.commit = new Value<CommitHistory>("commit",seriesCommit.getCommitHistory());
        this.initView = initView;

        //cache
        this.seriesId = new SeriesId(seriesKey, seriesCommit.getRootTreeId());

        this.threedModelLoader = new ThreedModelLoader(app.getThreedModelClient(), seriesId);

        this.seriesLoader = new Loader<SeriesId, Series>(seriesId, new AsyncFunction<SeriesId, Series>() {
            @Override
            public void start(SeriesId arg, final Completer<Series> completer) throws Exception {

                Future<ThreedModel> ff = threedModelLoader.ensureLoaded();
                ff.success(new OnSuccess<ThreedModel>() {
                    @Override
                    public void onSuccess(@Nonnull ThreedModel threedModel) {
                        Series series = new Series(app, brand, threedModel, commit,initView);
                        completer.setResult(series);
                    }
                });

                ff.failure(new OnException() {
                    @Override
                    public boolean onException(Throwable e) {
                        completer.setException(e);
                        return true;
                    }
                });
            }
        });

    }


    public BrandInit getBrand() {
        return brand;
    }

    public SeriesKey getSeriesKey() {
        return seriesKey;
    }

    public Loader<SeriesId, Series> getSeriesLoader() {
        return seriesLoader;
    }

    public Future<Series> ensureSeries() {
        return getSeriesLoader().ensureLoaded();
    }

    public SeriesId getSeriesId() {
        return seriesId;
    }

    public App getApp() {
        return app;
    }


    public int getSeriesViewId() {
        return seriesViewId;
    }

    public RWValue<CommitHistory> commit() {
        return commit;
    }

    public String getTabLabelString() {
        return seriesKey.toStringPretty() + " [" + commit.get().getDisplayName() + "]";
    }


}
