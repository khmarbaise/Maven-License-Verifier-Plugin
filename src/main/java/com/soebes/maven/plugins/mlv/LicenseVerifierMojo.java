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
package com.soebes.maven.plugins.mlv;

import org.apache.maven.plugin.MojoExecutionException;

import com.soebes.maven.plugins.mlv.licenses.LicenseInformation;


/**
 * This goal is intended to check the artifacts during the prepare-package phase and
 * print out warnings etc. based on the given configuration for the plugin.
 *
 * @goal verify
 * @phase prepare-package
 * @requiresDependencyResolution test
 * @author <a href="mailto:kama@soebes.de">Karl Heinz Marbaise</a>
 */
public class LicenseVerifierMojo extends AbstractLicenseVerifierPlugIn {

    public void execute() throws MojoExecutionException {

        loadLicenseData();

        if (licenseData.hasExcludedByConfiguration()) {
            if (isVerbose()) {
                for (LicenseInformation item : licenseData.getValid()) {
                    getLog().warn("The artifact: " + item.getArtifact().getId() + " has been execluded by the configuration.");
                }
            }
        }

        if (licenseData.hasValid()) {
            for (LicenseInformation item : licenseData.getValid()) {
                if (isVerbose()) {
                    getLog().info("   ]VALID[   (" + item.getArtifact().getScope() + ") The artifact " + item.getProject().getId() + " has a license which is categorized as valid");
                }
            }
        }

        if (licenseData.hasInvalid()) {
            for (LicenseInformation item : licenseData.getInvalid()) {
                getLog().error("  ]INVALID[ (" + item.getArtifact().getScope() + ") The artifact " + item.getProject().getId() + " has a license which is categorized as invalid");
            }
        }

        if (licenseData.hasWarning()) {
            for (LicenseInformation item : licenseData.getWarning()) {
                getLog().warn("]WARNING[ (" + item.getArtifact().getScope() + ") The artifact " + item.getProject().getId() + " has a license which is categorized as warning");
            }
        }

        if (licenseData.hasUnknown()) {
            for (LicenseInformation item : licenseData.getUnknown()) {
                getLog().warn("]UNKNOWN[ (" + item.getArtifact().getScope() + ") The artifact " + item.getProject().getId() + " has a license which is categorized as unknown");
            }
        }

        if (licenseData.hasValid() && failOnValid) {
            throw new MojoExecutionException("A license which is categorized as VALID has been found.");
        }

        if (licenseData.hasInvalid() && failOnInvalid) {
            throw new MojoExecutionException("A license which is categorized as INVALID has been found.");
        }

        if (licenseData.hasWarning() && failOnWarning) {
            throw new MojoExecutionException("A license which is categorized as WARNING has been found.");
        }

        if (licenseData.hasUnknown() && failOnUnknown) {
            throw new MojoExecutionException("A license which is categorized as UNKNOWN has been found.");
        }
    }

}
