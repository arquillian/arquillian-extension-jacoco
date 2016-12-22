package org.jboss.arquillian.extension.jacoco.test.unit;

import org.jboss.arquillian.extension.jacoco.client.instrumentation.ManifestAsset;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.ArchivePaths;
import org.jboss.shrinkwrap.api.Filter;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class FilterComposerTestCase
{

   @Test
   public void should_include_any_asset_when_no_include_and_exclude_pattern_defined() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter(null, null);

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_include_asset_matching_include_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.client.*", null);

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_include_asset_matching_exact_include_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter(ManifestAsset.class.getName(), null);

      // when
      final boolean include = filter.include(FilterTestUtils.convertFromClass(ManifestAsset.class));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_exclude_asset_not_matching_include_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.client.*", null);

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/container/CoverageDataCommand.class"));

      // then
      assertThat(include).isFalse();
   }

   @Test
   public void should_include_asset_matching_one_of_include_patterns() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.client.*, org.arquillian.extension.integration.*", null);

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_include_asset_not_matching_exclude_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter(null, "org.arquillian.extension.jacoco.client.*");

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/container/CoverageDataCommand.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_exclude_asset_matching_exclude_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter(null, "org.arquillian.extension.jacoco.client.*");

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isFalse();
   }

   @Test
   public void should_exclude_asset_matching_include_and_exclude_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.client.*", "org.arquillian.extension.jacoco.*");

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isFalse();
   }

   @Test
   public void should_include_asset_matching_include_and_not_matching_exclude_pattern() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.client.*", "org.arquillian.extension.integration.*");

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();
   }

   @Test
   public void should_exclude_any_asset_when_exclude_pattern_defines_global_exclusion() throws Exception
   {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.client.*", "*");

      // when
      final boolean include = filter.include(FilterTestUtils.convertFromClass(ManifestAsset.class));

      // then
      assertThat(include).isFalse();
   }

   @Test
   public void should_include_all_assets_from_include_except_those_defined_in_exclude() throws Exception {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.client.*", "org.arquillian.extension.jacoco.container.*,org.arquillian.extension.jacoco.client.filter.*" );

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/ManifestAsset.class"));

      // then
      assertThat(include).isTrue();

   }

   @Test
   public void should_exclude_asset_matching_exclude_pattern_even_when_there_is_superset_matching_include_pattern() throws Exception {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.client.*", "org.arquillian.extension.jacoco.container.*,org.arquillian.extension.jacoco.client.filter.*" );

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/filter/FilterComposer.class"));

      // then
      assertThat(include).isFalse();

   }

   @Test
   public void should_exclude_asset_matching_one_of_the_exclude_patterns() throws Exception {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.client.*",
              "org.arquillian.extension.jacoco.container.*,org.arquillian.extension.jacoco.client.filter.*" );

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/container/CoverageDataCommand.class"));

      // then
      assertThat(include).isFalse();

   }

   @Test
   public void should_exclude_asset_in_case_of_any_conflict() throws Exception {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.*, org.arquillian.extension.jacoco.client.*, org.arquillian.extension.jacoco.container.CoverageDataCommand",
              "org.arquillian.extension.jacoco.container.*,org.arquillian.extension.jacoco.client.filter.*" );

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/container/CoverageDataCommand.class"));

      // then
      assertThat(include).isFalse();

   }

   @Test
   public void should_include_asset_if_no_conflict_with_the_exclude_patterns() throws Exception {
      // given
      final Filter<ArchivePath> filter = FilterTestUtils.createComposedFilter("org.arquillian.extension.jacoco.*, org.arquillian.extension.jacoco.client.*, org.arquillian.extension.jacoco.container.CoverageDataCommand" ,
              "org.arquillian.extension.jacoco.container.*,org.arquillian.extension.jacoco.client.filter.*" );

      // when
      final boolean include = filter.include(ArchivePaths.create("org/arquillian/extension/jacoco/client/JaCoCoArchiveAppender.class"));

      // then
      assertThat(include).isTrue();

   }

}
