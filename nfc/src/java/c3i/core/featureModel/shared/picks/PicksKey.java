package c3i.core.featureModel.shared.picks;

import c3i.core.featureModel.shared.boolExpr.Var;

import java.util.Set;

public final class PicksKey {

    private final Set<Var> userPicks;

    public PicksKey(Set<Var> userPicks) {
        assert userPicks != null;
        this.userPicks = userPicks;
    }

    @Override
    public boolean equals(Object o) {
        PicksKey key = (PicksKey) o;
        return userPicks.equals(key.userPicks);
    }

    @Override
    public int hashCode() {
        return userPicks.hashCode();
    }
}
