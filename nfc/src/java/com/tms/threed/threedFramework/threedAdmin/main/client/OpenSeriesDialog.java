package com.tms.threed.threedFramework.threedAdmin.main.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.tms.threed.threedFramework.threedAdmin.jpgGen.client.CommitPickList;
import com.tms.threed.threedFramework.repo.shared.CommitHistory;
import com.tms.threed.threedFramework.threedModel.shared.SeriesKey;


public class OpenSeriesDialog extends MyDialogBox {

    private final CommitPickList commitPickList;

    public OpenSeriesDialog(final SeriesKey seriesKey, final CommitHistory head, final CommitPickList.Callback callback) {
        super("Pick Series Version");

        CommitPickList.Callback callback1 = new CommitPickList.Callback() {
            @Override
            public void openButtonClicked(SeriesKey seriesKey, CommitHistory ch) {
                callback.openButtonClicked(seriesKey, ch);
                OpenSeriesDialog.this.hide();
            }

            @Override
            public void checkinButtonClicked(SeriesKey seriesKey) {
                callback.checkinButtonClicked(seriesKey);
                OpenSeriesDialog.this.hide();
            }
        };

        commitPickList = new CommitPickList(seriesKey, head, callback1);


        FlowPanel fp = new FlowPanel();


        fp.add(new HTML("<div style='font-size:2em;font-weight:bold;padding-top:0em;padding-bottom:.5em;'>" + seriesKey.toStringPretty() + "</div>"));
        fp.add(commitPickList);


        fp.getElement().getStyle().setPadding(1, Style.Unit.EM);

        setWidget(fp);

        center();




    }


}
