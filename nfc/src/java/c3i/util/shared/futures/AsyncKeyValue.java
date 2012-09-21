package c3i.util.shared.futures;

import c3i.util.shared.events.ChangeListener;
import c3i.util.shared.events.ExceptionHandler;
import c3i.util.shared.events.ExceptionTopic;
import com.google.common.base.Objects;
import smartsoft.util.gwt.client.Console;

import javax.annotation.Nonnull;

public class AsyncKeyValue<K, V> implements RValue<V> {

    private KeyGetter<K, V> keyGetter;

    private final Value<K> key;
    private final Value<V> value;

    private final ExceptionTopic exceptionTopic = new ExceptionTopic();


    private Loader<K, V> loader;

    public AsyncKeyValue(final AsyncFunction<K, V> asyncFunction) {
        this.key = new Value<K>(null);
        this.value = new Value<V>(null);

        this.key.addChangeListener(new ChangeListener<K>() {
            @Override
            public void onChange(final K newKey) {
                if (newKey == null) {
                    AsyncKeyValue.this.value.set(null);
                } else {
                    loader = new Loader<K, V>(newKey, asyncFunction);
                    Future<V> f = loader.ensureLoaded();
                    f.success(new OnSuccess<V>() {
                        @Override
                        public void onSuccess(@Nonnull V result) {
                            try {
                                value.set(result);
                            } catch (Throwable e) {
                                Console.error(e);
                                e.printStackTrace();
                                throw new RuntimeException(e);
                            }
                        }
                    });

                    f.failure(new OnException() {
                        @Override
                        public boolean onException(Throwable e) {
                            Console.error(e);
                            e.printStackTrace();
                            value.set(null);
                            exceptionTopic.fire(e);
                            return true;
                        }
                    });
                }
            }
        });
    }

    public AsyncKeyValue(final AsyncFunction<K, V> asyncFunction, K initialKey) {
        this(asyncFunction);
        setKey(initialKey);
    }

    public boolean isDirty() {
        Object currentKey = key.get();

        V v = value.get();

        K currentValueKey;

        if (v == null) {
            currentValueKey = null;
        } else if (v instanceof HasKey) {
            HasKey<K> hasKey = (HasKey<K>) v;
            currentValueKey = hasKey.getKey();
        } else if (keyGetter != null) {
            currentValueKey = keyGetter.getKey(v);
        } else if (Objects.equal(key, v)) {
            currentValueKey = (K) v;
        } else {
            throw new IllegalStateException();
        }

        return Objects.equal(currentKey, currentValueKey);
    }

    public void setKey(K newKey) {
        key.set(newKey);
    }

    public K getKey() {
        return key.get();
    }

    @Override
    public V get() {
        return value.get();
    }


    @Override
    public void removeAll() {

    }

    public void addKeyChangeListener(ChangeListener<K> l) {
        key.addChangeListener(l);
    }

    public void removeKeyChangeListener(ChangeListener<K> l) {
        key.removeChangeListener(l);
    }

    public void addChangeListener(ChangeListener<V> l) {
        value.addChangeListener(l);
    }

    public void removeChangeListener(ChangeListener<V> l) {
        value.removeChangeListener(l);
    }

    public void addExceptionHandler(ExceptionHandler eh) {
        exceptionTopic.add(eh);
    }

    public void removeExceptionHandler(ExceptionHandler eh) {
        exceptionTopic.remove(eh);
    }


    public void setKeyAndValue(K newKey, V newValue) {
        key.suspend();
        key.set(newKey);
        key.resume();
        value.set(newValue);
    }


    public void forceFireValueChange() {
        value.forceFireChangeEvent();
    }

    @Override
    public boolean isEmpty() {
        return value == null;
    }

    public Value<V> getValue() {
        return value;
    }

    public Value<K> getKeyValue() {
        return key;
    }

    public void setKeyGetter(KeyGetter<K, V> keyGetter) {
        this.keyGetter = keyGetter;
    }


}
