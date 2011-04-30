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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.repository.ArtifactRepository;
import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.model.License;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.MavenProjectBuilder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import com.soebes.maven.plugins.mlv.filter.PatternExcludeFilter;
import com.soebes.maven.plugins.mlv.licenses.LicenseValidator;
import com.soebes.maven.plugins.mlv.licenses.LicensesFile;
import com.soebes.maven.plugins.mlv.model.LicensesContainer;

/**
 * @author Karl Heinz Marbaise
 * @requiresDependencyResolution test
 */
public abstract class AbstractLicenseVerifierPlugIn
    extends AbstractMojo
{
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
     * @parameter expression="${verbose}" default-value="false"
     */
    private boolean verbose;

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

    /**
     * This is just for simplicity to store all license information
     * here to make the checkings and other operation simpler.
     */
    private ArrayList<LicenseInformation> licenseInformations = new ArrayList<LicenseInformation>();

    private HashMap<String, LicenseInformation> licenseList = new HashMap<String, LicenseInformation>();

    protected LicenseValidator licenseValidator = null;
    protected LicensesContainer licensesContainer = null;

    /**
     * Get all their dependencies and put the information
     * into the intermediate list of licenses.
     *
     * @param depArtifacts
     * @throws MojoExecutionException
     */
    protected void getDependArtifacts(Set<?> depArtifacts)
            throws MojoExecutionException {

        PatternExcludeFilter patternExcludeFilter = new PatternExcludeFilter();
        ArtifactFilter filter = patternExcludeFilter.createFilter(excludes);

        for (Iterator depArtIter = depArtifacts.iterator(); depArtIter.hasNext(); ) {
           Artifact depArt = (Artifact) depArtIter.next();

           if (!filter.include(depArt)) {
               if (isVerbose()) {
                   getLog().warn("The artifact: " + depArt.getId() + " has been execluded by the configuration.");
               }
               continue;
           }

           LicenseInformation li = new LicenseInformation();

           // Here we access to the files so we can check if they contain LICENSE file etc. (check for JAR file reading? Other file types ? )
//           if (verbose) {
//               getLog().info("Artifact file:" + depArt.getFile().getAbsolutePath());
//           }

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

           // depArt.getScope() => Can be use eventually
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
    }

    public HashMap<String, LicenseInformation> getLicenseList() {
        return licenseList;
    }

    public ArrayList<LicenseInformation> getLicenseInformations() {
        return licenseInformations;
    }

    /**
     * This method will load the licenses.xml file.
     *
     * @throws MojoExecutionException
     */
    protected void loadLicensesFile() throws MojoExecutionException {
        if (licenseFile == null)
        {
            //If no licenseFile configuration given we will end.
            return;
        }
        try {
            getLog().info("Loading " + licenseFile.getAbsolutePath() + " licenses file.");
            licensesContainer = LicensesFile.getLicenses(licenseFile);
            licenseValidator = new LicenseValidator(licensesContainer);
        } catch (IOException e) {
            //Use the internal licenses.xml file.???
            throw new MojoExecutionException(
                "The LicenseFile configuration is wrong, " +
                "cause we couldn't find the " + licenseFile.getAbsolutePath());
        } catch (XmlPullParserException e) {
            //Use the internal licenses.xml file.???
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
