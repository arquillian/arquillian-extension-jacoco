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
package org.jboss.arquillian.extension.jacoco.test.integration;

import javax.ejb.EJB;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.jacoco.test.ImplicitNoCoverageBean;
import org.jboss.arquillian.extension.jacoco.test.excluded.ExplicitNoCoverageBean;
import org.jboss.arquillian.extension.jacoco.test.included.CoverageBean;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Lukas Krejci
 */
@RunWith(Arquillian.class)
public class IncludeExcludeTestCase
{
   @Deployment
   public static JavaArchive createDeployment()
   {
      return ShrinkWrap.create(JavaArchive.class, "test.jar").addClasses(
            CoverageBean.class, ExplicitNoCoverageBean.class,
            ImplicitNoCoverageBean.class, IncludeExcludeTestCase.class);
   }

   @EJB
   private ExplicitNoCoverageBean noCoverageBean1;

   @EJB
   private ImplicitNoCoverageBean noCoverageBean2;

   @Test
   public void shouldBeAbleToGenerateSomeTestCoverage() throws Exception
   {
      Assert.assertNotNull(noCoverageBean1);
      Assert.assertNotNull(noCoverageBean2);

      noCoverageBean1.test(true);
      noCoverageBean2.test(true);
   }
}
