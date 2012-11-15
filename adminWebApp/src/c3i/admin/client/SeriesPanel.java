package c3i.admin.client;

import c3i.admin.client.featurePicker.CurrentUiPicks;
import c3i.admin.client.featurePicker.VarPanel;
import c3i.admin.client.featurePicker.VarPanelFactory;
import c3i.admin.client.featurePicker.VarPanelModel;
import c3i.core.featureModel.shared.FeatureModel;
import c3i.core.featureModel.shared.FixedPicks;
import c3i.core.featureModel.shared.boolExpr.Var;
import c3i.repo.shared.CommitHistory;
import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.futures.Future;
import c3i.util.shared.futures.OnException;
import c3i.util.shared.futures.OnSuccess;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import smartsoft.util.gwt.client.Console;
import smartsoft.util.gwt.client.ui.tabLabel.TabAware;
import smartsoft.util.gwt.client.ui.tabLabel.TabLabel;

import javax.annotation.Nonnull;

public class SeriesPanel extends SplitLayoutPanel implements TabAware {

    private final SeriesSession seriesSession;

    private FeaturePickerPanel featurePickerPanel;
    private ThreedAdminPanel threedAdminPanel;

    public SeriesPanel(final SeriesSession seriesSession) {
        this.seriesSession = seriesSession;

        Future<Series> seriesFuture = seriesSession.ensureSeries();

        seriesFuture.success(new OnSuccess<Series>() {
            @Override
            public void onSuccess(@Nonnull Series series) {

                featurePickerPanel = initFeaturePickerPanel(series);
                threedAdminPanel = new ThreedAdminPanel(series.getThreedAdminModel());

                addWest(featurePickerPanel, 300);
                add(threedAdminPanel);

                initialRefresh();

                series.getCurrentUiPicks().addChangeListener(new ChangeListener<FixedPicks>() {
                    @Override
                    public void onChange(FixedPicks newValue) {
                        featurePickerPanel.refresh();
                    }
                });


            }
        });

        seriesFuture.failure(new OnException() {
            @Override
            public boolean onException(Throwable e) {
                Console.error(e);
                e.printStackTrace();
                return false;
            }
        });

    }


    public ThreedAdminPanel getThreedAdminPanel() {
        return threedAdminPanel;
    }


    private FeaturePickerPanel initFeaturePickerPanel(final Series series) {

        final VarPanelFactory varPanelFactory = new VarPanelFactory();

        class MyVarPanelModel implements VarPanelModel {

            @Override
            public boolean showFieldHeadings() {
                return true;
            }

            @Override
            public boolean hideDerived() {
                return true;
            }

            @Override
            public VarPanel getVarPanel(Var var) {
                return varPanelFactory.getVarPanel(var);
            }

            @Override
            public CurrentUiPicks getPicks() {
                return series.getCurrentUiPicks();
            }

            @Override
            public String getRadioGroupPrefix() {
                return seriesSession.getSeriesViewId() + "";
            }
        }

        MyVarPanelModel varPanelContext = new MyVarPanelModel();
        varPanelFactory.setVarPanelContext(varPanelContext);

        FeatureModel featureModel = series.getThreedModel().getFeatureModel();
        final VarPanel rootVarPanel = varPanelFactory.getVarPanel(featureModel.getRootVar());


        FeaturePickerPanel fpp = new FeaturePickerPanel();
        fpp.setRootVarPanel(rootVarPanel);

        return fpp;
    }

    private void initialRefresh() {
        featurePickerPanel.refresh();
    }

    private TabLabel tabLabel;

    @Override
    public TabLabel getTabLabel() {
        if (tabLabel == null) {
            tabLabel = new MyTabLabel();
        }
        return tabLabel;
    }

    private class MyTabLabel extends TabLabel {
        private MyTabLabel() {
            super(seriesSession.getTabLabelString(), true);
            seriesSession.commit().addChangeListener(new ChangeListener<CommitHistory>() {
                @Override
                public void onChange(CommitHistory newValue) {
                    setLabel(seriesSession.getTabLabelString());
                }
            });
        }


    }

    @Override
    public void afterClose() {

    }
}