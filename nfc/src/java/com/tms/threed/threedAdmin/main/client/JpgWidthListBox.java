package com.tms.threed.threedAdmin.main.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.ListBox;
import com.tms.threed.threedCore.threedModel.shared.JpgWidth;
import com.tms.threed.repo.shared.RtConfig;
import com.tms.threed.util.gwtUtil.client.Console;
import com.tms.threed.util.gwtUtil.client.events2.ValueChangeHandlers;

public class JpgWidthListBox extends ListBox {

    private final ValueChangeHandlers<JpgWidth> jpgWidthChangeHandlers = new ValueChangeHandlers<JpgWidth>(this);

    private final RtConfig rtConfig;

    public JpgWidthListBox(final RtConfig rtConfig) {
        this.rtConfig = rtConfig;

        addChangeHandler(new ChangeHandler() {
            @Override public void onChange(ChangeEvent event) {
                jpgWidthChangeHandlers.fire(getSelectedJpgWidth());
            }
        });

        rtConfig.addJpgWidthsChangeHandlers(new ValueChangeHandler() {
            @Override public void onValueChange(ValueChangeEvent e) {
                refresh();
            }
        });

        refresh();


    }


    private void refresh() {
        clear();
        addItem("Standard", "sStd");
        for (JpgWidth width : rtConfig.getJpgWidths()) {
            Console.log("width = " + width);
            if (width.isStandard()) continue;
            addItem(width.intValue() + "");
        }
        if (getItemCount() > 0) {
            setSelectedIndex(0);
        }
    }


    public JpgWidth getSelectedJpgWidth() {
        int i = getSelectedIndex();
        if (i == -1) {
            throw new IllegalStateException("No jpgWidth selected");
        }
        String itemText = getItemText(i);
        return new JpgWidth(itemText);
    }

    public HandlerRegistration addJpgWidthChangeHandler(ValueChangeHandler<JpgWidth> handler) {
        return jpgWidthChangeHandlers.addHandler(ValueChangeEvent.getType(), handler);
    }


}
