package threed.core.threedModel.shared;

import com.google.common.base.Preconditions;

import java.io.Serializable;

public class BrandKey implements Serializable {

    public static final BrandKey TOYOTA = new BrandKey("toyota");
    public static final BrandKey SCION = new BrandKey("scion");
    public static final BrandKey LEXUS = new BrandKey("lexus");

    private /* final */ String key;

    private BrandKey(){}

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

}
