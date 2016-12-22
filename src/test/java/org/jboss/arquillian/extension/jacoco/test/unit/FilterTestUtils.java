package org.jboss.arquillian.extension.jacoco.test.unit;

import org.jboss.arquillian.extension.jacoco.client.filter.FilterComposer;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;

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
      final FilterComposer filterComposer = FilterComposer.create(includePattern, excludePattern);
      return filterComposer.composeFilter();
   }

}
