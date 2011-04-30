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
package com.soebes.maven.plugins.mlv;

import java.util.Collections;
import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;


/**
 *
 * @goal verify
 * @phase prepare-package
 * @requiresDependencyResolution test
 * @author Karl Heinz Marbaise
 */
public class LicenseVerifierMojo
    extends AbstractLicenseVerifierPlugIn
{

    public void execute()
        throws MojoExecutionException
    {
// The following code can be later used if we load a license.xml file from an
// different package.
//		ClassLoader sysClassLoader = this.getClass().getClassLoader();
//        URL[] urls = ((URLClassLoader)sysClassLoader).getURLs();
//        if (getLog().isDebugEnabled()) {
//			for (int i = 0; i < urls.length; i++) {
//				getLog().debug("ClassPath: " + urls[i].getFile());
//			}
//        }
//
//		try {
//			Enumeration<URL> list = this.getClass().getClassLoader().getResources("/license/*.xml");
//		} catch (IOException e) {
//			getLog().error("Exception: " + e.getMessage());
//		}
        loadLicensesFile();

        //Get a set with all dependent artifacts incl.
        //the transitive dependencies.
        Set<?> depArtifacts = this.project.getArtifacts();

        //Check if we have to do something.
        if (depArtifacts.isEmpty()) {
            getLog().info("We haven't found any dependencies.");
            return;
        }

        //Get all the informations about the licenses of the artifacts.
        getDependArtifacts(depArtifacts);

        Collections.sort(getLicenseInformations(), new ArtifactComperator());

        boolean isValid = false;
        boolean isInvalid = false;
        boolean isWarning = false;
        boolean isUnknwon = false;

        for (LicenseInformation item : getLicenseInformations()) {

            /*
             * [INFO]    XXXX
             * [ERROR]   XXXX
             * [WARNING] XXXX
             *
             * ]VALID[   XXX
             * ]INVALID[ XXX
             * ]WARNING[ XXX
             * ]UNKNOWN[ XXX
             */
            if (licenseValidator.isValid(item.getLicenses())) {
                if (isVerbose()) {
                    getLog().info("   ]VALID[   (" + item.getArtifact().getScope() + ") The artifact " + item.getProject().getId() + " has a license which is categorized as valid");
                }
                isValid = true;
            } else if (licenseValidator.isInvalid(item.getLicenses())) {
                getLog().error("  ]INVALID[ (" + item.getArtifact().getScope() + ") The artifact " + item.getProject().getId() + " has a license which is categorized as invalid");
                isInvalid = true;
            } else if (licenseValidator.isWarning(item.getLicenses())) {
                getLog().warn("]WARNING[ (" + item.getArtifact().getScope() + ") The artifact " + item.getProject().getId() + " has a license which is categorized as warning");
                isWarning = true;
            } else if (licenseValidator.isUnknown(item.getLicenses())) {
                getLog().warn("]UNKNOWN[ (" + item.getArtifact().getScope() + ") The artifact " + item.getProject().getId() + " has a license which is categorized as unknown");
                isUnknwon = true;
            }
        }

        if (isValid && failOnValid) {
            throw new MojoExecutionException("A license which is categorized as VALID has been found.");
        }
        if (isInvalid && failOnInvalid) {
            throw new MojoExecutionException("A license which is categorized as INVALID has been found.");
        }
        if (isWarning && failOnWarning) {
            throw new MojoExecutionException("A license which is categorized as WARNING has been found.");
        }
        if (isUnknwon && failOnUnknown) {
            throw new MojoExecutionException("A license which is categorized as UNKNOWN has been found.");
        }
    }

}
