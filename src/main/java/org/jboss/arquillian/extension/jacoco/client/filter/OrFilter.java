package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.Filter;

import java.util.Collection;

/**
 * Created by hemani on 12/20/16.
 */

public class OrFilter {

    public static <T> boolean getFilter(Collection<Filter<T>> filterPatterns, T object) {
        if (filterPatterns.toString().contains("IncludeRegExpPaths")) {
            return new IncludeFilter().or(filterPatterns, object);
        }
        else {
            return new ExcludeFilter().or(filterPatterns, object);
        }
    }
}
