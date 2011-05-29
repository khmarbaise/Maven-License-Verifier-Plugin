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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.model.License;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.soebes.maven.plugins.mlv.licenses.LicenseData;
import com.soebes.maven.plugins.mlv.licenses.LicenseInformation;
import com.soebes.maven.plugins.mlv.licenses.LicenseValidator;
import com.soebes.maven.plugins.mlv.licenses.LicensesFile;
import com.soebes.maven.plugins.mlv.model.LicensesContainer;

/**
 * @author <a href="mailto:kama@soebes.de">Karl Heinz Marbaise</a>
 * @requiresDependencyResolution test
 */
public abstract class AbstractLicenseVerifierPlugIn extends AbstractMojo {

    /**
     * The Maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Used to build a maven projects from artifacts in the remote repository.
     *
     * @component
     * @required
     * @readonly
     */
    protected MavenProjectBuilder projectBuilder;

    /**
     * Location of the local repository.
     *
     * @parameter expression="${localRepository}"
     * @readonly
     * @required
     */
    protected ArtifactRepository localRepository;

    /**
     * List of Remote Repositories used by the resolver
     *
     * @parameter expression="${project.remoteArtifactRepositories}"
     * @readonly
     * @required
     */
    protected List<ArtifactRepository> remoteRepositories;

    /**
     * This will turn on verbose behavior and will print out
     * all information about the artifacts.
     *
     * @parameter expression="${mlv.verbose}" default-value="false"
     */
    private boolean verbose;

    /**
     * This will turn on strictChecking behavior which
     * will check URL and Name of a license instead of
     * only URL or Name.
     *
     * @parameter expression="${mlv.strickChecking}" default-value="false"
     */
    protected boolean stricktChecking;

    /**
     * The build will fail if a license with the category <b>Valid</b>
     * has been found.
     * @parameter expression="${mlv.failOnValid}" default-value="false"
     */
    protected boolean failOnValid;
    /**
     * The build will fail if a license with the category <b>Invalid</b>
     * has been found.
     * @parameter expression="${mlv.failOnInvalid}" default-value="false"
     */
    protected boolean failOnInvalid;
    /**
     * The build will fail if a license with the category <b>Warning</b>
     * has been found.
     * @parameter expression="${mlv.failOnWarning}" default-value="false"
     */
    protected boolean failOnWarning;
    /**
     * The build will fail if a license can not be categorized
     * in any of the categories.
     * @parameter expression="${mlv.failOnUnknown}" default-value="false"
     */
    protected boolean failOnUnknown;

    /**
     * The name of the licenses.xml file which will be used to categorize
     * the licenses of the artifacts.
     * @parameter	expression="${mlv.licenseFile}"
     * 				default-value="${project.basedir}/src/licenses/licenses.xml"
     */
    protected File licenseFile;

    /**
     * Gives you the possibility to
     * define a list of references to licenses files.
     * <p>
     * &lt;configuration&gt;
     *    [...]
     *    &lt;licenseRefs&gt;
     *      &lt;licenseRef&gt;oss-licenses.xml&lt;/licenseRef&gt;
     *      &lt;licenseRef&gt;test-licenses.xml&lt;/licenseRef&gt;
     *      [...]
     *    &lt;/licenseRefs&gt;
     *    [...]
     * &lt;/configuration&gt;
     */
    protected ArrayList<File> licenseRefs;

    /**
     * By using excludes you can exclude particular artifacts from being checked
     * by the Maven Licenses Verifier Plugin.
     * <pre>
     *   &lt;excludes&gt;
     *      &lt;exclude&gt;groupId:artifactId:type:version&lt;/exclude&gt;
     *      ..
     *   &lt;/excludes&gt;
     * </pre>
     * @parameter
     */
    protected List<String> excludes;

    protected LicenseData licenseData = null;

