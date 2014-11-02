package org.jboss.arquillian.extension.jacoco.test.verify;

import junit.framework.Assert;

import org.jboss.arquillian.extension.jacoco.test.ImplicitNoCoverageBean;
import org.jboss.arquillian.extension.jacoco.test.excluded.ExplicitNoCoverageBean;
import org.jboss.arquillian.extension.jacoco.test.included.CoverageBean;
import org.jboss.arquillian.extension.jacoco.test.included.ImportedSubArchive;
import org.jboss.arquillian.extension.jacoco.test.included.SubCoverageBean;
import org.junit.Test;

/*
 * We need to verify the content of jacoco.exec after the 'test' JVM has shutdown and flushed the 
 * data to disk. This is ran in a separate surefire execution. (see pom.xml)
 */
public class VerifyIntegrationCoverageTestCase
{

   @Test
   public void verifyIncludeExcludeTestCaseCoverageData() throws Exception
   {
      Assert.assertFalse(
            "There was coverage data collected for ExplicitNoCoverageBean class even though there shouldn't have been.",
            CoverageChecker.hasCoverageData(ExplicitNoCoverageBean.class));

      Assert.assertFalse(
            "There was coverage data collected for ImplicitNoCoverageBean class even though there shouldn't have been.",
            CoverageChecker.hasCoverageData(ImplicitNoCoverageBean.class));
   }

   @Test
   public void verifyJacocoInegrationTestCaseCoverageData() throws Exception
   {
      Assert.assertTrue(
            "There was no coverage data collected for CoverageBean class even though there should have been.",
            CoverageChecker.hasCoverageData(CoverageBean.class));
   }

   @Test
   public void verifySubArchiveTestCaseCoverageData() throws Exception
   {
      Assert.assertTrue(
            "There was no coverage data collected for SubCoverageBean class even though there should have been.",
            CoverageChecker.hasCoverageData(SubCoverageBean.class));
   }

   @Test
   public void verifyImportedSubArchiveTestCaseCoverageData() throws Exception {
      Assert.assertTrue(
              "There was no coverage data collected for ImportedSubArchive class even though there should have been.",
              CoverageChecker.hasCoverageData(ImportedSubArchive.class));
   }
}
