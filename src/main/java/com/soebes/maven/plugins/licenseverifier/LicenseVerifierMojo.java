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
package com.soebes.maven.plugins.licenseverifier;

import org.apache.maven.plugin.MojoExecutionException;

import com.soebes.maven.plugins.licenseverifier.licenses.LicenseInformation;


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
                    getLog().warn("The artifact: " + item.getArtifact().getId() + " has been excluded by the configuration.");
                }
            }
        }

        if (licenseData.hasValid()) {
            for (LicenseInformation item : licenseData.getValid()) {
                if (isVerbose()) {
                    getLog().info(createLogString(6, item, "]VALID[", "valid"));
                }
            }
        }

        if (licenseData.hasInvalid()) {
            for (LicenseInformation item : licenseData.getInvalid()) {
                getLog().error(createLogString(7, item, "]INVALID[", "invalid"));
            }
        }

        if (licenseData.hasWarning()) {
            for (LicenseInformation item : licenseData.getWarning()) {
                getLog().warn(createLogString(9, item, "]WARNING[", "warning"));
            }
        }

        if (licenseData.hasUnknown()) {
            for (LicenseInformation item : licenseData.getUnknown()) {
                getLog().warn(createLogString(9, item, "]UNKNOWN[", "unknown"));
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

    private String createLogString(int logLevelLength, LicenseInformation item, String prefix, String status) {
        StringBuilder log = new StringBuilder();

        for(int i = 0; i < (9 - logLevelLength); i++) {
            log.append(" ");
        }

        log.append(prefix);

        for(int i = 0; i < (10 - prefix.length()); i++) {
            log.append(" ");
        }

        log.append("(");
        log.append(item.getArtifact().getScope());
        log.append(") The artifact ");
        log.append(item.getProject().getId());
        log.append(" has a license ");
        if (isExplicitLicenceInfo()) {
            StringBuilder licenseInfo = new StringBuilder();
            for(org.apache.maven.model.License license : item.getLicenses()) {
                licenseInfo.append("\"");
                licenseInfo.append(license.getName());
                licenseInfo.append("\" - ");
                licenseInfo.append(license.getUrl());
                licenseInfo.append(", ");
            }

            if (licenseInfo.length() > 2) {
                log.append("(");
                log.append(licenseInfo.toString().substring(0, licenseInfo.length() - 2));
                log.append(") ");
            }
        }

        log.append("which is categorized as ");
        log.append(status);

        return log.toString();
    }
}
