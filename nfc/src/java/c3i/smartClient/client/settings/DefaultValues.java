package c3i.smartClient.client.settings;

public class DefaultValues<T> implements DefaultFunction<T> {

    private final T[] fallBackValues;

    public DefaultValues(T... fallBackValues) {
        this.fallBackValues = fallBackValues;
    }

    @Override
    public T getDefaultValue(Arg<T> arg) {

        for (int i = 0; i < fallBackValues.length; i++) {
            T fallBackValue = fallBackValues[i];
            if (fallBackValue != null) {
                return fallBackValue;
            }
        }
        return null;
    }

}
