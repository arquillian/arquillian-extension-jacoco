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
package org.jboss.arquillian.extension.jacoco.client;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.Asset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;

import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Instrument all Classes (or their subset if found in the User defined
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lkrejci@redhat.com">Lukas Krejci</a>
 * @version $Revision: $
 * @Deployment.
 */
public class ApplicationArchiveInstrumenter implements ApplicationArchiveProcessor
{

   private static final Logger LOGGER = Logger.getLogger(ApplicationArchiveInstrumenter.class.getName());

   @Inject
   private Instance<JacocoConfiguration> config;

   public void process(Archive<?> applicationArchive, TestClass testClass)
   {
      processArchive(applicationArchive, config.get().getClassFilter());
   }

   private void processArchive(Archive<?> archive, Filter<ArchivePath> filter)
   {

      instrument(archive, archive.getContent(filter));
      new SignatureRemover().removeSignatures(archive);

      // Process sub-archives recursively
      final Map<ArchivePath, Node> jars = archive.getContent(Filters.include(".*\\.(jar|war|rar|ear)$"));
      for (Entry<ArchivePath, Node> entry : jars.entrySet())
      {
         // Should have used genericArchive, but with GenericArchive we need
         // to specify a ArchiveFormat and that trigger this SHRINKWRAP-474
         final JavaArchive subArchive = archive.getAsType(JavaArchive.class, entry.getKey());
         if (subArchive == null)
         {
            // If Archive contains dir path suffixed with [.ear|.war|.rar|.ear] then
            // corresponding subarchive asset is null - ARQ-1931
            LOGGER.log(Level.WARNING, String.format("directory path %s contains .ear | .war | .rar | .jar", entry.getValue()));
         }
         else
         {
            processArchive(subArchive, filter);
         }
      }
   }

   private void instrument(Archive<?> archive, Map<ArchivePath, Node> classes)
   {
      for (Entry<ArchivePath, Node> entry : classes.entrySet())
      {
         final Asset original = entry.getValue().getAsset();
         archive.delete(entry.getKey());
         archive.add(new InstrumenterAsset(original), entry.getKey());
      }
   }
}
