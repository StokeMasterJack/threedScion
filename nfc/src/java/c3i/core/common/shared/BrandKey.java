package c3i.core.common.shared;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

import java.io.Serializable;

public class BrandKey implements Serializable {

    public static final BrandKey TOYOTA = new BrandKey("toyota");
    public static final BrandKey SCION = new BrandKey("scion");
    public static final BrandKey LEXUS = new BrandKey("lexus");

    private static final ImmutableList<BrandKey> ALL = ImmutableList.of(TOYOTA, SCION, LEXUS);

    private /* final */ String key;

    private BrandKey() {
    }

    private BrandKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static BrandKey fromString(String key) {
        Preconditions.checkNotNull(key);
        if (key.equals(TOYOTA.getKey())) return TOYOTA;
        else if (key.equals(SCION.getKey())) return SCION;
        else if (key.equals(LEXUS.getKey())) return LEXUS;
        else throw new IllegalArgumentException("Invalid brand key[" + key + "]");
    }

    public static ImmutableCollection<BrandKey> getAll() {
        return ALL;
    }

    public String toJson() {
        return key;
    }

    @Override
    public String toString() {
        return key;
    }

    public boolean isScion() {
        return this == SCION;
    }

    public boolean isToyota() {
        return this == TOYOTA;
    }
}
