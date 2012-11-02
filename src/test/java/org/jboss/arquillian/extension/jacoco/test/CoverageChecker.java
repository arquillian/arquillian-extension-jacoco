/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011, Red Hat Middleware LLC, and individual contributors
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
package org.jboss.arquillian.extension.jacoco.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfo;

/**
 * @author Lukas Krejci
 */
public final class CoverageChecker
{
   public static final File DEFAULT_OUTPUT_FILE = new File("target" + File.separator + "jacoco.exec");

   private CoverageChecker() { }

   public static boolean hasCoverageData(Class<?>... classes) throws IOException
   {
      return hasCoverageData(DEFAULT_OUTPUT_FILE, Arrays.asList(classes));
   }

   public static boolean hasCoverageData(File jacocoDataFile, Class<?>... classes) throws IOException
   {
      return hasCoverageData(jacocoDataFile, Arrays.asList(classes));
   }

   public static boolean hasCoverageData(File jacocoDataFile, List<Class<?>> classes) throws IOException
   {
      FileInputStream fin = new FileInputStream(jacocoDataFile);

      final List<String> visitedClasses = new ArrayList<String>();

      try
      {
         ExecutionDataReader reader = new ExecutionDataReader(fin);

         reader.setExecutionDataVisitor(new IExecutionDataVisitor()
         {

            @Override
            public void visitClassExecution(ExecutionData data)
            {
               String binaryName = convertToBinaryName(data.getName());
               for(boolean hit : data.getData())
               {
                  if(hit) // make sure the Bean has recorded a hit
                  {
                     visitedClasses.add(binaryName);
                     break;
                  }
               }
            }
         });

         reader.setSessionInfoVisitor(new ISessionInfoVisitor()
         {
            @Override
            public void visitSessionInfo(SessionInfo info)
            {
            }
         });

         reader.read();

         for (Class<?> cls : classes)
         {
            if (!visitedClasses.contains(cls.getName()))
            {
               return false;
            }
         }

         return true;
      } finally
      {
         fin.close();
      }
   }

   private static String convertToBinaryName(String vmName)
   {
      return vmName.replace('/', '.');
   }
}
