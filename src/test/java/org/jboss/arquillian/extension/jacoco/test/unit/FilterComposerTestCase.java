package org.jboss.arquillian.extension.jacoco.test.unit;

import org.jboss.arquillian.extension.jacoco.client.filter.FilterComposer;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.junit.Test;

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
      final List<String> includes = Arrays.asList("org.arquillian.extension.jacoco.client.*");
      final List<String> excludes = Collections.emptyList();
      final FilterComposer filterComposer = new FilterComposer(includes, excludes);
      final Filter<ArchivePath> filter = filterComposer.composeFilter();

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_include_asset_matching_exact_include_pattern() throws Exception
   {
      // given
      final List<String> includes = Arrays.asList("org.arquillian.extension.jacoco.client.ManifestAsset.class");
      final List<String> excludes = Collections.emptyList();
      final FilterComposer filterComposer = new FilterComposer(includes, excludes);
      final Filter<ArchivePath> filter = filterComposer.composeFilter();

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_exclude_asset_not_matching_include_pattern() throws Exception
   {
      // given
      final List<String> includes = Arrays.asList("org.arquillian.extension.jacoco.client.*");
      final List<String> excludes = Collections.emptyList();
      final FilterComposer filterComposer = new FilterComposer(includes, excludes);
      final Filter<ArchivePath> filter = filterComposer.composeFilter();

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/container/CoverageDataCommand.class"));

      // then
      assertThat(include).isFalse();
   }

   @Test
   public void should_include_asset_not_matching_exclude_pattern() throws Exception
   {
      // given
      final List<String> excludes = Arrays.asList("org.arquillian.extension.jacoco.client.*");
      final List<String> includes = Collections.emptyList();
      final FilterComposer filterComposer = new FilterComposer(includes, excludes);
      final Filter<ArchivePath> filter = filterComposer.composeFilter();

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/container/CoverageDataCommand.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_exclude_asset_matching_exclude_pattern() throws Exception
   {
      // given
      final List<String> excludes = Arrays.asList("org.arquillian.extension.jacoco.client.*");
      final List<String> includes = Collections.emptyList();
      final FilterComposer filterComposer = new FilterComposer(includes, excludes);
      final Filter<ArchivePath> filter = filterComposer.composeFilter();

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isFalse();
   }

   @Test
   public void should_exclude_asset_matching_include_and_exclude_pattern() throws Exception
   {
      // given
      final List<String> excludes = Arrays.asList("org.arquillian.extension.jacoco.*");
      final List<String> includes = Arrays.asList("org.arquillian.extension.jacoco.client.*");
      final FilterComposer filterComposer = new FilterComposer(includes, excludes);
      final Filter<ArchivePath> filter = filterComposer.composeFilter();

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isFalse();
   }

   @Test
   public void should_include_asset_matching_include_and_not_matching_exclude_pattern() throws Exception
   {
      // given
      final List<String> excludes = Arrays.asList("org.arquillian.extension.integration.*");
      final List<String> includes = Arrays.asList("org.arquillian.extension.jacoco.client.*");
      final FilterComposer filterComposer = new FilterComposer(includes, excludes);
      final Filter<ArchivePath> filter = filterComposer.composeFilter();

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

}
