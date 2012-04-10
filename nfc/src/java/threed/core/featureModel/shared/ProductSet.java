package threed.core.featureModel.shared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class ProductSet  {

    protected String name;

    protected List<String> varList;
    protected String[] varArray;

    private final ProductDef productDef;

    protected final String[] foreignVarArray;

    protected int[] foreignToLocalMap;

    protected Set<Product> products = new HashSet<Product>();

    public ProductSet(String name, ProductDef productDef) {
        this.name = name;
        this.productDef = productDef;
        foreignVarArray = null;
    }

    private static String[] getStringArray(Collection<String> foreignVars) {
        List<String> list = new ArrayList<String>(foreignVars);
        String[] a = new String[foreignVars.size()];
        list.toArray(a);
        return a;
    }


    public void setName(String name) {
        this.name = name;
    }


    public String[] getVarArray() {
        return varArray;
    }

    

    public boolean add(Product p) {
        assert p.getVarCount() == varArray.length;
        return products.add(p);
    }

    public Product addForeign(Product foreignProduct) {
        boolean[] foreignBits = foreignProduct.getFieldValues();
        assert foreignToLocalMap != null;
        assert foreignBits.length == foreignVarArray.length;
        boolean[] localProduct = convertForeignToLocal(foreignBits);
        return addProduct(localProduct);
    }

    public Product addForeign(boolean[] foreignProduct) {
        assert foreignToLocalMap != null;
        assert foreignProduct.length == foreignVarArray.length;
        boolean[] localProduct = convertForeignToLocal(foreignProduct);
        return addProduct(localProduct);
    }

    private boolean[] convertForeignToLocal(boolean[] foreignProduct) {
        assert foreignProduct.length == foreignToLocalMap.length;
        boolean[] localProduct = new boolean[varArray.length];
        for (int foreignIndex = 0; foreignIndex < foreignProduct.length; foreignIndex++) {
            if (foreignProduct[foreignIndex]) {
                int localIndex = foreignToLocalMap[foreignIndex];
                if (localIndex != -1) {
                    localProduct[localIndex] = true;
                }
            }
        }
        return localProduct;
    }

    public Product addProduct(boolean[] p) {
//        Product product = new Product(p);
//        if (products.add(product)) {
//            return product;
//        } else {
            return null;
//        }
    }

//    @Override public String apply(Integer varIndex) {
//        return varArray[varIndex];
//    }





    public long getProductCount() {
        return products.size();
    }

    public int getVarCount() {
        return varArray.length;
    }

    public void print(Appendable a) {
        TreeSet<String> featureSets = new TreeSet<String>();
        for (Product product : products) {
            featureSets.add(product.toString());
        }

        String ls = "\n";
        for (String fs : featureSets) {
            try {
                a.append(fs);
                a.append(ls);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


    }


    public String getName() {
        return name;
    }

    public void printSummary() {
        System.out.println(toString());
    }

    public String toString() {return getName() + ": " + getProductCount();}

}
