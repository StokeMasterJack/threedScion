package c3i.smartClient.client.settings;

public interface DefaultFunction<T> {
    T getDefaultValue(Arg<T> arg);
}
