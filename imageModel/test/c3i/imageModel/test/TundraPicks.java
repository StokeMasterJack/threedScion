package c3i.imageModel.test;

import c3i.featureModel.shared.common.SimplePicks;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

public class TundraPicks implements SimplePicks {

    public static final Splitter SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    String stringPicks = "Base, Regular, Long, V8Big, 6AT, 4WD, 8328, 3L5, FM13, Graphite, Fabric, LayerPad1, LayerPad2, 13, TGDown, ColorPaint, iForce, TowHooks, ST, DS, TO, CJ";
    ImmutableSet<String> setPicks;

    public TundraPicks() {
        setPicks = ImmutableSet.copyOf(SPLITTER.split(stringPicks));
    }

    @Override
    public boolean isPicked(Object var) {
        return setPicks.contains(var.toString());
    }

    @Override
    public boolean isValidBuild() {
        return true;
    }

}
