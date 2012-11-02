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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;
import org.jboss.shrinkwrap.api.Node;
import org.jboss.shrinkwrap.api.asset.ArchiveAsset;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * Instrument all Classes (or their subset if found in the User defined
 * 
 * @Deployment.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lkrejci@redhat.com">Lukas Krejci</a>
 * 
 * @version $Revision: $
 */
public class ApplicationArchiveInstrumenter implements
      ApplicationArchiveProcessor
{
   @Inject
   private Instance<JacocoConfiguration> config;

   private static class AndFilter<T> implements Filter<T>
   {
      private Collection<Filter<T>> filters;

      AndFilter(Collection<Filter<T>> filters)
      {
         this.filters = filters;
      }

      @Override
      public boolean include(T object)
      {
         for (Filter<T> f : filters)
         {
            if (!f.include(object))
            {
               return false;
            }
         }
         return true;
      }
   }

   private void processArchive(Archive<?> archive, Filter<ArchivePath> filter)
   {
      Map<ArchivePath, Node> classes = archive.getContent(filter);

      for (Entry<ArchivePath, Node> entry : classes.entrySet())
      {
         Asset original = entry.getValue().getAsset();
         archive.delete(entry.getKey());
         archive.add(new InstrumenterAsset(original), entry.getKey());
      }

      // Process sub-archives recursively
      Map<ArchivePath, Node> jars = archive.getContent();
      for (Entry<ArchivePath, Node> entry : jars.entrySet())
      {
         Asset asset = entry.getValue().getAsset();
         if (asset instanceof ArchiveAsset)
         {
            Archive<?> subArchive = ((ArchiveAsset) asset).getArchive();
            processArchive(subArchive, filter);
         }
      }
   }

   public void process(Archive<?> applicationArchive, TestClass testClass)
   {
      processArchive(applicationArchive, composeFilter());
   }

   private Filter<ArchivePath> composeFilter()
   {
      List<Filter<ArchivePath>> filters = new ArrayList<Filter<ArchivePath>>();
      filters.add(Filters.include(".*\\.class"));

      for (String include : getIncludes())
      {
         filters.add(Filters.include(include));
      }

      for (String exclude : getExcludes())
      {
         filters.add(Filters.exclude(exclude));
      }

      return new AndFilter<ArchivePath>(filters);
   }

   private List<String> getIncludes()
   {
      return convertToRegexps(config.get().getIncludes());
   }

   private List<String> getExcludes()
   {
      return convertToRegexps(config.get().getExcludes());
   }

   private List<String> convertToRegexps(List<String> patterns)
   {
      if (patterns.isEmpty())
      {
         return patterns;
      } else
      {
         ArrayList<String> ret = new ArrayList<String>(patterns.size());
         for (String regexp : patterns)
         {
            regexp = regexp.replace(".", "\\/").replace("*", ".*")
                  .replace('?', '.');

            ret.add(".*" + regexp + "\\.class");
         }

         return ret;
      }
   }
}
