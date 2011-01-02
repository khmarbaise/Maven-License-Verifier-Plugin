/*
 * Copyright 2010 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soebes.maven.plugins.mlv.licenses;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.soebes.maven.plugins.mlv.TestBase;
import com.soebes.maven.plugins.mlv.model.LicenseItem;
import com.soebes.maven.plugins.mlv.model.LicensesContainer;
import com.soebes.maven.plugins.mlv.model.LicensesList;

/**
 * This Test will check the basic functionality of reading/writing
 * of XML file.
 * @author Karl Heinz Marbaise
 *
 */
public class LicenseFileTest extends TestBase {

	@Test
	public void writeLicenseFile() throws IOException {
		String expectedResult =
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+"<licenses>\n"
			+"  <valid>\n"
			+"    <license>\n"
			+"      <id>ThisIdNr1</id>\n"
			+"      <description>Test desc</description>\n"
			+"      <names>\n"
			+"        <name>Name 1</name>\n"
			+"        <name>Name 2</name>\n"
			+"      </names>\n"
			+"      <urls>\n"
			+"        <url>URL 1</url>\n"
			+"        <url>URL 2</url>\n"
			+"      </urls>\n"
			+"    </license>\n"
		    +"    <license>\n"
			+"      <id>ThisIsId2</id>\n"
		    +"      <description>Second one</description>\n"
		    +"      <names>\n"
		    +"        <name>Name 1</name>\n"
		    +"      </names>\n"
		    +"      <urls>\n"
		    +"        <url>URL 1</url>\n"
		    +"      </urls>\n"
		    +"    </license>\n"
			+"  </valid>\n"
			+"</licenses>\n";
		
		LicensesContainer licensesContainer = new LicensesContainer();

		LicensesList validLicenseList = new LicensesList();

		LicenseItem license = new LicenseItem();
		license.setId("ThisIdNr1");
		license.setDescription("Test desc");
		ArrayList<String> names = new ArrayList<String>();
		names.add("Name 1");
		names.add("Name 2");
		license.setNames(names);
		ArrayList<String> urls = new ArrayList<String>();
		urls.add("URL 1");
		urls.add("URL 2");
		license.setUrls(urls);
		validLicenseList.addLicense(license);
		
		license = new LicenseItem();
		license.setId("ThisIsId2");
		license.setDescription("Second one");
		names = new ArrayList<String>();
		names.add("Name 1");
		license.setNames(names);
		urls = new ArrayList<String>();
		urls.add("URL 1");
		license.setUrls(urls);
		validLicenseList.addLicense(license);

		licensesContainer.setValid(validLicenseList);
		
		Assert.assertEquals(LicensesFile.toString(licensesContainer), expectedResult);
	}

	@Test
	public void readLicenseFile() throws IOException, XmlPullParserException {
		LicensesContainer result = LicensesFile.getLicenses(new File(getTestResourcesDirectory() + "licenses.xml"));
		Assert.assertEquals(false, result.getInvalid().getLicenses().isEmpty());
		Assert.assertEquals(false, result.getValid().getLicenses().isEmpty());
		Assert.assertEquals(false, result.getWarning().getLicenses().isEmpty());

		Assert.assertEquals(result.getValid().getLicenses().get(0).getId(),"Apache Software License 2.0");
		Assert.assertEquals(result.getValid().getLicenses().get(0).getDescription(),"Apache Software License 2.0");
		Assert.assertEquals(result.getValid().getLicenses().get(0).getNames().get(0),"Apache 2");
		Assert.assertEquals(result.getValid().getLicenses().get(0).getNames().get(1),"Apache Software License 2.0");
		Assert.assertEquals(result.getValid().getLicenses().get(0).getNames().get(2),"Apache Software License, Version 2.0");

		Assert.assertEquals(result.getValid().getLicenses().get(0).getUrls().get(0),"http://www.apache.org/licenses/LICENSE-2.0");
		Assert.assertEquals(result.getValid().getLicenses().get(0).getUrls().get(1),"http://www.apache.org/licenses/LICENSE-2.0.html");
		Assert.assertEquals(result.getValid().getLicenses().get(0).getUrls().get(2),"http://www.apache.org/licenses/LICENSE-2.0.txt");
		Assert.assertEquals(result.getValid().getLicenses().get(0).getUrls().get(3),"http://apache.org/licenses/LICENSE-2.0");
		Assert.assertEquals(result.getValid().getLicenses().get(0).getUrls().get(4),"http://apache.org/licenses/LICENSE-2.0.html");
		Assert.assertEquals(result.getValid().getLicenses().get(0).getUrls().get(5),"http://apache.org/licenses/LICENSE-2.0.txt");

		Assert.assertEquals(result.getInvalid().getLicenses().get(0).getId(),"GNU General Public License (GPL)");
		Assert.assertEquals(result.getInvalid().getLicenses().get(0).getDescription(),"GNU General Public License (GPL)");
		Assert.assertEquals(result.getInvalid().getLicenses().get(0).getNames().get(0),"GNU General Public License, version 2");
		Assert.assertEquals(result.getInvalid().getLicenses().get(0).getNames().get(1),"GNU General Public License, version 3");
		Assert.assertEquals(result.getInvalid().getLicenses().get(0).getUrls().get(0),"http://www.gnu.org/licenses/gpl-2.0.html");
		Assert.assertEquals(result.getInvalid().getLicenses().get(0).getUrls().get(1),"http://www.gnu.org/licenses/gpl-3.0.txt");

		Assert.assertEquals(result.getWarning().getLicenses().get(0).getId(),"Apache Software License 1.1 (Historic)");
		Assert.assertEquals(result.getWarning().getLicenses().get(0).getDescription(),"Apache Software License 1.1 (Historic)");
		Assert.assertEquals(result.getWarning().getLicenses().get(0).getNames().get(0),"Apache License, Version 1.1");
		Assert.assertEquals(result.getWarning().getLicenses().get(0).getUrls().get(0),"http://www.apache.org/licenses/LICENSE-1.1");
		
	}
	
}
