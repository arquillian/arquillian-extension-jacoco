package org.jboss.arquillian.extension.jacoco.test.unit;

import org.jboss.arquillian.extension.jacoco.client.ArchiveInstrumenter;
import org.jboss.arquillian.extension.jacoco.client.FilterComposer;
import org.jboss.arquillian.extension.jacoco.client.JacocoConfiguration;
import org.jboss.arquillian.extension.jacoco.client.SignatureRemover;
import org.jboss.shrinkwrap.api.*;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.mock;

/**
 * Created by hemani on 12/13/16.
 */
public class FilterComposerTestCase {

    private SignatureRemover signatureRemover;

    private ArchiveInstrumenter instrumenter;

    @Before
    public void wire_instrumenter() {
        this.signatureRemover = mock(SignatureRemover.class);
        this.instrumenter = new ArchiveInstrumenter(signatureRemover);
    }


    @Test
    public void should_retrieve_contents_of_filter() throws Exception {

        // given

        final JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "dri.jar").
               addClass(FilterComposer.class).addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        ArchivePath targetPath = ArchivePaths.create("org.jboss.arquillian.extension.jacoco.test.included.*");


        List<String> include = new ArrayList<String>();
        include.add("org.jboss.arquillian.extension.jacoco.test.included.*");

        List<String> exclude = new ArrayList<String>();
        exclude.add("org.jboss.arquillian.extension.jacoco.test.integration.*");

        FilterComposer composer = new FilterComposer(include, exclude);
        Filter<ArchivePath> archivePathFilter = composer.composeFilter();

        //Assert.assertTrue("should be located in " + targetPath, javaArchive.contains(targetPath));

        /*
        Map<String, String> arq_config = new LinkedHashMap();
        arq_config.put("includes", "org.jboss.arquillian.extension.jacoco.test.included.*");
        arq_config.put("excludes", "org.jboss.arquillian.extension.jacoco.test.integration.*;org.jboss.arquillian.extension.jacoco.test.excluded.*");

        JacocoConfiguration config = JacocoConfiguration.fromMap(arq_config);

        instrumenter.processArchive(javaArchive, config.getClassFilter());

        // when
        final List<Asset> classAssets = extractClassAssets(javaArchive);
        System.out.println(classAssets);
        assertThat(classAssets).hasSize(2).hasOnlyElementsOfType(FilterComposer.class);
        */

    }

    private List<Asset> extractClassAssets(JavaArchive javaArchive) {
        final List<Asset> classAssets = new ArrayList<Asset>();
        for (Map.Entry<ArchivePath, Node> entry : javaArchive.getContent(JacocoConfiguration.ALL_CLASSES).entrySet()) {
            classAssets.add(entry.getValue().getAsset());
        }
        return classAssets;
    }

}
