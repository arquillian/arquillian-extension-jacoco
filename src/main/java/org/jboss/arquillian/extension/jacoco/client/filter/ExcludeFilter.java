package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.Filter;

import java.util.Collection;

/**
 * Created by hemani on 12/20/16.
 */

public class ExcludeFilter extends PatternFilter {

    public <T> boolean or(Collection<Filter<T>> filterPatterns, T object) {
        for (Filter<T> f : filterPatterns) {
            if (!f.include(object)) {
                return true;
            }
        }
        return false;
    }
}
