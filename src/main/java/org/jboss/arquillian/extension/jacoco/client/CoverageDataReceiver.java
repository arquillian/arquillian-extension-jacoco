package org.jboss.arquillian.extension.jacoco.client;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.extension.jacoco.container.CoverageDataCommand;

public class CoverageDataReceiver {

    public void storeCoverageData(@Observes CoverageDataCommand<ByteArrayOutputStream> coverageDataCommandEvent) {
        FileOutputStream fout = null;
        try {
            fout = new FileOutputStream("/home/jose_freitas/java/opensource/jacocoext/dummy.data");
            fout.write(coverageDataCommandEvent.getResult().toByteArray());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fout != null) {
                try {
                    fout.close();
                } catch (Exception e) {
                    throw new RuntimeException("Could not close coverage file", e);
                }
            }
        }
    }
}
