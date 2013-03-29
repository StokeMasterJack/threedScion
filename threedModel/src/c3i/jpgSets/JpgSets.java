package c3i.jpgSets;

import c3i.imageModel.shared.Slice;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class JpgSets {

    private final ImmutableMap<Slice, JpgSet> map;

    public JpgSets(Map<Slice, JpgSet> map) {
        if (map instanceof ImmutableMap) {
            this.map = (ImmutableMap<Slice, JpgSet>) map;
        } else {
            this.map = ImmutableMap.copyOf(map);
        }
    }

    public ImmutableMap<Slice, JpgSet> getMap() {
        return map;
    }

    public int getJpgCount() {
        int c = 0;
        for (JpgSet jpgSet : map.values()) {
            c += jpgSet.getJpgCount();
        }
        return c;
    }
}
