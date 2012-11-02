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
package org.jboss.arquillian.extension.jacoco.test.integration;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.container.test.api.RunAsClient;
import org.jboss.arquillian.extension.jacoco.test.CoverageChecker;
import org.jboss.arquillian.extension.jacoco.test.included.SubCoverageBean;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * SubArchiveTestCase
 *
 * @author <a href="mailto:maschmid@redhat.com">Marek Schmidt</a>
 */
@RunWith(Arquillian.class)
public class SubArchiveTestCase
{
   @Deployment
   public static WebArchive createDeployment()
   {
      return ShrinkWrap.create(WebArchive.class, "test.war")
                  .addClass(SubArchiveTestCase.class)
                  .addAsLibrary(
                          ShrinkWrap.create(JavaArchive.class, "test.jar")
                              .addClasses(SubCoverageBean.class));
   }

   @EJB
   private SubCoverageBean bean;

   @Test
   public void shouldBeAbleToGenerateSomeTestCoverage() throws Exception
   {
      Assert.assertNotNull(bean);
      bean.test(false);
   }

   @Test
   @RunAsClient
   public void checkCoverageData() throws Exception
   {
      Assert.assertTrue(
            "There was no coverage data collected for CoverageBean class even though there should have been.",
            CoverageChecker.hasCoverageData(SubCoverageBean.class));
   }
}
