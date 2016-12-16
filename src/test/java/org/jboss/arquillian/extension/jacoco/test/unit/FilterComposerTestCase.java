package org.jboss.arquillian.extension.jacoco.test.unit;

import org.jboss.arquillian.extension.jacoco.client.filter.FilterComposer;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.junit.Ignore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterComposerTestCase
{

   @Test
   public void should_include_any_asset_when_no_include_and_exclude_pattern_defined() throws Exception
   {
      // given
      final FilterComposer filterComposer = new FilterComposer(Collections.<String>emptyList(), Collections.<String>emptyList());
      final Filter<ArchivePath> filter = filterComposer.composeFilter();

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_include_asset_matching_include_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = createComposedFilter("org.arquillian.extension.jacoco.client.*", null);

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_include_asset_matching_exact_include_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = createComposedFilter("org.arquillian.extension.jacoco.client.ManifestAsset", null);

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_exclude_asset_not_matching_include_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = createComposedFilter("org.arquillian.extension.jacoco.client.*", null);

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/container/CoverageDataCommand.class"));

      // then
      assertThat(include).isFalse();
   }

   @Test
   public void should_include_asset_not_matching_exclude_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = createComposedFilter(null, "org.arquillian.extension.jacoco.client.*");

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/container/CoverageDataCommand.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_exclude_asset_matching_exclude_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = createComposedFilter(null, "org.arquillian.extension.jacoco.client.*");

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isFalse();
   }

   @Test
   public void should_exclude_asset_matching_include_and_exclude_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = createComposedFilter("org.arquillian.extension.jacoco.client.*", "org.arquillian.extension.jacoco.*");

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isFalse();
   }

   @Test
   public void should_include_asset_matching_include_and_not_matching_exclude_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = createComposedFilter("org.arquillian.extension.jacoco.client.*", "org.arquillian.extension.integration.*");

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   @Ignore("Please implement me :)")
   public void should_include_asset_matching_one_of_include_patterns() throws Exception
   {
      // given
      // TODO concrete class + pattern to include

      // when

      // then
   }

   @Test
   @Ignore("Please implement me :)")
   public void should_exclude_any_asset_when_exclude_pattern_defines_global_exclusion() throws Exception
   {
      // given

      // when

      // then
   }

   // three more tests:
   // exclude filter.*,container.*
   // include client.*
   // test for:
   // - class from client should be included
   // - class from filter should be excluded
   // - class from container should be excluded
   // 1. implement 3 test cases (ok to just have this)
   // (Homework :)) 2. see how we can use https://github.com/Pragmatists/JUnitParams to make it one parameterized test
   // !!! important note - there is a bug in junit params which does not let you run tests in parallel
   // option 1: use @NonThreadSafe on the class -> https://github.com/Pragmatists/JUnitParams/issues/34
   // option 2: junit parameterized runner

   public static Filter<ArchivePath> createComposedFilter(String includePattern, String excludePattern)
   {
      List<String> excludes = splitPattern(excludePattern);
      List<String> includes = splitPattern(includePattern);
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
