package c3i.smartClient.client.settings;

import com.google.common.base.Preconditions;
import smartsoft.util.shared.Strings;

import java.util.logging.Logger;

public class Arg<T> {

    public ArgDef<T> def;

    protected T clientValue;

    public Arg(ArgDef<T> def) {
        this.def = def;
    }

    public Arg(Class<T> type) {
        this.def = new ArgDef<T>(type);
    }

    public Arg(T defaultValue) {
        this.def = new ArgDef<T>(defaultValue);
    }

    public Arg(DefaultFunction<T> defaultFunction) {
        this.def = new ArgDef<T>(defaultFunction);
    }

    public Arg(EffectiveFunction<T> effectiveFunction) {
        this.def = new ArgDef<T>(effectiveFunction);
    }

    public static <TT> Arg<TT> create(Class<TT> type) {
        return new Arg<TT>(type);
    }

//    public static <TT> Arg<TT> create(TT defaultValue) {
//        return new Arg<TT>(defaultValue);
//    }

    public static <TT> Arg<TT> create() {
        TT d = null;
        return new Arg<TT>(d);
    }

    public static <TT> Arg<TT> create(String name) {
        Arg<TT> a = create();
        return a.name(name);
    }


    public static <TT> Arg<TT> create(String name, TT defaultValue) {
        Arg<TT> a = new Arg<TT>(defaultValue);
        return a.name(name);
    }

    public static <TT> Arg<TT> create(String name, DefaultFunction<TT> defaultFunction) {
        Arg<TT> a = new Arg<TT>(defaultFunction);
        return a.name(name);
    }

    public Arg<T> type(Class<T> type) {
        def.type(type);
        return this;
    }

    public Arg<T> defaultValue(T defaultValue) {
        def.defaultValue(defaultValue);
        return this;
    }

    public Arg<T> name(String name) {
        def.name(name);
        return this;
    }

    public Arg<T> defaultFunction(DefaultFunction<T> defaultFunction) {
        def.defaultFunction(defaultFunction);
        return this;
    }

    public Arg<T> defaultValues(T... fallBackValues) {
        def.defaultFunction(new DefaultValues<T>(fallBackValues));
        return this;
    }

    public Arg<T> effectiveFunction(EffectiveFunction<T> effectiveFunction) {
        def.effectiveFunction(effectiveFunction);
        return this;
    }

    public boolean isEmpty() {
        return Strings.isEmpty(clientValue);
    }

    public static class ArgDef<T> {

        protected Class<T> type;
        protected T defaultValue;

        protected String name = "MyArg";
        protected DefaultFunction<T> defaultFunction = new SimpleDefaultFunction<T>();
        protected EffectiveFunction<T> effectiveFunction = new SimpleEffectiveFunction<T>();

        public ArgDef(Class<T> type) {
            this.type = type;
        }

        public ArgDef(T defaultValue) {
            this.defaultValue = defaultValue;
        }

        public ArgDef(DefaultFunction<T> defaultFunction) {
            this.defaultFunction = defaultFunction;
        }

        public ArgDef(EffectiveFunction<T> effectiveFunction) {
            this.effectiveFunction = effectiveFunction;
        }

        public ArgDef<T> name(String name) {
            this.name = name;
            return this;
        }

        public ArgDef<T> type(Class<T> type) {
            this.type = type;
            return this;
        }

        public ArgDef<T> defaultValue(T defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public ArgDef<T> defaultFunction(DefaultFunction<T> defaultFunction) {
            this.defaultFunction = defaultFunction;
            return this;
        }

        public ArgDef<T> effectiveFunction(EffectiveFunction<T> effectiveFunction) {
            this.effectiveFunction = effectiveFunction;
            return this;
        }

        public Arg<T> create() {
            return new Arg<T>(this);
        }


    }


    final public T getClientValue() {
        return clientValue;
    }

    final public void setClientValue(T clientValue) {
        this.clientValue = clientValue;
    }

    final public void parseClientValue(String clientValueRaw) {
        T parsed = parse(clientValueRaw);
        setClientValue(parsed);
    }

    public T parse(String clientValue) {
        throw new UnsupportedOperationException();
    }

    public T getDefaultValue() {
        return def.defaultFunction.getDefaultValue(this);
    }

    public T getEffectiveValue() {
        return def.effectiveFunction.getEffectiveValue(this);
    }

    public T get() {
        return getEffectiveValue();
    }

    public void checkNotNull(String msg) throws IllegalStateException {
        Preconditions.checkNotNull(clientValue, msg);
    }

    public void checkNotNull() throws IllegalStateException {
        Preconditions.checkNotNull(clientValue);
    }

    public void checkNotEmpty() throws IllegalStateException {
        Strings.checkNotEmpty(clientValue);
    }

    public void checkNotEmpty(String msg) throws IllegalStateException {
        Strings.checkNotEmpty(clientValue, msg);
    }

    public void log(String argName, Logger log) {
        logClientValue(argName, log);
        logEffectiveValue(argName, log);
    }

    public void logEffectiveValue(String argName, Logger log) {
        log.info(argName + ".effectiveValue[" + getEffectiveValue() + "]");
    }

    public void logClientValue(String argName, Logger log) {
        log.info(argName + ".clientValue[" + getClientValue() + "]");
    }

}
