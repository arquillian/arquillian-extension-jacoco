package org.jboss.arquillian.extension.jacoco.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.UUID;

import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.IExecutionDataVisitor;
import org.jacoco.core.data.ISessionInfoVisitor;
import org.jacoco.core.data.SessionInfoStore;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.extension.jacoco.CoverageDataCommand;

public class CoverageDataReceiver
{
   public void storeCoverageData(@Observes CoverageDataCommand coverageDataCommandEvent)
   {
      try
      {
         ExecutionDataStore dataStore = new ExecutionDataStore();
         SessionInfoStore sessionStore = new SessionInfoStore();

         read(new ByteArrayInputStream(coverageDataCommandEvent.getCoverageDate()), dataStore, sessionStore);

         if(JacocoConfiguration.isJacocoAgentActive()) {
            copyToAgentExecutionStore(dataStore);
         }

         coverageDataCommandEvent.setResult("SUCCESS");
      }
      catch (Exception e)
      {
         coverageDataCommandEvent.setResult("FAILURE");
         e.printStackTrace();
      }
   }

   private void copyToAgentExecutionStore(ExecutionDataStore dataStore) throws Exception
   {
      Field f = UUID.class.getDeclaredField("$jacocoAccess");
      Object executor = f.get(null);

      Method m = executor.getClass().getDeclaredMethod("getExecutionData", new Class[] {Object[].class});
      m.setAccessible(true);
      for (ExecutionData data : dataStore.getContents())
      {
         Object[] probeData = new Object[] { data.getId(), data.getName(), data.getData().length };
         m.invoke(executor, new Object[] { probeData });
         boolean[] resultData = (boolean[]) probeData[0];
         for (int i = 0; i < data.getData().length; i++)
         {
            if (!resultData[i])
            {
               resultData[i] = data.getData()[i];
            }
         }
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
         throw new RuntimeException("Could not read data to ExecutionDataStore from InputStream", e);
      }
      finally
      {
         if (stream != null)
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
}
