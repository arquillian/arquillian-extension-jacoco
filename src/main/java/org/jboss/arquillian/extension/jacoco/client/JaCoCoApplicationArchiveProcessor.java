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
package org.jboss.arquillian.extension.jacoco.client;

import org.jboss.arquillian.container.test.spi.client.deployment.ApplicationArchiveProcessor;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.extension.jacoco.client.configuration.JaCoCoConfiguration;
import org.jboss.arquillian.extension.jacoco.client.instrumentation.ArchiveInstrumenter;
import org.jboss.arquillian.extension.jacoco.client.instrumentation.SignatureRemover;
import org.jboss.arquillian.test.spi.TestClass;
import org.jboss.shrinkwrap.api.Archive;

/**
 * Instrument all Classes (or their subset if found in the User defined
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @author <a href="mailto:lkrejci@redhat.com">Lukas Krejci</a>
 * @version $Revision: $
 * @Deployment.
 */
public class JaCoCoApplicationArchiveProcessor implements ApplicationArchiveProcessor {

    @Inject
    private Instance<JaCoCoConfiguration> config;

    public void process(Archive<?> applicationArchive, TestClass testClass) {
        new ArchiveInstrumenter(new SignatureRemover()).processArchive(applicationArchive, config.get().getClassFilter());
    }
}
