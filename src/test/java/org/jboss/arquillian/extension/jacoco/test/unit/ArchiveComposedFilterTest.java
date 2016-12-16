package org.jboss.arquillian.extension.jacoco.test.unit;

import org.jboss.arquillian.extension.jacoco.CoverageDataCommand;
import org.jboss.arquillian.extension.jacoco.client.CoverageDataReceiver;
import org.jboss.arquillian.extension.jacoco.client.InstrumentedAsset;
import org.jboss.arquillian.extension.jacoco.client.ManifestAsset;
import org.jboss.arquillian.extension.jacoco.client.filter.AndFilter;
import org.jboss.arquillian.extension.jacoco.client.filter.FilterComposer;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.jboss.arquillian.extension.jacoco.test.unit.FilterComposerTestCase.createComposedFilter;

public class ArchiveComposedFilterTest
{

   @Test
   public void should_exclude_class_from_the_archive() throws Exception
   {
      // given
      final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "simple.jar").addClasses(CoverageDataCommand.class, ManifestAsset.class);
      final Filter<ArchivePath> filter = FilterComposerTestCase.createComposedFilter(null, "org.jboss.arquillian.extension.jacoco.CoverageDataCommand");

      // when
      final Map<ArchivePath, Node> filteredContent = jar.getContent(filter);

      // then
      assertThat(filteredContent.keySet()).doesNotContain(convertFromClass(CoverageDataCommand.class));
   }

   @Test
   public void should_include_class_when_its_not_matching_exclude_pattern() throws Exception
   {
      // given
      final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "simple.jar").addClasses(CoverageDataCommand.class, ManifestAsset.class);
      final Filter<ArchivePath> filter = FilterComposerTestCase.createComposedFilter(null, "org.jboss.arquillian.extension.jacoco.client.*");

      // when
      final Map<ArchivePath, Node> filteredContent = jar.getContent(filter);

      // then
      assertThat(filteredContent.keySet()).contains(convertFromClass(CoverageDataCommand.class))
                                          .doesNotContain(convertFromClass(ManifestAsset.class));
   }

   @Test
   public void should_include_class_when_matching_include_pattern() throws Exception
   {
      // given
      final JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "simple.jar").addPackages(true, InstrumentedAsset.class.getPackage().getName());
      final Filter<ArchivePath> filter = FilterComposerTestCase.createComposedFilter("org.jboss.arquillian.extension.jacoco.client.*", "org.jboss.arquillian.extension.jacoco.client.filter.*");

      // when
      final Map<ArchivePath, Node> filteredContent = jar.getContent(filter);

      // then
      assertThat(filteredContent.keySet()).contains(convertFromClass(CoverageDataReceiver.class))
              .doesNotContain(convertFromClass(FilterComposer.class), convertFromClass(AndFilter.class));

   }

   private ArchivePath convertFromClass(Class<?> cls)
   {
      final StringBuilder builder = new StringBuilder();
      final String pathToClass = builder.append("/").append(cls.getName().replace(".", "/")).append(".class").toString();
      return ArchivePaths.create(pathToClass);
   }

}
