package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.Filter;

import java.util.Arrays;
import java.util.Collection;

public class AndFilter<T> implements Filter<T> {

    private final Collection<Filter<T>> filters;

    public AndFilter(Collection<Filter<T>> filters) {
        this.filters = filters;
    }

    public static <F> Filter<F> and(Filter<F> ... filters) {
        return new AndFilter<F>(Arrays.asList(filters));
    }

    public static <F> Filter<F> and(Collection<Filter<F>> filters) {
        return new AndFilter<F>(filters);
    }

    public boolean include(T object) {
        if (filters.isEmpty()) {
            return true;
        }

        for (Filter<T> filter : filters) {
            if (!filter.include(object)) {
                return false;
            }
        }
        return true;
    }
}
