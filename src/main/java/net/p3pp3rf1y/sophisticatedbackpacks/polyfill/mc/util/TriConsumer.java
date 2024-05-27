package net.p3pp3rf1y.sophisticatedbackpacks.polyfill.mc.util;

@FunctionalInterface
public interface TriConsumer<T, U, V> {

    void accept(T t, U u, V v);
}
