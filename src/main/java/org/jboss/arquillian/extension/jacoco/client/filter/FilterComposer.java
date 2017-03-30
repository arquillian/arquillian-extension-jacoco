package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.jboss.arquillian.extension.jacoco.client.configuration.JaCoCoConfiguration.ALL_CLASSES;

public class FilterComposer {

    private static final String SEPARATOR = "\\s*;\\s*|\\s*,\\s*";

    private final List<String> includes;
    private final List<String> excludes;

    private FilterComposer(List<String> includes, List<String> excludes) {
        this.includes = includes;
        this.excludes = excludes;
    }

    public static FilterComposer create(String includePatterns, String excludePatterns) {
        return new FilterComposer(splitPattern(includePatterns), splitPattern(excludePatterns));
    }

    private static List<String> splitPattern(String patterns) {
        List<String> result = new ArrayList<String>();
        if (patterns != null && patterns.length() > 0) {
            final List<String> splitPatterns = Arrays.asList(patterns.split(SEPARATOR));
            for (String splitPattern : splitPatterns) {
                result.add(splitPattern.trim());
            }
        }
        return result;
    }

    public List<String> getExcludes() {
        return excludes;
    }

    public List<String> getIncludes() {
        return includes;
    }

    public Filter<ArchivePath> composeFilter() {
        final List<Filter<ArchivePath>> includeFilter = new ArrayList<Filter<ArchivePath>>();
        final List<Filter<ArchivePath>> excludeFilter = new ArrayList<Filter<ArchivePath>>();

        for (String include : getIncludeRegexps()) {
            includeFilter.add(Filters.include(include));
        }

        for (String exclude : getExcludeRegexps()) {
            excludeFilter.add(Filters.exclude(exclude));
        }

        if (includeFilter.isEmpty() && excludeFilter.isEmpty()) {
            includeFilter.add(ALL_CLASSES);
        }

        final Filter<ArchivePath> notExcluded = AndFilter.and(excludeFilter);
        final Filter<ArchivePath> included = OrFilter.or(includeFilter);
        return AndFilter.and(notExcluded, included);
    }

    private List<String> getIncludeRegexps() {
        return convertToRegexps(getIncludes());
    }

    private List<String> getExcludeRegexps() {
        return convertToRegexps(getExcludes());
    }

    private List<String> convertToRegexps(List<String> patterns) {
        if (patterns.isEmpty()) {
            return patterns;
        } else {
            final List<String> ret = new ArrayList<String>(patterns.size());
            for (String regexp : patterns) {
                regexp = regexp.replace(".", "\\/").replace("*", ".*")
                    .replace('?', '.');

                ret.add(".*" + regexp + "\\.class");
            }

            return ret;
        }
    }
}
