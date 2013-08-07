package c3i.smartClient.client.settings;

public class SimpleEffectiveFunction<T> implements EffectiveFunction<T> {

    @Override
    public T getEffectiveValue(Arg<T> arg) {
        T clientValue = arg.getClientValue();
        if (clientValue != null) {
            return clientValue;
        } else {
            return arg.getDefaultValue();
        }
    }

}
