package c3i.smartClient.client.settings;

public class SimpleDefaultFunction<T> implements DefaultFunction<T> {

    @Override
    public T getDefaultValue(Arg<T> arg) {
        return arg.def.defaultValue;
    }
}
