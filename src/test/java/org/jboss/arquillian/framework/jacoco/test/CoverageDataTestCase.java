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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Reader;
import java.util.Collection;

import org.jacoco.core.analysis.BundleCoverage;
import org.jacoco.core.analysis.ClassCoverage;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.PackageCoverage;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.jacoco.core.instr.Analyzer;
import org.jacoco.report.DirectorySourceFileLocator;
import org.jacoco.report.FileMultiReportOutput;
import org.jacoco.report.IReportVisitor;
import org.jacoco.report.ISourceFileLocator;
import org.jacoco.report.html.HTMLFormatter;
import org.junit.Ignore;
import org.junit.Test;

/**
 * CoverageDataTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class CoverageDataTestCase
{

   @Test
   @Ignore // not a test case, example usage
   public void generateReport() throws Exception
   {
      ExecutionDataReader reader = new ExecutionDataReader(new FileInputStream(new File("target/coverage.data")));
      ExecutionDataStore store = new ExecutionDataStore();
      SessionInfoStore sessionStore = new SessionInfoStore();

      reader.setExecutionDataVisitor(store);
      reader.setSessionInfoVisitor(sessionStore);
      reader.read();

      CoverageBuilder builder = new CoverageBuilder(store);
      Analyzer analyzer = new Analyzer(builder);
      
      for(ExecutionData testData : store.getContents())
      {
         System.out.println("analyzingClasses " + analyzer.analyzeAll(new File("target/test-classes/" + testData.getName() + ".class")));         
      }
      System.out.println("analyzingClasses " + analyzer.analyzeAll(new File("target/classes")));

      HTMLFormatter html = new HTMLFormatter();
      html.setReportOutput(new FileMultiReportOutput(new File("target/coverage/")));

      ISourceFileLocator sourceFileLocator = new MultipleSourceFileLocator(
            new DirectorySourceFileLocator(new File("src/main/java"), "UTF-8"), 
            new DirectorySourceFileLocator(new File("src/test/java"), "UTF-8"));
      
      BundleCoverage bundle = builder.getBundle("Arquillian Run");
      
      IReportVisitor visitor = html.createReportVisitor(bundle, sessionStore.getInfos(), store.getContents());
      visitBundle(
            visitor, 
            bundle, 
            sourceFileLocator);
      visitor.visitEnd(sourceFileLocator);
   }
   
   private class MultipleSourceFileLocator implements ISourceFileLocator 
   {
      private ISourceFileLocator[] sourceFileLocators;
      
      public MultipleSourceFileLocator(ISourceFileLocator... fileLocators)
      {
         this.sourceFileLocators = fileLocators;
      }
      
      /* (non-Javadoc)
       * @see org.jacoco.report.ISourceFileLocator#getSourceFile(java.lang.String, java.lang.String)
       */
      public Reader getSourceFile(String packageName, String fileName) throws IOException
      {
         for(ISourceFileLocator locator : sourceFileLocators)
         {
            Reader reader = locator.getSourceFile(packageName, fileName);
            if(reader != null)
            {
               return reader;
            }
         }
         return null;
      }
   }

   private static void visitBundle(final IReportVisitor visitor, final BundleCoverage bundledata,
         final ISourceFileLocator sourceFileLocator) throws IOException
   {
      for (final PackageCoverage p : bundledata.getPackages())
      {
         visitPackage(visitor.visitChild(p), p, sourceFileLocator);
      }
   }

   private static void visitPackage(final IReportVisitor visitor, final PackageCoverage packagedata,
         final ISourceFileLocator sourceFileLocator) throws IOException
   {
      visitLeafs(visitor, packagedata.getSourceFiles(), sourceFileLocator);
      for (final ClassCoverage c : packagedata.getClasses())
      {
         visitClass(visitor.visitChild(c), c, sourceFileLocator);
      }
      visitor.visitEnd(sourceFileLocator);
   }

   private static void visitClass(final IReportVisitor visitor, final ClassCoverage classdata,
         final ISourceFileLocator sourceFileLocator) throws IOException
   {
      visitLeafs(visitor, classdata.getMethods(), sourceFileLocator);
      visitor.visitEnd(sourceFileLocator);
   }

   private static void visitLeafs(final IReportVisitor visitor, final Collection<? extends ICoverageNode> leafs,
         final ISourceFileLocator sourceFileLocator) throws IOException
   {
      for (final ICoverageNode l : leafs)
      {
         // Ignore Arquillian Client side methods
         if("createDeployment".equals(l.getName()))
         {
            continue;
         }
// Attempt to auto set line coverage for Arquillian Client side Methods         
//         if (l instanceof MethodCoverage)
//         {
//            MethodCoverage methodCoverage = (MethodCoverage) l;
//            if("createDeployment".equals(l.getName())) 
//            {
//               LinesImpl lineCoverage = (LinesImpl)methodCoverage.getLineCounter();
//               ILines lines = methodCoverage.getLines();
//               int[] autoCoveredLines = new int[(lines.getLastLine() - lines.getFirstLine()) + 1];
//               int count = 0;
//               for(int i = lines.getFirstLine(); i <= lines.getLastLine(); i++)
//               {
//                  autoCoveredLines[count++] = i;
//               }
//               
//               lineCoverage.increment(autoCoveredLines, true);
//            }
//         }
         final IReportVisitor child = visitor.visitChild(l);
         child.visitEnd(sourceFileLocator);
      }
   }
}
