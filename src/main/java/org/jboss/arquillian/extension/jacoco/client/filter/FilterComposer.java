package org.jboss.arquillian.extension.jacoco.client.filter;

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;

import java.util.ArrayList;
import java.util.List;

import static org.jboss.arquillian.extension.jacoco.client.JacocoConfiguration.ALL_CLASSES;

public class FilterComposer
{

   private final List<String> includes;
   private final List<String> excludes;

   public FilterComposer(List<String> includes, List<String> excludes)
   {
      this.includes = includes;
      this.excludes = excludes;
   }

   public List<String> getExcludes()
   {
      return excludes;
   }

   public List<String> getIncludes()
   {
      return includes;
   }

   public Filter<ArchivePath> composeFilter()
   {
      List<Filter<ArchivePath>> includeFilter = new ArrayList<Filter<ArchivePath>>();
      List<Filter<ArchivePath>> excludeFilter = new ArrayList<Filter<ArchivePath>>();

      for (String include : getIncludeRegexps())
      {
         includeFilter.add(Filters.include(include));
      }

      for (String exclude : getExcludeRegexps())
      {
         excludeFilter.add(Filters.exclude(exclude));
      }

      if (includeFilter.isEmpty() && excludeFilter.isEmpty())
      {
         includeFilter.add(ALL_CLASSES);
      }

      return new CompositeFilter<ArchivePath>(includeFilter, excludeFilter);
   }

   private List<String> getIncludeRegexps()
   {
      return convertToRegexps(getIncludes());
   }

   private List<String> getExcludeRegexps()
   {
      return convertToRegexps(getExcludes());
   }

   private List<String> convertToRegexps(List<String> patterns)
   {
      if (patterns.isEmpty())
      {
         return patterns;
      } else
      {
         final List<String> ret = new ArrayList<String>(patterns.size());
         for (String regexp : patterns)
         {
            regexp = regexp.replace(".", "\\/").replace("*", ".*")
                  .replace('?', '.');

            ret.add(".*" + regexp + "\\.class");
         }

         return ret;
      }
   }
}
