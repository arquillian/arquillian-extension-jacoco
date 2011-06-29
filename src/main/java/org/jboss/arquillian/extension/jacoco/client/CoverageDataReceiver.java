package org.jboss.arquillian.extension.jacoco.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.extension.jacoco.container.CoverageDataCommand;

public class CoverageDataReceiver
{

   public void storeCoverageData(@Observes CoverageDataCommand coverageDataCommandEvent)
   {
      FileOutputStream fout = null;
      try
      {
         File targetDirectory = new File("target/jacocoext");
         targetDirectory.mkdirs();
         
         fout = new FileOutputStream(new File(targetDirectory, "dummy.data"));
         fout.write(coverageDataCommandEvent.getCoverageDate());
      }
      catch (FileNotFoundException e)
      {
         e.printStackTrace();
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      finally
      {
         coverageDataCommandEvent.setResult("SUCCESS");
         if (fout != null)
         {
            try
            {
               fout.close();
            }
            catch (Exception e)
            {
               throw new RuntimeException("Could not close coverage file", e);
            }
         }
      }
   }
}
