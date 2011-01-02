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

import org.apache.maven.model.License;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.soebes.maven.plugins.mlv.TestBase;
import com.soebes.maven.plugins.mlv.model.LicensesContainer;

/**
 * The intention of this test is to check if the comparsion only based
 * on name and/or on URL of the license is working correctly with activated
 * <b>strictChecking</b> option.
 * That means both <b>name</b> and <b>url</b> must part of the licenses.xml file.
 * 
 * @author Karl Heinz Marbaise
 *
 */
public class LicenseValidatorStrictTest extends TestBase {

	private LicenseValidator result;
	private LicensesContainer licensesContainer;

	@BeforeClass
	public void beforeClass() throws IOException, XmlPullParserException {
		licensesContainer = LicensesFile.getLicenses(new File(getTestResourcesDirectory() + "licenses.xml"));
		result = new LicenseValidator(licensesContainer);
		result.setStrictChecking(true);
	}

	@AfterClass
	public void afterClass() {
		result = null;
	}

	@Test
	public void catagorizeCPLV10() {
		License cl = new License ();
		cl.setName("Common Public License Version 1.0");
		cl.setUrl("http://www.opensource.org/licenses/cpl1.0.txt");
		Assert.assertTrue(result.isValid(cl));
		Assert.assertFalse(result.isInvalid(cl));
		Assert.assertFalse(result.isWarning(cl));
		Assert.assertFalse(result.isUnknown(cl));
	}

	@Test
	public void catagorizeApache20() {
		License cl = new License ();
		cl.setName("Apache Software License, Version 2.0");
		cl.setUrl("http://apache.org/licenses/LICENSE-2.0.txt");
		Assert.assertTrue(result.isValid(cl));
		Assert.assertFalse(result.isInvalid(cl));
		Assert.assertFalse(result.isWarning(cl));
		Assert.assertFalse(result.isUnknown(cl));
	}

	@Test
	public void categorizeApache20WithWrongURL() {
		License cl = new License();
		cl.setName("The Apache Software License, Version 2.0");
		cl.setUrl("/LICENSE.txt");
		Assert.assertFalse(result.isValid(cl));
		Assert.assertFalse(result.isInvalid(cl));
		Assert.assertFalse(result.isWarning(cl));
		Assert.assertTrue(result.isUnknown(cl));
	}

	@Test
	public void catagorizeGPL20() {
		License cl = new License ();
		cl.setName("GNU General Public License, version 3");
		cl.setUrl("http://www.gnu.org/licenses/gpl-3.0.txt");
		Assert.assertFalse(result.isValid(cl));
		Assert.assertTrue(result.isInvalid(cl));
		Assert.assertFalse(result.isWarning(cl));
		Assert.assertFalse(result.isUnknown(cl));
	}

	@Test
	public void catagorizeApache11() {
		License cl = new License ();
		cl.setName("Apache License, Version 1.1");
		cl.setUrl("http://www.apache.org/licenses/LICENSE-1.1");
		Assert.assertFalse(result.isValid(cl));
		Assert.assertFalse(result.isInvalid(cl));
		Assert.assertTrue(result.isWarning(cl));
		Assert.assertFalse(result.isUnknown(cl));
	}

	@Test
	public void catagorizeApache11OnlyByURL() {
		License cl = new License ();
		cl.setUrl("http://www.apache.org/licenses/LICENSE-1.1");
		Assert.assertFalse(result.isValid(cl));
		Assert.assertFalse(result.isInvalid(cl));
		Assert.assertFalse(result.isWarning(cl));
		Assert.assertTrue(result.isUnknown(cl));
	}

	@Test
	public void catagorizeUnknown() {
		License cl = new License ();
		cl.setName("Unknown License");
		cl.setUrl(null);
		Assert.assertFalse(result.isValid(cl));
		Assert.assertFalse(result.isInvalid(cl));
		Assert.assertFalse(result.isWarning(cl));
		Assert.assertTrue(result.isUnknown(cl));
	}

	@Test
	public void catagorizeArtifactValidWithTwoLicensesNames() {
		License cl1 = new License();
		cl1.setName("Test License");
		cl1.setUrl(null);

		License cl2 = new License();
		cl2.setName("Test License, Version 1.0");
		cl2.setUrl(null);
		
		ArrayList<License> licenses = new ArrayList<License>();
		licenses.add(cl1);
		licenses.add(cl2);
		
		Assert.assertFalse(result.isValid(licenses));
		Assert.assertFalse(result.isInvalid(licenses));
		Assert.assertFalse(result.isWarning(licenses));
		Assert.assertTrue(result.isUnknown(licenses));

	}

	@Test
	public void catagorizeArtifactValidWithTwoLicensesNameAndURL() {
		License cl1 = new License();
		cl1.setName("Test License");
		cl1.setUrl(null);

		License cl2 = new License();
		cl2.setName(null);
		cl2.setUrl("http://www.testlicense.org/License-1.0.txt");
		
		ArrayList<License> licenses = new ArrayList<License>();
		licenses.add(cl1);
		licenses.add(cl2);
		
		Assert.assertFalse(result.isValid(licenses));
		Assert.assertFalse(result.isInvalid(licenses));
		Assert.assertFalse(result.isWarning(licenses));
		Assert.assertTrue(result.isUnknown(licenses));
	}

	@Test
	public void catagorizeArtifactWithTwoLicensesFromTwoCategories() {
		// The first license is in the "Invalid" category
		// whereas the second one is in the "Valid" category
		License cl1 = new License();
		cl1.setName("GNU General Public License, version 2");
		cl1.setUrl(null);

		License cl2 = new License();
		cl2.setName(null);
		cl2.setUrl("http://www.testlicense.org/License-1.0.txt");

		ArrayList<License> licenses = new ArrayList<License>();
		licenses.add(cl1);
		licenses.add(cl2);

		Assert.assertFalse(result.isValid(licenses));
		Assert.assertFalse(result.isInvalid(licenses));
		Assert.assertFalse(result.isWarning(licenses));
		Assert.assertTrue(result.isUnknown(licenses));
	}

	@Test
	public void catagorizeArtifactWithTwoLicensesFromNoCategory() {
		// whereas the second one is in the "Valid" category
		License cl1 = new License();
		cl1.setName("Unknown License V1.0");
		cl1.setUrl(null);

		License cl2 = new License();
		cl2.setName(null);
		cl2.setUrl("http://www.the-unknown-license.com/license-v.1.0.html");

		ArrayList<License> licenses = new ArrayList<License>();
		licenses.add(cl1);
		licenses.add(cl2);

		Assert.assertFalse(result.isValid(licenses));
		Assert.assertFalse(result.isInvalid(licenses));
		Assert.assertFalse(result.isWarning(licenses));
		Assert.assertTrue(result.isUnknown(licenses));
	}

	@Test
	public void catagorizeArtifactNoLicenses() {
		ArrayList<License> licenses = new ArrayList<License>();

		Assert.assertFalse(result.isValid(licenses));
		Assert.assertFalse(result.isInvalid(licenses));
		Assert.assertFalse(result.isWarning(licenses));
		Assert.assertTrue(result.isUnknown(licenses));
	}

	
}
