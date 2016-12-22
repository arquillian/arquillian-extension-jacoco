package org.jboss.arquillian.extension.jacoco.test.unit;

import org.jboss.arquillian.extension.jacoco.client.filter.FilterComposer;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class FilterTestUtils
{
   public static ArchivePath convertFromClass(final Class<?> cls)
   {
      final StringBuilder builder = new StringBuilder();
      final String pathToClass = builder.append("/").append(cls.getName().replace(".", "/")).append(".class").toString();
      return ArchivePaths.create(pathToClass);
   }

   public static Filter<ArchivePath> createComposedFilter(String includePattern, String excludePattern)
   {
      final List<String> excludes = splitPattern(excludePattern);
      final List<String> includes = splitPattern(includePattern);
      final FilterComposer filterComposer = new FilterComposer(includes, excludes);
      return filterComposer.composeFilter();
   }

   private static List<String> splitPattern(String includePattern)
   {
      List<String> includes = new ArrayList<String>();
      if (includePattern != null && includePattern.length() > 0)
      {
         includes = Arrays.asList(includePattern.split(","));
      }
      return includes;
   }
}
