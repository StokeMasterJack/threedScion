package c3i.smartClient.client.settings;

public interface EffectiveFunction<T> {
    T getEffectiveValue(Arg<T> arg);
}
