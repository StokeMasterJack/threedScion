package c3i.util.shared.futures;

public interface RWValue<VT> extends RValue<VT> {

    void set(VT newValue);

}
