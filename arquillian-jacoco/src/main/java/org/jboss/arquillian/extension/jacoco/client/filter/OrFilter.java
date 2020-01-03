package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.Filter;

import java.util.Arrays;
import java.util.Collection;

public class OrFilter<T> implements Filter<T> {

    private final Collection<Filter<T>> filters;

    public OrFilter(Collection<Filter<T>> filters) {
        this.filters = filters;
    }

    public static <F> Filter<F> or(Filter<F>... filters) {
        return new OrFilter<F>(Arrays.asList(filters));
    }

    public static <F> Filter<F> or(Collection<Filter<F>> filters) {
        return new OrFilter<F>(filters);
    }

    public boolean include(T object) {
        if (filters.isEmpty()) {
            return true;
        }

        for (Filter<T> filter : filters) {
            if (filter.include(object)) {
                return true;
            }
        }
        return false;
    }
}