    protected void loadLicenseData() throws MojoExecutionException {
        getLog().debug("loadLicenseData()");
        LicensesContainer licenseContainer = loadLicensesFile();

        //Get a set with all dependent artifacts incl.
        //the transitive dependencies.
        Set<?> depArtifacts = this.project.getArtifacts();

        //Get all the informations about the licenses of the artifacts.
        ArrayList<LicenseInformation> licenseInformations = getDependArtifacts(depArtifacts);

        LicenseValidator licenseValidator = new LicenseValidator(licenseContainer);
        licenseValidator.setStrictChecking(stricktChecking);


        Collections.sort(licenseInformations, new ArtifactComperator());

        licenseData = new LicenseData(licenseValidator, licenseInformations, excludes, getLog());
        getLog().debug("loadLicenseData() done.");
    }


    /**
     * Get all their dependencies and put the information
     * into the intermediate list of licenses.
     *
     * @param depArtifacts
     * @throws MojoExecutionException
     */
    protected ArrayList<LicenseInformation> getDependArtifacts(Set<?> depArtifacts)
            throws MojoExecutionException {

        ArrayList<LicenseInformation> licenseInformations = new ArrayList<LicenseInformation>();

        for (Iterator<?> depArtIter = depArtifacts.iterator(); depArtIter.hasNext(); ) {
           Artifact depArt = (Artifact) depArtIter.next();

           LicenseInformation li = new LicenseInformation();

           //store the artifact about which the following license information
           //will be extracted.
           li.setArtifact(depArt);
           MavenProject depProject = null;
           try
           {
              depProject = projectBuilder.buildFromRepository(depArt, remoteRepositories, localRepository, true);
           }
           catch (ProjectBuildingException e)
           {
              throw new MojoExecutionException( "Unable to build project: " + depArt.getDependencyConflictId(), e );
           }

           //Set the project of the current license information
           li.setProject(depProject);

           //Add all licenses of the particular artifact to it's other informations.
           List<?> licenses = depProject.getLicenses();
           Iterator<?> licenseIter = licenses.iterator();
           while (licenseIter.hasNext())
           {
              License license = (License) licenseIter.next();
              li.addLicense(license);
           }
           licenseInformations.add(li);
        }
        return licenseInformations;
    }

    /**
     * This method will load the licenses.xml file either
     * from file system or via classpath. The second case
     * is given if the user defines a particular dependency
     * to define the licenses via a maven artifact.
     *
     * @throws MojoExecutionException
     */
    protected LicensesContainer loadLicensesFile() throws MojoExecutionException {
        LicensesContainer licenseContainer = null;

        if (licenseFile == null)
        {
            //If no licenseFile configuration given we will end.
            //Check in which cases this happens!
            return null;
        }

        try {
            getLog().debug("Trying to find " + licenseFile.getAbsolutePath() + " in file system.");
            if (licenseFile.exists()) {
                getLog().debug("Found licenses file in file system.");
                getLog().info("Loading " + licenseFile.getAbsolutePath() + " licenses file.");
                licenseContainer = LicensesFile.getLicenses(licenseFile);
            } else {
                getLog().info("Loading license file via classpath.");
                URL licenseURL = this.getClass().getResource(licenseFile.getPath());
                InputStream inputStream = null;
                if (licenseURL == null) {
                    inputStream = this.getClass().getResourceAsStream("/licenses/licenses.xml");
                    licenseURL = this.getClass().getResource("/licenses/licenses.xml");
                } else {
                    inputStream = this.getClass().getResourceAsStream(licenseFile.getPath());
                    licenseURL = this.getClass().getResource(licenseFile.getPath());
                }
                getLog().debug("Loading licenses.xml from " + licenseURL);
                licenseContainer = LicensesFile.getLicenses(inputStream);
            }

            return licenseContainer;

        } catch (IOException e) {
            //Use the internal licenses.xml file.???
            throw new MojoExecutionException(
                "The LicenseFile configuration is wrong, " +
                "cause we couldn't find the " + licenseFile.getAbsolutePath());
        } catch (XmlPullParserException e) {
            throw new MojoExecutionException(
                "The LicenseFile is wrong, " +
                "cause we couldn't read it " + e);
        }
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isVerbose() {
        return verbose;
    }
}
