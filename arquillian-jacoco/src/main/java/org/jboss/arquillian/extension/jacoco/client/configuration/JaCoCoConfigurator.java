/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
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

package org.jboss.arquillian.extension.jacoco.client.configuration;

import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

import java.util.Map;

/**
 * @author Lukas Krejci
 */
public class JaCoCoConfigurator {

    private static final String EXTENSION_NAME = "jacoco";

    @Inject
    @ApplicationScoped
    private InstanceProducer<JaCoCoConfiguration> configurationProvider;

    public void configure(@Observes ArquillianDescriptor desc) {
        Map<String, String> config = desc.extension(EXTENSION_NAME)
            .getExtensionProperties();

        configurationProvider.set(JaCoCoConfiguration.fromMap(config));
    }
}
