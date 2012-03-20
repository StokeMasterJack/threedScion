package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

//2554, AT, V6, camry

//2554, AT, V6, XLE, camry
public class Product {

    private final ProductDef def;
    private final boolean[] fieldValues;

    public Product(@Nonnull ProductDef def, @Nonnull boolean[] fieldValues) {
        assert def != null;
        assert fieldValues != null;
        this.def = def;
        this.fieldValues = fieldValues;
    }

    public Product(@Nonnull ProductDef def, @Nonnull Map<Integer, Boolean> fieldValues) {
        assert def != null;
        assert fieldValues != null;
        this.def = def;
        this.fieldValues = null;
    }

    public ProductDef getDef() {
        return def;
    }

    public boolean[] getFieldValues() {
        return fieldValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product that = (Product) o;
        return Arrays.equals(this.fieldValues, that.fieldValues);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(fieldValues);
    }

    public String toString2() {
        return toFeatureSet().toString().replace("[", "").replace("]", "");
    }

    public SortedSet<Var> toLeafFeatureSet(FeatureModel fm) {
        SortedSet<Var> set = new TreeSet<Var>();
        for (int fieldIndex = 0; fieldIndex < fieldValues.length; fieldIndex++) {
            if (fieldValues[fieldIndex]) {
                final Var fieldName = def.getFieldName(fieldIndex);
                if (fieldName.isLeaf()) {
                    set.add(fieldName);
                }
            }
        }
        return set;
    }

    public SortedSet<Var> toFeatureSet() {
        SortedSet<Var> set = new TreeSet<Var>();
        for (int fieldIndex = 0; fieldIndex < fieldValues.length; fieldIndex++) {
            if (fieldValues[fieldIndex]) {
                final Var fieldName = def.getFieldName(fieldIndex);
                set.add(fieldName);
            }
        }
        return set;
    }

    public SortedSet<String> toPngFeatureSet() {
        SortedSet<String> set = new TreeSet<String>();
        for (int fieldIndex = 0; fieldIndex < fieldValues.length; fieldIndex++) {
            if (fieldValues[fieldIndex]) {
                final Var fieldName = def.getFieldName(fieldIndex);
                String code = fieldName.getCode();
                if (code.endsWith(".jpg")) set.add(code);
            }
        }
        return set;
    }


    public String toString() {
        int max = fieldValues.length - 1;
        StringBuilder b = new StringBuilder();
        for (int fieldIndex = 0; fieldIndex < fieldValues.length; fieldIndex++) {
            if (fieldValues[fieldIndex]) {
                final Var fieldName = def.getFieldName(fieldIndex);
                b.append(fieldName);
                b.append(',');
            }
        }
        final int last = b.length() - 1;
        if (b.charAt(last) == ',') b.deleteCharAt(last);
        return b.toString();
    }

    public String toString3() {
        if (fieldValues == null || fieldValues.length == 0) throw new IllegalArgumentException();
        int iMax = fieldValues.length - 1;
        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            b.append(fieldValues[i] ? '1' : '0');
            if (i == iMax) return b.toString();
        }
    }

    public int getVarCount() {
        return fieldValues.length;
    }


    public boolean getValue(int fieldIndex) {
        return fieldValues[fieldIndex];
    }

    public boolean getValue(Var fieldName) {
        final int fieldIndex = def.indexOf(fieldName);
        return getValue(fieldIndex);
    }
}
