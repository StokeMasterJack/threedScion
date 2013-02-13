package c3i.smartClient.client.skins.bytSkin;

import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.InlineLabel;

import javax.annotation.Nonnull;
import java.util.logging.Level;
import java.util.logging.Logger;

import static smartsoft.util.shared.Strings.isEmpty;

public class MsrpPanel extends FlowPanel {

    private static final String FONT_COLOR = "white";
    private static final int FONT_SIZE_PX = 20;

    private final InlineLabel suffix = new InlineLabel("MSRP*");
    private final InlineLabel msrpValue = new InlineLabel("$??,???");

    public MsrpPanel() {
        add(new InlineHTML("&nbsp;"));
        add(msrpValue);
        add(suffix);

        Style suffixStyle = suffix.getElement().getStyle();
        suffixStyle.setFontSize((int) (FONT_SIZE_PX * .5), Style.Unit.PX);
        suffixStyle.setColor("#AAAAAA");
        suffixStyle.setMarginRight(5, Style.Unit.PX);
        suffixStyle.setMarginLeft(3, Style.Unit.PX);

        Style style = getElement().getStyle();
        style.setMargin(0, Style.Unit.PX);
        style.setPadding(0, Style.Unit.PX);
        style.setPaddingTop(7, Style.Unit.PX);

        style.setFontSize(FONT_SIZE_PX, Style.Unit.PX);
        style.setFontWeight(Style.FontWeight.BOLD);

//        style.setBackgroundColor("pink");

    }

    public void setMsrp(String msrp) {
        if (isEmpty(msrp)) {
            msrpValue.setText("$??,???");
            this.setVisible(false);
        } else if (isMsrpValid(msrp)) {
            msrpValue.setText(msrp);
            this.setVisible(true);
        } else {
            msrpValue.setText("$??,???");
            this.setVisible(false);
            log.log(Level.INFO, "Invalid MSRP[" + msrp + "]");
        }
    }

    private boolean isMsrpValid(@Nonnull String msrp) {
        assert msrp != null;
        return msrp.length() > 3;
    }

    private static Logger log = Logger.getLogger(MsrpPanel.class.getName());
}
