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
package org.jboss.arquillian.extension.jacoco.test.included;

import javax.ejb.Stateless;


/**
 * This bean is explicitly mentioned in the "includes" of jacoco configuration
 * in the the arquillian.xml and therefore is instrumented with code coverage.
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
@Stateless
public class CoverageBean 
{
   public void test(Boolean value) 
   {
      String test = "test";
      if(value)
      {
         if(test.length() == 4)
         {
            long start = System.currentTimeMillis();
            test = String.valueOf(start);
         }
      } 
      else
      {
         long start = System.currentTimeMillis();
         test = String.valueOf(start);
      }
   }
}
