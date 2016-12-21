package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.Filter;

import java.util.Collection;

/**
 * Created by hemani on 12/20/16.
 */

public class IncludeFilter extends PatternFilter {

    public <T> boolean or(Collection<Filter<T>> filterPatterns, T object) {
        boolean include = false;
        for (Filter<T> f : filterPatterns) {
            if ((f.include(object) || include)) {
                include = true;
            }
        }
        return include;
    }
}
