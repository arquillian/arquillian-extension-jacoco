package org.jboss.arquillian.extension.jacoco.client;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfoStore;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.extension.jacoco.CoverageDataCommand;

public class CoverageDataReceiver
{
   private static final String TARGET_FOLDER = "target/";

   @Inject
   private Instance<JacocoConfiguration> configuration;

   public void storeCoverageData(@Observes CoverageDataCommand coverageDataCommandEvent)
   {
      try
      {
         ExecutionDataStore dataStore = new ExecutionDataStore();
         SessionInfoStore sessionStore = new SessionInfoStore();
         
         read(new ByteArrayInputStream(coverageDataCommandEvent.getCoverageDate()), dataStore, sessionStore);
         
         File targetDirectory = new File(TARGET_FOLDER);
         targetDirectory.mkdirs();
            File targetFile = getTargetFile(targetDirectory);
         
         if(targetFile.exists())
         {
            // append to existing data set, each @Test come back as a single stream
            read(new FileInputStream(targetFile), dataStore, sessionStore);
         }

         write(new FileOutputStream(targetFile), dataStore, sessionStore);
         
         coverageDataCommandEvent.setResult("SUCCESS");

      }
      catch (Exception e)
      {
         coverageDataCommandEvent.setResult("FAILURE");
         e.printStackTrace();
      }
   }
   
   private void read(InputStream stream, IExecutionDataVisitor executionDataVisitor, ISessionInfoVisitor sessionVisitor)
      throws IOException
   {
      ExecutionDataReader reader = new ExecutionDataReader(stream);
      
      reader.setExecutionDataVisitor(executionDataVisitor);
      reader.setSessionInfoVisitor(sessionVisitor);
      try
      {
         reader.read();
      }
      catch (IOException e) 
      {
         if(stream != null)
         {
            try
            {
               stream.close();
            } 
            catch (Exception e2) 
            {
               e2.printStackTrace();
            }
         }
      }
   }
   
   private void write(OutputStream stream, ExecutionDataStore executionData, SessionInfoStore sessionStore) 
   {
      try
      {
         ExecutionDataWriter writer = new ExecutionDataWriter(stream);
         
         executionData.accept(writer);
         sessionStore.accept(writer);
         writer.flush();
      }
      catch (IOException e) 
      {
         if(stream != null)
         {
            try
            {
               stream.close();
            }
            catch (IOException e2) 
            {
               e.printStackTrace();
            }
         }
      }
    }

    private File getTargetFile(File targetDirectory) {
        String fileName = JacocoConfiguration.DEST_FILE_DEFAULT_VALUE;

        if (configuration != null) {
            JacocoConfiguration jc = configuration.get();
            if (jc != null) {
                fileName = jc.getDestinationFileName();
            }
        }

        return new File(targetDirectory, fileName);
    }
}
