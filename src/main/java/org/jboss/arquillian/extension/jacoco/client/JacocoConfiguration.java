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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Lukas Krejci
 */
public class JacocoConfiguration
{

   private static final String DEST_FILE_PROPERTY = "destFile";
   public static final String DEST_FILE_DEFAULT_VALUE = "jacoco.exec";
   private static final String INCLUDES_PROPERTY = "includes";
   public static final String INCLUDES_DEFAULT_VALUE = null;
   private static final String EXCLUDES_PROPERTY = "excludes";
   public static final String EXCLUDES_DEFAULT_VALUE = null;

   private static final String SEPARATOR = "\\s*;\\s*";

   private String destinationFileName;
   private List<String> includes;
   private List<String> excludes;

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

   public static JacocoConfiguration fromMap(Map<String, String> map)
   {
      JacocoConfiguration ret = new JacocoConfiguration();

      ConfigMap c = new ConfigMap(map);

      ret.destinationFileName = c.get(DEST_FILE_PROPERTY,
            DEST_FILE_DEFAULT_VALUE);

      String incls = c.get(INCLUDES_PROPERTY, INCLUDES_DEFAULT_VALUE);
      ret.includes = incls == null ? Collections.<String> emptyList() : Arrays
            .asList(incls.split(SEPARATOR));

      String excls = c.get(EXCLUDES_PROPERTY, EXCLUDES_DEFAULT_VALUE);
      ret.excludes = excls == null ? Collections.<String> emptyList() : Arrays
            .asList(excls.split(SEPARATOR));

      return ret;
   }

   public String getDestinationFileName()
   {
      return destinationFileName;
   }

   public List<String> getExcludes()
   {
      return excludes;
   }

   public List<String> getIncludes()
   {
      return includes;
   }
}
