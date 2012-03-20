package com.tms.threed.threedCore.featureModel.shared;

import com.tms.threed.threedCore.featureModel.shared.boolExpr.Var;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.SortedSet;
import java.util.TreeSet;

public class PartialProduct {

    private final ProductDef def;
    private final byte[] fieldValues;


    public PartialProduct(@Nonnull ProductDef def, @Nonnull byte[] fieldValues) {
        assert def != null;
        assert fieldValues != null;
        this.def = def;
        this.fieldValues = fieldValues;
    }

    public byte[] getFieldValues() {
        return fieldValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PartialProduct that = (PartialProduct) o;
        return Arrays.equals(this.fieldValues, that.fieldValues);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(fieldValues);
    }

    public SortedSet<Var> getPicks() {
        return getFeatureSet(Bit.TRUE);
    }

    public SortedSet<Var> getNonPicks() {
        return getFeatureSet(Bit.FALSE);
    }

    public SortedSet<Var> getDontCares() {
        return getFeatureSet(Bit.OPEN);
    }

    public SortedSet<Var> getFeatureSet(Bit filter) {
        SortedSet<Var> set = new TreeSet<Var>();
        for (int fieldIndex = 0; fieldIndex < fieldValues.length; fieldIndex++) {
            if (filter.matches(fieldValues[fieldIndex])) {
                final Var fieldName = def.getFieldName(fieldIndex);
                set.add(fieldName);
            }
        }
        return set;
    }

    public String toString(Bit filter) {
        return getFeatureSet(filter).toString();
    }

    public String toString() {
        return getFeatureSet(Bit.TRUE).toString();
    }

//    public SortedSet<String> getFeatureSet(Bit filter) {
//        SortedSet<String> set = new TreeSet<String>();
//        for (int i = 0; i < fieldValues.length; i++) {
//            if (fieldValues[i] == filter.value()) {
//                String code = intToVarMapper.indexToVar(i).getCode();
//                set.add(code);
//            }
//        }
//        return set;
//    }


    public String toBitString() {
        if (fieldValues == null || fieldValues.length == 0) throw new IllegalArgumentException();
        int iMax = fieldValues.length - 1;
        StringBuilder b = new StringBuilder();
        for (int i = 0; ; i++) {
            Bit bit = Bit.fromByte(fieldValues[i]);
            b.append(bit.toChar());
            if (i == iMax) return b.toString();
        }
    }
//
//    public Collection<Integer> getDontCares() {
//        ArrayList a = new ArrayList(fieldValues.length);
//        for (int i = 0; i < fieldValues.length; i++) {
//            byte bit = fieldValues[i];
//            if (isDontCare(bit)) a.add(i);
//        }
//        return a;
//    }

    public int getDontCareCount() {
        int c = 0;
        for (byte bit : fieldValues) {
            if (isDontCare(bit)) c++;
        }
        return c;
    }

    public boolean isDontCare(byte triState) {
        return Bit.isUnassigned(triState);
    }
}