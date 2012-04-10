package threed.core.featureModel.shared;

import threed.core.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ProductDef {

    private String name;
    private Var[] fieldNames;

    public ProductDef(@Nonnull String name, @Nonnull Var[] fieldNames) {
        this.name = name;
        this.fieldNames = fieldNames;
    }

    public ProductDef(@Nonnull String name, @Nonnull List<Var> fieldNames) {
        this.name = name;
        this.fieldNames = new Var[fieldNames.size()];
        for (int i = 0; i < fieldNames.size(); i++) {
            this.fieldNames[i] = fieldNames.get(i);
        }
    }

//    public ProductDef(@Nonnull String name, @Nonnull Collection<String> fieldNames) {
//        this.name = name;
//        this.fieldNames = toStringArray(fieldNames);
//    }

    public Product createProduct(boolean[] fieldValues) {
        assert fieldNames.length == fieldValues.length : fieldNames.length + "!=" + fieldValues.length;
        return new Product(this, fieldValues);
    }

    public PartialProduct createPartialProduct(byte[] fieldValues) {
        assert fieldNames.length == fieldValues.length : fieldNames.length + "!=" + fieldValues.length;
        return new PartialProduct(this, fieldValues);
    }

    public Product create(boolean... fieldValues) {
        assert fieldNames.length == fieldValues.length;
        return new Product(this, fieldValues);
    }

    private static String[] toStringArray(Collection<String> fieldNames) {
        List<String> list = new ArrayList<String>(fieldNames);
        String[] a = new String[fieldNames.size()];
        list.toArray(a);
        return a;
    }

    private static List<Var> toStringList(Var[] fieldNames) {
        return Arrays.asList(fieldNames);
    }

    public String getName() {
        return name;
    }

    public Var[] getFieldNames() {
        return fieldNames;
    }

    public Var getFieldName(int fieldIndex) {
        return fieldNames[fieldIndex];
    }

    public int indexOf(Var fieldName) {
        for (int i = 0; i < fieldNames.length; i++) {
            if (fieldNames[i].equals(fieldName)) return i;
        }
        return -1;
    }

    public int getFieldCount() {
        return fieldNames.length;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductDef that = (ProductDef) o;

        if (!Arrays.equals(fieldNames, that.fieldNames)) return false;
        if (!name.equals(that.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + Arrays.hashCode(fieldNames);
        return result;
    }

    public void print() {
        System.out.println(toString());
    }

    @Override public String toString() {
        return name + ": " + toStringList(fieldNames);
    }
}
