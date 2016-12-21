package org.jboss.arquillian.extension.jacoco.client.filter;

public class AndFilter {

    public static boolean and(boolean notExcluded, boolean included) {
        if (notExcluded && included) {
            return true;
        }
        return false;
    }
}
