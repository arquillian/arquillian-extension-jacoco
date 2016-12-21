package org.jboss.arquillian.extension.jacoco.test.unit;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.extension.jacoco.client.JaCoCoArchiveAppender;
import org.jboss.arquillian.extension.jacoco.client.configuration.JaCoCoConfiguration;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class AuxiliaryArchiveAppenderTestCase
{

   @Test
   public void shouldPackageJacocoByDefault() throws Exception
   {
      JavaArchive archive = createArchive(JaCoCoConfiguration.fromMap(new HashMap<String, String>()));
      Assert.assertTrue(archive.contains("org/jacoco/core/JaCoCo.class"));
      Assert.assertTrue(archive.contains("org/objectweb/asm/ClassReader.class"));
   }

   @Test
   public void shouldNotPackageASMIfConfigOptionSet() throws Exception
   {
      HashMap<String, String> config = new HashMap<String, String>();
      config.put("appendAsmLibrary", "false");

      JavaArchive archive = createArchive(JaCoCoConfiguration.fromMap(config));
      Assert.assertTrue(archive.contains("org/jacoco/core/JaCoCo.class"));
      Assert.assertFalse(archive.contains("org/objectweb/asm/ClassReader.class"));
   }

   private JavaArchive createArchive(JaCoCoConfiguration configuration)
   {
      return createAppender(configuration).createAuxiliaryArchive().as(JavaArchive.class);
   }

   private JaCoCoArchiveAppender createAppender(JaCoCoConfiguration configuration)
   {
      JaCoCoArchiveAppender appender = new JaCoCoArchiveAppender();
      appender.setConfig(new DummyInstance<JaCoCoConfiguration>(configuration));
      return appender;
   }

   private static class DummyInstance<T> implements Instance<T>
   {

      private T dummy;

      public DummyInstance(T dummy)
      {
         this.dummy = dummy;
      }

      @Override
      public T get()
      {
         return dummy;
      }
   }
}