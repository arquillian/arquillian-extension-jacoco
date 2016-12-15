package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.Filter;

import java.util.Collection;

class AndFilter<T> implements Filter<T>
{
    private Collection<Filter<T>> filters;

    AndFilter(Collection<Filter<T>> filters)
    {
        this.filters = filters;
    }

    @Override
    public boolean include(T object)
    {
        for (Filter<T> f : filters)
        {
            if (!f.include(object))
            {
                return false;
            }
        }
        return true;
    }
}
