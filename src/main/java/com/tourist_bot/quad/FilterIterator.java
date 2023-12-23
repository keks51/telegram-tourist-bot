package com.tourist_bot.quad;

import java.util.Iterator;
import java.util.function.Function;


public class FilterIterator<T> implements Iterator<T> {

    private final Iterator<T> iter;
    private final Function<T, Boolean> filter;

    private T next;

    public FilterIterator(Iterator<T> iter, Function<T, Boolean> filter) {
        this.iter = iter;
        this.filter = filter;
        nextValue();
    }

    private void nextValue() {
        while (iter.hasNext()) {
            T value = iter.next();
            if (filter.apply(value)) {
                next = value;
                return;
            }
        }
        next = null;
    }

    @Override
    public boolean hasNext() {
        return next != null;
    }

    @Override
    public T next() {
        try {
            return next;
        } finally {
            nextValue();
        }

    }

}