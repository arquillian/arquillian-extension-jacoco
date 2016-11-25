package org.jboss.arquillian.extension.jacoco.test.unit;


import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.extension.jacoco.client.ApplicationArchiveInstrumenter;
import org.jboss.arquillian.extension.jacoco.client.JacocoConfiguration;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.HashMap;

import static org.mockito.Mockito.*;

@RunWith(org.mockito.junit.MockitoJUnitRunner.class)
public class ApplicationArchiveInstrumenterTest {

    @Mock
    private Instance<JacocoConfiguration> config;

    @Test
    public void should_not_break_process_archive_if_it_contains_path_as_ear_type(){

        when(config.get()).thenReturn(JacocoConfiguration.fromMap(new HashMap<String, String>()));
        WebArchive webArchive = ShrinkWrap.create(WebArchive.class, "web.war").addClass(DummyClass.class);
        EnterpriseArchive enterpriseArchive =  ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
                .addAsLibraries(webArchive)
                .addAsDirectories("subarchive.ear");

        ApplicationArchiveInstrumenter applicationArchiveInstrumenter = new ApplicationArchiveInstrumenter();
        applicationArchiveInstrumenter.setConfig(config);
        applicationArchiveInstrumenter.process(enterpriseArchive, new TestClass(DummyClass.class));

        verify(config, atMost(1)).get();
    }

    private class DummyClass {

    }
}
