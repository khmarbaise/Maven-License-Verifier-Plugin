/**
 * The Maven License Verifier Plugin
 *
 * Copyright (c) 2009, 2010, 2011 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2009, 2010, 2011 by Karl Heinz Marbaise
 *
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.soebes.maven.plugins.licenseverifier.licenses;

import static org.fest.assertions.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.maven.model.License;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.soebes.maven.plugins.licenseverifier.TestBase;
import com.soebes.maven.plugins.licenseverifier.licenses.LicenseValidator;
import com.soebes.maven.plugins.licenseverifier.licenses.LicensesFile;
import com.soebes.maven.plugins.mlv.model.LicensesContainer;

/**
 * The intention of this test is to check if the usage of a licenses.xml file
 * will work which contains an invaild section only.
 *
 * @author Karl Heinz Marbaise
 *
 */
public class LicenseValidatorInvalidOnlyTest extends TestBase {

    private LicenseValidator result;
    private LicensesContainer licensesContainer;

    @BeforeClass
    public void beforeClass() throws IOException, XmlPullParserException {
        licensesContainer = LicensesFile
                .getLicenses(new File(getTestResourcesDirectory() + "licenses-invalid-only.xml"));
        result = new LicenseValidator(licensesContainer, false);
    }

    @AfterClass
    public void afterClass() {
        result = null;
    }

    @Test
    public void catagorizeApache20() {
        License cl = new License();
        cl.setName("Apache Software License, Version 2.0");
        cl.setUrl("http://apache.org/licenses/LICENSE-2.0.txt");

        assertThat(result.isValid(cl)).isFalse();
        assertThat(result.isInvalid(cl)).isTrue();
        assertThat(result.isWarning(cl)).isFalse();
        assertThat(result.isUnknown(cl)).isFalse();
    }

    @Test
    public void catagorizeUnknown() {
        License cl = new License();
        cl.setName("Unknown License");
        cl.setUrl(null);

        assertThat(result.isValid(cl)).isFalse();
        assertThat(result.isInvalid(cl)).isFalse();
        assertThat(result.isWarning(cl)).isFalse();
        assertThat(result.isUnknown(cl)).isTrue();
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

        assertThat(result.isValid(licenses)).isFalse();
        assertThat(result.isInvalid(licenses)).isFalse();
        assertThat(result.isWarning(licenses)).isFalse();
        assertThat(result.isUnknown(licenses)).isTrue();
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

        assertThat(result.isValid(licenses)).isFalse();
        assertThat(result.isInvalid(licenses)).isFalse();
        assertThat(result.isWarning(licenses)).isFalse();
        assertThat(result.isUnknown(licenses)).isTrue();
    }

    @Test
    public void catagorizeArtifactNoLicenses() {
        ArrayList<License> licenses = new ArrayList<License>();

        assertThat(result.isValid(licenses)).isFalse();
        assertThat(result.isInvalid(licenses)).isFalse();
        assertThat(result.isWarning(licenses)).isFalse();
        assertThat(result.isUnknown(licenses)).isTrue();
    }

}
