package org.jboss.arquillian.extension.jacoco.test.unit;


import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.extension.jacoco.client.ApplicationArchiveInstrumenter;
import org.jboss.arquillian.extension.jacoco.client.JacocoConfiguration;
import org.jboss.arquillian.test.spi.TestClass;

import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.EnterpriseArchive;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;


public class ApplicationArchiveInstrumenterTest {

    @Test
    public void should_not_break_process_archive_if_it_contains_path_as_ear_type_and_log_warning() throws NoSuchFieldException, IllegalAccessException {
        JavaArchive javaArchive = ShrinkWrap.create(JavaArchive.class, "dri.jar").addClass(DummyInstance.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");

        EnterpriseArchive enterpriseArchive =  ShrinkWrap.create(EnterpriseArchive.class, "test.ear")
                .addAsLibraries(javaArchive)
                .addAsManifestResource(EmptyAsset.INSTANCE, "application.xml")
                .addAsManifestResource(EmptyAsset.INSTANCE, "dir.ear/pom.properties");

        ApplicationArchiveInstrumenter applicationArchiveInstrumenter = new ApplicationArchiveInstrumenter();
        setDefaultValueIfConfigIsNull(applicationArchiveInstrumenter);

        Logger logger = Logger.getLogger(ApplicationArchiveInstrumenter.class.getName());
        Formatter formatter = new SimpleFormatter();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Handler handler = new StreamHandler(out, formatter);
        logger.addHandler(handler);

        try {
            applicationArchiveInstrumenter.process(enterpriseArchive, new TestClass(DummyInstance.class));

            handler.flush();
            String logMsg = out.toString();

            assertNotNull(logMsg);
            assertTrue(logMsg.contains("WARNING: directory path /META-INF/dir.ear contains .ear | .war | .rar | .jar"));
        } finally {
            logger.removeHandler(handler);
        }
    }

    private void setDefaultValueIfConfigIsNull(ApplicationArchiveInstrumenter applicationArchiveInstrumenter) throws NoSuchFieldException, IllegalAccessException {
        Field field = applicationArchiveInstrumenter.getClass().getDeclaredField("config");
        field.setAccessible(true);
        if (field.get(applicationArchiveInstrumenter) == null) {
            field.set(applicationArchiveInstrumenter, new DummyInstance<JacocoConfiguration>(JacocoConfiguration.fromMap(new HashMap<String, String>())));
        }
    }

    private static class DummyInstance<T> implements Instance<T> {

        private T dummy;

        public DummyInstance(T dummy) {
            this.dummy = dummy;
        }

        @Override
        public T get() {
            return dummy;
        }
    }
}
