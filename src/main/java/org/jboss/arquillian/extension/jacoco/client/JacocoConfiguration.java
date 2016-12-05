/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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

import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Lukas Krejci
 */
public class JacocoConfiguration
{

   public static final Filter<ArchivePath> ALL_CLASSES = Filters.include(".*\\.class");

   private static final String INCLUDES_PROPERTY = "includes";
   public static final String INCLUDES_DEFAULT_VALUE = null;
   private static final String EXCLUDES_PROPERTY = "excludes";
   public static final String EXCLUDES_DEFAULT_VALUE = null;
   private static final String APPEND_ASM_LIBRARY_PROPERTY = "appendAsmLibrary";

   private static final String APPEND_ASM_LIBRARY_DEFAULT = "true";
   private static final String SEPARATOR = "\\s*;\\s*";

   private List<String> includes;
   private List<String> excludes;

   private Filter<ArchivePath> composedFilter;

   private boolean appendAsmLibrary;

   private static class ConfigMap
   {
      Map<String, String> map;

      ConfigMap(Map<String, String> map)
      {
         this.map = map;
      }

      String get(String key, String defaultValue)
      {
         String ret = map.get(key);
         return ret == null ? defaultValue : ret;
      }
   }

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

   public static JacocoConfiguration fromMap(Map<String, String> map)
   {
      JacocoConfiguration ret = new JacocoConfiguration();

      ConfigMap c = new ConfigMap(map);

      String incls = c.get(INCLUDES_PROPERTY, INCLUDES_DEFAULT_VALUE);
      ret.includes = incls == null ? Collections.<String>emptyList() : Arrays
            .asList(incls.split(SEPARATOR));

      String excls = c.get(EXCLUDES_PROPERTY, EXCLUDES_DEFAULT_VALUE);
      ret.excludes = excls == null ? Collections.<String>emptyList() : Arrays
            .asList(excls.split(SEPARATOR));

      ret.composedFilter = ret.composeFilter();

      String appendAsmLibrary = c.get(APPEND_ASM_LIBRARY_PROPERTY, APPEND_ASM_LIBRARY_DEFAULT);
      ret.appendAsmLibrary = Boolean.valueOf(appendAsmLibrary);

      return ret;
   }

   public List<String> getExcludes()
   {
      return excludes;
   }

   public List<String> getIncludes()
   {
      return includes;
   }

   public Filter<ArchivePath> getClassFilter()
   {
      return composedFilter;
   }

   public boolean isAppendAsmLibrary()
   {
      return appendAsmLibrary;
   }

   private Filter<ArchivePath> composeFilter()
   {
      List<Filter<ArchivePath>> filters = new ArrayList<Filter<ArchivePath>>();
      filters.add(ALL_CLASSES);

      for (String include : getIncludeRegexps())
      {
         filters.add(Filters.include(include));
      }

      for (String exclude : getExcludeRegexps())
      {
         filters.add(Filters.exclude(exclude));
      }

      return new AndFilter<ArchivePath>(filters);
   }

   private List<String> getIncludeRegexps()
   {
      return convertToRegexps(getIncludes());
   }

   private List<String> getExcludeRegexps()
   {
      return convertToRegexps(getExcludes());
   }

   private List<String> convertToRegexps(List<String> patterns)
   {
      if (patterns.isEmpty())
      {
         return patterns;
      }
      else
      {
         final List<String> ret = new ArrayList<String>(patterns.size());
         for (String regexp : patterns)
         {
            regexp = regexp.replace(".", "\\/").replace("*", ".*")
                  .replace('?', '.');

            ret.add(".*" + regexp + "\\.class");
         }

         return ret;
      }
   }

   public static boolean isJacocoAgentActive()
   {
      try
      {
         UUID.class.getDeclaredField("$jacocoAccess");
      } catch (Exception e)
      {
         return false;
      }
      return true;
   }
}
