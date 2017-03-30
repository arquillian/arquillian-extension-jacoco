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

import org.jboss.arquillian.extension.jacoco.client.filter.FilterComposer;
import org.jboss.shrinkwrap.api.ArchivePath;
import org.jboss.shrinkwrap.api.Filter;
import org.jboss.shrinkwrap.api.Filters;

import java.util.*;

/**
 * @author Lukas Krejci
 */
public class JaCoCoConfiguration {

    public static final Filter<ArchivePath> ALL_CLASSES = Filters.include(".*\\.class");

    private static final String INCLUDES_PROPERTY = "includes";
    public static final String INCLUDES_DEFAULT_VALUE = null;
    private static final String EXCLUDES_PROPERTY = "excludes";
    public static final String EXCLUDES_DEFAULT_VALUE = null;
    private static final String APPEND_ASM_LIBRARY_PROPERTY = "appendAsmLibrary";

    private static final String APPEND_ASM_LIBRARY_DEFAULT = "true";

    private Filter<ArchivePath> composedFilter;

    private boolean appendAsmLibrary;

    private static class ConfigMap {
        Map<String, String> map;

        ConfigMap(Map<String, String> map) {
            this.map = map;
        }

        String get(String key, String defaultValue) {
            String ret = map.get(key);
            return ret == null ? defaultValue : ret;
        }
    }

    public static JaCoCoConfiguration fromMap(Map<String, String> map) {
        JaCoCoConfiguration ret = new JaCoCoConfiguration();

        ConfigMap c = new ConfigMap(map);

        String incls = c.get(INCLUDES_PROPERTY, INCLUDES_DEFAULT_VALUE);
        String excls = c.get(EXCLUDES_PROPERTY, EXCLUDES_DEFAULT_VALUE);

        FilterComposer composer = FilterComposer.create(incls, excls);
        ret.composedFilter = composer.composeFilter();

        String appendAsmLibrary = c.get(APPEND_ASM_LIBRARY_PROPERTY, APPEND_ASM_LIBRARY_DEFAULT);
        ret.appendAsmLibrary = Boolean.valueOf(appendAsmLibrary);

        return ret;
    }

    public Filter<ArchivePath> getClassFilter() {
        return composedFilter;
    }

    public boolean isAppendAsmLibrary() {
        return appendAsmLibrary;
    }

    public static boolean isJacocoAgentActive() {
        try {
            UUID.class.getDeclaredField("$jacocoAccess");
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
