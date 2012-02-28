package com.tms.threed.threedFramework.threedAdmin.jpgGen.client;

import com.google.gwt.dom.client.Style;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.tms.threed.threedFramework.threedAdmin.main.client.services.ThreedAdminService1Async;
import com.tms.threed.threedFramework.repo.shared.JpgWidth;
import com.tms.threed.threedFramework.threedAdmin.main.client.services.FetchJpgGenStatusCallback;
import com.tms.threed.threedFramework.threedModel.shared.SeriesId;
import com.tms.threed.threedFramework.threedModel.shared.Slice;

import javax.annotation.Nonnull;
import java.util.ArrayList;


public class JpgGenStatusPanel extends DockLayoutPanel implements TabCloseListener {

    private final ThreedAdminService1Async service;

    private ArrayList<SliceStatus> sliceList = new ArrayList<SliceStatus>();

    private CellTable<SliceStatus> table = new CellTable<SliceStatus>();
    private final Timer timer;

    private final SeriesId seriesId;
    private final JpgWidth jpgWidth;

    public JpgGenStatusPanel(final ThreedAdminService1Async service,SeriesId seriesId, JSONArray jsViews, JpgWidth jpgWidth) {
        super(Style.Unit.EM);
        this.service = service;

        this.seriesId = seriesId;
        this.jpgWidth = jpgWidth;
        final FlowPanel fp = new FlowPanel();

        for (int i = 0; i < jsViews.size(); i++) {
            JSONObject jsView = jsViews.get(i).isObject();
            System.out.println("jsView = " + jsView);

            String viewName = jsView.get("view").isString().stringValue();
            int angleCount = (int) jsView.get("angleCount").isNumber().doubleValue();

            SliceStatus sliceStatus = new SliceStatus();

            for (int angle = 1; angle <= angleCount; angle++) {
                Slice slice = new Slice(viewName,angle);
                sliceStatus.slice = slice;
                sliceList.add(sliceStatus);
            }


        }


        TextColumn<SliceStatus> viewColumn = new TextColumn<SliceStatus>() {
            @Override
            public String getValue(SliceStatus slice) {
                return slice.getViewName();
            }
        };

        TextColumn<SliceStatus> angleColumn = new TextColumn<SliceStatus>() {
            @Override
            public String getValue(SliceStatus slice) {
                return slice.getAngle() + "";
            }
        };

        TextColumn<SliceStatus> jpgCountColumn = new TextColumn<SliceStatus>() {
            @Override
            public String getValue(SliceStatus slice) {
                Integer jpgCount = slice.getJpgCount();
                if (jpgCount == null) return null;
                return jpgCount + "";
            }
        };

        TextColumn<SliceStatus> jpgSetColumn = new TextColumn<SliceStatus>() {
            @Override
            public String getValue(SliceStatus slice) {
                Boolean jpgSet = slice.getJpgSet();
                if (jpgSet == null || jpgSet.equals(false)) return null;

                return "Complete";
            }
        };


        TextColumn<SliceStatus> jpgGenColumn = new TextColumn<SliceStatus>() {
            @Override
            public String getValue(SliceStatus slice) {
                Boolean jpgGen = slice.getJpgGen();
                if (jpgGen == null || jpgGen.equals(false)) return null;

                return "Complete";
            }
        };

        TextColumn<SliceStatus> missingJpgCountColumn = new TextColumn<SliceStatus>() {
            @Override
            public String getValue(SliceStatus slice) {
                Integer missingJpgCount = slice.getMissingJpgCount();
                if (missingJpgCount == null) return null;
                return missingJpgCount + "";
            }
        };

        table.addColumn(viewColumn, "View");
        table.addColumn(angleColumn, "Angle");
        table.addColumn(jpgCountColumn, "JpgCount");
        table.addColumn(jpgSetColumn, "JpgSetComplete");
        table.addColumn(jpgGenColumn, "JpgGenComplete");
        table.addColumn(missingJpgCountColumn, "MissingJpgCount");

        table.setRowCount(sliceList.size(), true);

        table.setRowData(0, sliceList);

        fp.add(table);

        refreshContent();

        fp.getElement().getStyle().setMargin(2, Style.Unit.EM);


        add(fp);


        timer = new Timer() {
            @Override public void run() {
                refreshContent();
            }
        };

        timer.scheduleRepeating(1000);


    }

    class SliceStatus {

        Slice slice;

        Integer jpgCount;
        Boolean jpgSet;
        Boolean jpgGen;
        Integer missingJpgCount;


        public String getViewName() {
            return slice.getViewName();
        }

        public int getAngle() {
            return slice.getAngle();
        }

        public Integer getJpgCount() {
            return jpgCount;
        }

        public Boolean getJpgSet() {
            return jpgSet;
        }

        public Boolean getJpgGen() {
            return jpgGen;
        }

        public Integer getMissingJpgCount() {
            return missingJpgCount;
        }

        public Slice getSlice() {
            return slice;
        }
    }


    private void refreshContent() {
        service.fetchJpgGenStatus(seriesId, jpgWidth, new FetchJpgGenStatusCallback() {
            @Override public void onSuccess(@Nonnull JSONArray jsSlices) {
                assert jsSlices != null;
                sliceList = rebuildList(jsSlices);
                table.setRowCount(sliceList.size());
                table.setRowData(0,sliceList);
                table.redraw();
            }
        });
    }

    private ArrayList<SliceStatus> rebuildList(JSONArray jsSlices){

        ArrayList<SliceStatus> a = new ArrayList<SliceStatus>();
        for(int i=0;i<jsSlices.size();i++){
            JSONObject jsSlice = jsSlices.get(i).isObject();
            SliceStatus st = new SliceStatus();
            String view = jsSlice.get("view").isString().stringValue();
            int angle = (int) jsSlice.get("angle").isNumber().doubleValue();
            st.slice = new Slice(view,angle);
            JSONValue jpgCount = jsSlice.get("jpgCount");
            if(jpgCount==null){
                st.jpgCount = null;
            }
            else{
                st.jpgCount = (int) jpgCount.isNumber().doubleValue();
            }

            a.add(st);


        }

        return a;
    }

    @Override public void afterClose() {
        if (timer != null) {
            timer.cancel();
        }
    }
}
