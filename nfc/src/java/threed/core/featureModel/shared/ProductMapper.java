package threed.core.featureModel.shared;

import threed.core.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;

public class ProductMapper {

    private final ProductDef fromProductDef;
    private final ProductDef toProductDef;

    private final int fromFieldCount;
    private final int toFieldCount;

    private final int[] map;

    public ProductMapper(@Nonnull ProductDef fromProductDef, @Nonnull ProductDef toProductDef) {
        assert fromProductDef.getFieldCount() > toProductDef.getFieldCount();
        
        this.fromProductDef = fromProductDef;
        this.toProductDef = toProductDef;

        this.fromFieldCount = fromProductDef.getFieldCount();
        this.toFieldCount = toProductDef.getFieldCount();

        map = initMap();
    }



    private int[] initMap() {
        int[] m = new int[fromFieldCount];
        for (int fromIndex = 0; fromIndex < fromFieldCount; fromIndex++) {
            Var fieldName = fromProductDef.getFieldName(fromIndex);
            int localIndex = toProductDef.indexOf(fieldName);
            m[fromIndex] = localIndex;
        }
        return m;
    }

    public Product map(Product fromProduct){
        assert fromProduct.getDef().equals(this.fromProductDef);
        final boolean[] from = fromProduct.getFieldValues();


        final boolean[] to = convert(from);
        return toProductDef.createProduct(to);
    }

    private boolean[] convert(boolean[] fromProduct) {
        assert fromProduct.length == fromFieldCount;

        boolean[] toProduct = new boolean[toFieldCount];
        for (int fromIndex = 0; fromIndex < fromProduct.length; fromIndex++) {
            if (fromProduct[fromIndex]) {
                int toIndex = map[fromIndex];
                if (toIndex != -1) {
                    toProduct[toIndex] = true;
                }
            }
        }
        return toProduct;
    }

    public int mapIndex(int fromIndex){
        return map[fromIndex];
    }

}
