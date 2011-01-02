package com.soebes.maven.plugins.mlv;

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

import java.util.Set;

import org.apache.maven.plugin.MojoExecutionException;


/**
 *
 * @goal check
 * @phase test
 * @author Karl Heinz Marbaise
 */
public class LicenseVerifierMojo
    extends AbstractLicenseVerifierPlugIn
{

	@SuppressWarnings("unchecked")
	public void execute()
        throws MojoExecutionException
    {
//		getLog().info("LicenseVerifierMojo:execute()");
//		getLog().info("LicenseVerifierMojo:execute(): isExecutionRoot(): " + project.isExecutionRoot());
//		getLog().info("LicenseVerifierMojo:execute(): name:" + project.getName());
//		getLog().info("LicenseVerifierMojo:execute(): basedir:" + project.getBasedir().getAbsolutePath());


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
    	Set depArtifacts = project.getArtifacts();

    	//Check if we have to do something.
    	if (depArtifacts.isEmpty()) {
    		getLog().info("We haven't found any dependencies.");
    		return;
    	}

    	//Get all the informations about the licenses of the arttifacts.
    	getDependArtifacts(depArtifacts);

    	//@TODO: see #270 Check all artifacts and fail at the end and not at the first wrong artifact.
		for (LicenseInformation item : getLicenseInformations()) {
			if (verbose) {
				getLog().info("Checking artifact: " + item.getArtifact().getId());
			}
			if (licenseValidator.isValid(item.getLicenses())) {
				if (failOnValid) {
					throw new MojoExecutionException("Based on the configuration an valid license has been found.");
				}
			} else if (licenseValidator.isInvalid(item.getLicenses())) {
				getLog().error("/ERROR/ The artifact " + item.getProject().getId() + " has an license which is categorized as invalid");
				if (failOnInvalid) {
					throw new MojoExecutionException("Based on the configuration an invalid license has been found.");
				}
			} else if (licenseValidator.isWarning(item.getLicenses())) {
				getLog().warn("/WARNING/ The artifact " + item.getProject().getId() + " has an license which is categorized as warning");
				if (failOnWarning) {
					throw new MojoExecutionException("Based on the configuration an warining license has been found.");
				}
			} else if (licenseValidator.isUnknown(item.getLicenses())) {
				getLog().warn("/UNKNOWN/ The artifact " + item.getProject().getId() + " has an license which is categorized as unknown");
				if (failOnUnknown) {
					throw new MojoExecutionException("Based on the configuration an unknown license has been found.");
				}
			}
		}
    }

}
