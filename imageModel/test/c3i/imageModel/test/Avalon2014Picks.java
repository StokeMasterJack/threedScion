package c3i.imageModel.test;

import c3i.featureModel.shared.boolExpr.Var;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;

public class Avalon2014Picks implements c3i.imageModel.shared.SimplePicks {

    public static final Splitter SPLITTER = Splitter.on(',').trimResults().omitEmptyStrings();

    String stringPicks = "XLE, V6, 6AT, 3544, 218, LD03, Almond, Leather, AudioSTRD";
    ImmutableSet<String> setPicks;

    public Avalon2014Picks() {
        setPicks = ImmutableSet.copyOf(SPLITTER.split(stringPicks));
    }

    @Override
    public boolean isPicked(Var var) {
        return setPicks.contains(var.toString());
    }


}
