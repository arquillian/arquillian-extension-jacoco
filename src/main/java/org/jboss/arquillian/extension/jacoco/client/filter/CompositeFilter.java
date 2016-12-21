package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.Filter;

import java.util.Collection;

/**
 * Created by hemani on 12/20/16.
 */

public class CompositeFilter<T> implements Filter<T> {

    private Collection<Filter<T>> includePatterns;
    private Collection<Filter<T>> excludePatterns;

    CompositeFilter(final Collection<Filter<T>> includes, final Collection<Filter<T>> excludes) {
        this.includePatterns = includes;
        this.excludePatterns = excludes;
    }

    @Override
    public boolean include(T object) {

        boolean included = true;
        if (!includePatterns.isEmpty()) {
            included = OrFilter.getFilter(includePatterns, object);
        }

        boolean notExcluded = true;
        if (!excludePatterns.isEmpty()) {
             notExcluded = NegateFilter.not(OrFilter.getFilter(excludePatterns, object));
        }

        boolean compositeFilter = AndFilter.and(notExcluded, included);

        return compositeFilter;
    }
}
