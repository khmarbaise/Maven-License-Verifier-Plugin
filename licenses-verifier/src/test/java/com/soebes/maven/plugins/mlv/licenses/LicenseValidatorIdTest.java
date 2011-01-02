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

public class LicenseValidatorIdTest extends TestBase {

	private LicenseValidator result;
	private LicensesContainer licensesContainer;

	@BeforeClass
	public void beforeClass() throws IOException, XmlPullParserException {
		licensesContainer = LicensesFile.getLicenses(new File(getTestResourcesDirectory() + "licenses.xml"));
		result = new LicenseValidator(licensesContainer);
	}

	@AfterClass
	public void afterClass() {
		result = null;
	}

	@Test
	public void catagorizeGPL20WithId() {
		License cl = new License ();
		cl.setName("GNU General Public License, version 3");
		cl.setUrl("http://www.gnu.org/licenses/gpl-3.0.txt");
		Assert.assertFalse(result.isValid(cl));
		Assert.assertTrue(result.isInvalid(cl));
		Assert.assertFalse(result.isWarning(cl));
		Assert.assertFalse(result.isUnknown(cl));

		Assert.assertNull(result.getValidId(cl));
		Assert.assertEquals(result.getInvalidId(cl), "GNU General Public License (GPL)");
		Assert.assertNull(result.getWarningId(cl));
	}

	@Test
	public void catagorizeArtifactValidWithTwoLicensesNameAndURLWithId() {
		License cl1 = new License();
		cl1.setName("Test License");
		cl1.setUrl(null);

		License cl2 = new License();
		cl2.setName(null);
		cl2.setUrl("http://www.testlicense.org/License-1.0.txt");
		
		ArrayList<License> licenses = new ArrayList<License>();
		licenses.add(cl1);
		licenses.add(cl2);
		
		Assert.assertTrue(result.isValid(licenses));
		Assert.assertFalse(result.isInvalid(licenses));
		Assert.assertFalse(result.isWarning(licenses));
		Assert.assertFalse(result.isUnknown(licenses));

		Assert.assertEquals(result.getValidIds(licenses).size(), 1);
		Assert.assertEquals(result.getValidIds(licenses).get(0), "Test License for two Licenses");
	}

	@Test
	public void catagorizeTwoLicensesWithIds() {
		License cl1 = new License();
		cl1.setName("The TMate Open Source License");
		cl1.setUrl(null);

		License cl2 = new License();
		cl2.setName(null);
		cl2.setUrl("http://www.testlicense.org/License-1.0.txt");

		ArrayList<License> licenses = new ArrayList<License>();
		licenses.add(cl1);
		licenses.add(cl2);
		
		Assert.assertTrue(result.isValid(licenses));
		Assert.assertFalse(result.isInvalid(licenses));
		Assert.assertFalse(result.isWarning(licenses));
		Assert.assertFalse(result.isUnknown(licenses));

		Assert.assertEquals(result.getValidIds(licenses).size(), 2);
		Assert.assertEquals(result.getValidIds(licenses).get(0), "The TMate Open Source License");
		Assert.assertEquals(result.getValidIds(licenses).get(1), "Test License for two Licenses");
	}


}
