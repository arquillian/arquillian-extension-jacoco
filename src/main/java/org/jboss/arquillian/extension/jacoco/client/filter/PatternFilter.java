package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.Filter;

import java.util.Collection;

/**
 * Created by hemani on 12/20/16.
 */

abstract class PatternFilter {

    public abstract <T> boolean or(Collection<Filter<T>> filterPatterns, T object);
}
