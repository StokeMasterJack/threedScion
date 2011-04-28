package com.tms.threed.threedFramework.repo.shared;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.tms.threed.threedFramework.util.gwtUtil.client.events2.ValueChangeHandlers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RtConfig implements Serializable {

    private static final long serialVersionUID = -8175875818895006502L;

    private  transient ValueChangeHandlers<Integer> threadCountChangeHandlers;
    private  transient ValueChangeHandlers<List<JpgWidth>> jpgWidthsChangeHandlers;

    private int jpgGenThreadCount = 5;
    private ArrayList<JpgWidth> jpgWidths = new ArrayList<JpgWidth>();

    public void addJpgWidth(JpgWidth jpgWidth) {
        if (jpgWidths.contains(jpgWidth)) return;
        jpgWidths.add(jpgWidth);
        if(jpgWidthsChangeHandlers!=null)  jpgWidthsChangeHandlers.fire(null);
    }

    public List<JpgWidth> getJpgWidths() {
        return jpgWidths;
    }

    public int getJpgGenThreadCount() {
        return jpgGenThreadCount;
    }

    public void setJpgGenThreadCount(int jpgGenThreadCount) {
        if(this.jpgGenThreadCount == jpgGenThreadCount) return;
        if (jpgGenThreadCount < 1 || jpgGenThreadCount > 100) {
            throw new IllegalArgumentException();
        }
        this.jpgGenThreadCount = jpgGenThreadCount;
        if(threadCountChangeHandlers!=null)  threadCountChangeHandlers.fire(jpgGenThreadCount);
    }

    public static RtConfig createDefault() {
        RtConfig repoConfig = new RtConfig();
        repoConfig.addJpgWidth(new JpgWidth(480));
        return repoConfig;
    }

    @Override public String toString() {
        return "RtConfig{" +
                "jpgGenThreadCount=" + jpgGenThreadCount +
                ", jpgWidths=" + jpgWidths +
                '}';
    }

    public void removeJpgWith(JpgWidth jpgWidth) {
        jpgWidths.remove(jpgWidth);
        if(jpgWidthsChangeHandlers!=null) jpgWidthsChangeHandlers.fire(null);
    }

    public HandlerRegistration addThreadCountChangeHandlers(ValueChangeHandler<Integer> handler) {
        if(threadCountChangeHandlers==null) threadCountChangeHandlers = new ValueChangeHandlers<Integer>(this);
        return threadCountChangeHandlers.addHandler(ValueChangeEvent.getType(), handler);
    }

    public HandlerRegistration addJpgWidthsChangeHandlers(ValueChangeHandler<List<JpgWidth>> handler) {
        if(jpgWidthsChangeHandlers==null) jpgWidthsChangeHandlers = new ValueChangeHandlers<List<JpgWidth>>(this);
        return jpgWidthsChangeHandlers.addHandler(ValueChangeEvent.getType(), handler);
    }
}
