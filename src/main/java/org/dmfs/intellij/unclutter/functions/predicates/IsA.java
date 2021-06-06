package org.dmfs.intellij.unclutter.functions.predicates;

import java.util.function.Predicate;


public final class IsA<T, V extends T> implements Predicate<T>
{
    private final Class<V> clazz;
    private final Predicate<? super V> delegate;


    public IsA(Class<V> clazz, Predicate<? super V> delegate)
    {
        this.clazz = clazz;
        this.delegate = delegate;
    }


    @Override
    public boolean test(T t)
    {
        return clazz.isInstance(t) && delegate.test((V) t);
    }
}
