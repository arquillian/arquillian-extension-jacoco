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
package org.jboss.arquillian.extension.jacoco.test.unit;

import java.io.File;
import java.io.FileInputStream;

import org.jacoco.core.analysis.Analyzer;
import org.jacoco.core.analysis.CoverageBuilder;
import org.jacoco.core.data.ExecutionData;
import org.jacoco.core.data.ExecutionDataReader;
import org.jacoco.core.data.ExecutionDataStore;
import org.jacoco.core.data.SessionInfoStore;
import org.junit.Ignore;
import org.junit.Test;

/**
 * CoverageDataTestCase
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class CoverageDataTestCase {

    @Test
    @Ignore // not a test case, example usage
    public void generateReport() throws Exception {
        ExecutionDataReader reader = new ExecutionDataReader(new FileInputStream(new File("target/coverage.data")));
        ExecutionDataStore store = new ExecutionDataStore();
        SessionInfoStore sessionStore = new SessionInfoStore();

        reader.setExecutionDataVisitor(store);
        reader.setSessionInfoVisitor(sessionStore);
        reader.read();

        CoverageBuilder builder = new CoverageBuilder();
        Analyzer analyzer = new Analyzer(store, builder);

        for (ExecutionData testData : store.getContents()) {
            System.out.println("analyzingClasses " + analyzer.analyzeAll(
                new File("target/test-classes/" + testData.getName() + ".class")));
        }
        System.out.println("analyzingClasses " + analyzer.analyzeAll(new File("target/classes")));
    }
}
