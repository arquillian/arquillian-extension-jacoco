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
package org.jboss.arquillian.extension.jacoco.test.integration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import javax.inject.Inject;

import junit.framework.Assert;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.extension.jacoco.test.included.ImportedSubArchive;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.exporter.ZipExporter;
import org.jboss.shrinkwrap.api.importer.ZipImporter;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

/*
 * Test case to verify we can handle Classes found in archives imported
 * from a Zip file. Covergae verification is done in VerifyIntegrationCoverageTestCase
 */
@RunWith(Arquillian.class)
public class ImportedSubArchiveTestCase {

	@Deployment
	public static WebArchive createImportedArchive() throws Exception {
		WebArchive war = ShrinkWrap.create(WebArchive.class)
							.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml")
							.addClasses(ImportedSubArchiveTestCase.class)
							.addAsLibraries(
								ShrinkWrap.create(JavaArchive.class)
									.addClass(ImportedSubArchive.class)
									.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
							);
		
		ByteArrayOutputStream target = new ByteArrayOutputStream();
		war.as(ZipExporter.class).exportTo(target);
		target.flush();

		ByteArrayInputStream source = new ByteArrayInputStream(target.toByteArray());
		return ShrinkWrap.create(ZipImporter.class, war.getName()).importFrom(source).as(WebArchive.class);
	}
	
	@Inject
	private ImportedSubArchive bean;
	
	@Test
	public void shouldBeInvoked() {
		Assert.assertEquals("A", bean.getName());
	}
}
