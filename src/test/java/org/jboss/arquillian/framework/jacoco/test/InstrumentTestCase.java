/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, Red Hat Middleware LLC, and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.framework.jacoco.test;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.jacoco.core.analysis.ClassCoverage;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.instr.Analyzer;
import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jboss.arquillian.framework.jacoco.container.ArquillianRuntime;
import org.junit.Ignore;
import org.junit.Test;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.util.TraceClassVisitor;

/**
 * Verify that the instrumentation works as expected.
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class InstrumentTestCase
{
   public static Object[] object = new Object[6];

   @Test
   @Ignore
   // debug only
   public void trace() throws Exception
   {
      ClassReader reader = new ClassReader(getTargetClass(CoverageBean.class.getName()));
      reader.accept(new TraceClassVisitor(new PrintWriter(System.out)), ClassReader.EXPAND_FRAMES);
   }

   @Test
   public void shouldGetCoverageData() throws Exception
   {
      final String targetName = CoverageBean.class.getName();

      IRuntime runtime = ArquillianRuntime.getInstance();

      final Instrumenter instr = new Instrumenter(runtime);
      final byte[] instrumented = instr.instrument(getTargetClass(targetName));

      // Now we're ready to run our instrumented class and need to startup the
      // runtime first:
      runtime.startup();

      // In this tutorial we use a special class loader to directly load the
      // instrumented class definition from a byte[] instances.
      final MemoryClassLoader memoryClassLoader = new MemoryClassLoader();
      memoryClassLoader.addDefinition(targetName, instrumented);
      final Class<?> targetClass = memoryClassLoader.loadClass(targetName);

      // Here we execute our test target class
      final Object targetInstance = targetClass.newInstance();
      targetClass.getMethod("test", Boolean.class).invoke(targetInstance, true);
      targetClass.getMethod("test", Boolean.class).invoke(targetInstance, false);

      // At the end of test execution we collect execution data and shutdown
      // the runtime:
      final ExecutionDataStore executionData = new ExecutionDataStore();
      runtime.collect(executionData, null, false);
      runtime.shutdown();

      // Together with the original class definition we can calculate coverage
      // information:
      final CoverageBuilder coverageBuilder = new CoverageBuilder(executionData);
      final Analyzer analyzer = new Analyzer(coverageBuilder);
      analyzer.analyzeClass(getTargetClass(targetName));

      Assert.assertNotNull(coverageBuilder.getClasses());
      Assert.assertEquals(1, coverageBuilder.getClasses().size());

      // Let's dump some metrics and line coverage information:
      for (final ClassCoverage cc : coverageBuilder.getClasses())
      {
         Assert.assertEquals(0, cc.getInstructionCounter().getMissedCount());
         Assert.assertEquals(0, cc.getLineCounter().getMissedCount());
         Assert.assertEquals(0, cc.getMethodCounter().getMissedCount());
      }
   }

   /**
    * A class loader that loads classes from in-memory data.
    */
   public static class MemoryClassLoader extends ClassLoader
   {

      private final Map<String, byte[]> definitions = new HashMap<String, byte[]>();

      /**
       * Add a in-memory representation of a class.
       * 
       * @param name
       *            name of the class
       * @param bytes
       *            class definition
       */
      public void addDefinition(final String name, final byte[] bytes)
      {
         definitions.put(name, bytes);
      }

      @Override
      protected Class<?> loadClass(final String name, final boolean resolve) throws ClassNotFoundException
      {
         final byte[] bytes = definitions.get(name);
         if (bytes != null)
         {
            return defineClass(name, bytes, 0, bytes.length);
         }
         return super.loadClass(name, resolve);
      }

   }

   private InputStream getTargetClass(final String name)
   {
      final String resource = '/' + name.replace('.', '/') + ".class";
      return getClass().getResourceAsStream(resource);
   }
}
