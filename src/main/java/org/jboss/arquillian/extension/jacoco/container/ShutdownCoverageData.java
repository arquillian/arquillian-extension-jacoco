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
package org.jboss.arquillian.extension.jacoco.container;

import java.io.ByteArrayOutputStream;

import org.jacoco.core.data.ExecutionDataWriter;
import org.jacoco.core.runtime.IRuntime;
import org.jboss.arquillian.container.test.spi.command.CommandService;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.event.suite.AfterSuite;

/**
 * StartCoverageData
 * 
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class ShutdownCoverageData {
    @Inject
    private Instance<IRuntime> runtimeInst;

    @Inject
    private Instance<CommandService> commandService;

    public void writeCoverageData(@Observes AfterSuite arqEvent) throws Exception {
        IRuntime runtime = runtimeInst.get();
        if (runtime != null) {
            ByteArrayOutputStream coverageOutputStream = null;
            try {
                coverageOutputStream = new ByteArrayOutputStream();
                ExecutionDataWriter writer = new ExecutionDataWriter(coverageOutputStream);
                runtime.collect(writer, writer, true);
            } finally {
                runtime.shutdown();
                if (coverageOutputStream != null) {
                    try {
                        coverageOutputStream.close();
                    } catch (Exception e) {
                        throw new RuntimeException("Could not close coverage file", e);
                    }
                }
            }

            commandService.get().execute(new CoverageDataCommand<ByteArrayOutputStream>(coverageOutputStream));
        }
    }
}
