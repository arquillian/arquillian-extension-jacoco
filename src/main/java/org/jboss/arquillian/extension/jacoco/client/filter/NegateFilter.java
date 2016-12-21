package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.Filter;

/**
 * Created by hemani on 12/20/16.
 */

public class NegateFilter {

    public static boolean not(boolean or) {
        return !or;
    }
}
