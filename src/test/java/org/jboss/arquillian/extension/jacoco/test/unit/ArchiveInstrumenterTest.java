package org.jboss.arquillian.extension.jacoco.test.unit;

import org.jboss.arquillian.extension.jacoco.client.instrumentation.ArchiveInstrumenter;
import org.jboss.arquillian.extension.jacoco.client.instrumentation.InstrumentedAsset;
import org.jboss.arquillian.extension.jacoco.client.configuration.JaCoCoConfiguration;
import org.jboss.arquillian.extension.jacoco.client.instrumentation.ManifestAsset;
import org.jboss.arquillian.extension.jacoco.client.instrumentation.SignatureRemover;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ArchiveInstrumenterTest
{

   private SignatureRemover signatureRemover;

   private ArchiveInstrumenter instrumenter;

   @Before
   public void wire_instrumenter()
   {
      this.signatureRemover = mock(SignatureRemover.class);
      this.instrumenter = new ArchiveInstrumenter(signatureRemover);
   }

   @Test
   public void should_remove_signatures_from_all_archives_ignoring_directories_named_with_java_archive_suffix() throws Exception
   {
      // given
      final JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "dri.jar").addClass(ArchiveInstrumenterTest.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

      final EnterpriseArchive enterpriseArchive = ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
            .addAsLibraries(javaArchive)
            .addAsManifestResource(new StringAsset("<test></test>"), "application.xml")
            // [ARQ-1931] Having directory with `.ear` suffix resulted in NPE
            .addAsManifestResource(new StringAsset("sample content"), "dir.ear/pom.properties");

      // when
      instrumenter.processArchive(enterpriseArchive, JaCoCoConfiguration.ALL_CLASSES);

      // then
      verify(signatureRemover, times(2)).removeSignatures(any(Archive.class));
   }

   @Test
   public void should_instrument_all_classes_in_the_archive() throws Exception
   {
      // given
      final JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "dri.jar")
            .addClasses(ArchiveInstrumenterTest.class, ManifestAsset.class, ArchiveInstrumenter.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

      // when
      instrumenter.processArchive(javaArchive, JaCoCoConfiguration.ALL_CLASSES);
      final List<Asset> classAssets = extractClassAssets(javaArchive);

      // then
      assertThat(classAssets).hasSize(3).hasOnlyElementsOfType(InstrumentedAsset.class);
   }

   private List<Asset> extractClassAssets(JavaArchive javaArchive)
   {
      final List<Asset> classAssets = new ArrayList<Asset>();
      for (Map.Entry<ArchivePath, Node> entry : javaArchive.getContent(JaCoCoConfiguration.ALL_CLASSES).entrySet())
      {
         classAssets.add(entry.getValue().getAsset());
      }
      return classAssets;
   }


}

