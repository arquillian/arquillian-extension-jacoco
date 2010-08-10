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
package org.jboss.arquillian.framework.jacoco.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import org.jacoco.core.instr.Instrumenter;
import org.jacoco.core.runtime.IRuntime;
import org.jboss.arquillian.framework.jacoco.container.ArquillianRuntime;
import org.jboss.shrinkwrap.api.asset.Asset;

/**
 * InstrumenterAsset
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class InstrumenterAsset implements Asset
{
   private Asset asset;
   
   /**
    * 
    */
   public InstrumenterAsset(Asset asset)
   {
      this.asset = asset;
   }
   
   /* (non-Javadoc)
    * @see org.jboss.shrinkwrap.api.asset.Asset#openStream()
    */
   public InputStream openStream()
   {
      try
      {
         IRuntime runtime = ArquillianRuntime.getInstance();
         Instrumenter instrumenter = new Instrumenter(runtime);
         byte[] instrumented = instrumenter.instrument(asset.openStream());
         File folder = new File("target/org/jboss/arquillian/framework/jacoco/test/");
         folder.mkdirs();
         FileOutputStream output = new FileOutputStream(new File(folder, "CoverageTestBean.class"));
         output.write(instrumented);
         output.close();
         
         return new ByteArrayInputStream(instrumented);
      }
      catch (Exception e) 
      {
         throw new RuntimeException("Could not instrument Asset " + asset, e);
      }
   }
}
