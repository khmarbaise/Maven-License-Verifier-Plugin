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
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.model.License;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;

import com.soebes.maven.plugins.mlv.model.LicenseItem;

/**
 * Generate a report about the license verifier report.
 *
 * @goal report
 * @author <a href="mailto:kama@soebes.de">Karl Heinz Marbaise</a>
 */
public class LicenseVerifierReport
        extends AbstractLicenseVerifierPlugIn
        implements MavenReport {

    /**
     * The directory where the report will be generated
     *
     * @parameter expression="${project.reporting.outputDirectory}" default-value="${project.reporting.outputDirectory}/lvp"
     * @required
     * @readonly
     */
    private File outputDirectory;
    /**
     * The directory where the report will be generated
     *
     * @parameter expression="${project.reporting.outputDirectory}" default-value="${project.reporting.outputDirectory}/lvp"
     * @required
     * @readonly
     */
    private File reportOutputDirectory;

    /**
     * @see org.apache.maven.reporting.AbstractMavenReport#executeReport(java.util.Locale)
     */
    protected void executeReport(Locale locale) throws MavenReportException {
        try {
            getLog().info("LicenseVerifierReport:executeReport()");
            RenderingContext context = new RenderingContext(getOutputDirectory(), getOutputName() + ".html");
            SiteRendererSink sink = new SiteRendererSink(context);
            generate(sink, locale);
        } catch (MavenReportException e) {
            getLog().error("An error has occurred in " + getName(Locale.ENGLISH)
                    + " report generation:" + e.getMessage(), e);
        } catch (RuntimeException e) {
            getLog().error(e.getMessage(), e);
        }
    }

    protected File getOutputDirectory() {
        if (!outputDirectory.isAbsolute()) {
            outputDirectory = new File(project.getBasedir(), outputDirectory.getPath());
        }
        //Create the folder structure.
        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        return outputDirectory.getAbsoluteFile();
    }

    protected MavenProject getProject() {
        return project;
    }

    public String getDescription(Locale locale) {
        return "Generated License Verifier Report";
    }

    public String getName(Locale locale) {
        return "License Verifier Report";
    }

    public String getOutputName() {
        return "licenseverifierreport";
    }

    /**
     * @see org.apache.maven.reporting.MavenReport#getOutputName()
     */
    protected ResourceBundle getBundle(Locale locale) {
        return ResourceBundle.getBundle(
                "mlvp",
                locale,
                this.getClass().getClassLoader());
    }

    /**
     * generates an empty report in case there are no sources to generate a report with
     *
     * @param bundle the resource bundle to retrieve report phrases from
     * @param sink   the report formatting tool
     */
    private void doGenerateEmptyReport(ResourceBundle bundle, Sink sink) {
        sink.head();
        sink.title();

        sink.text(bundle.getString("report.mlvp.header"));
        sink.title_();
        sink.head_();

        sink.body();
        sink.section1();

        sink.sectionTitle1();
        sink.text(bundle.getString("report.mlvp.mainTitle"));
        sink.sectionTitle1_();

        sink.paragraph();
        sink.text("No artifacts found to create and licesen verifier report.");
        sink.paragraph_();

        sink.section1_();

        sink.body_();
        sink.flush();
        sink.close();
    }

    private void doGenerateReport(ResourceBundle bundle, Sink sink) {

        sink.head();
        sink.title();

        sink.text(bundle.getString("report.mlvp.header"));
        sink.title_();
        sink.head_();

        sink.body();

        sink.section1();
        doGenerateLicenseCategories(bundle, sink);
        sink.section1_();

        sink.section1();
        doGenerateItemReport(bundle, sink);
        sink.section1_();

//The different scopes should be seen only if we have artifacts with an appropriately scope otherwise no headlines etc.
        if (hasScope(Artifact.SCOPE_COMPILE)) {
            sink.section1();
            doGenerateArtifactCategories(bundle, sink, Artifact.SCOPE_COMPILE);
            sink.section1_();
        }

        if (hasScope(Artifact.SCOPE_TEST)) {
            sink.section1();
            doGenerateArtifactCategories(bundle, sink, Artifact.SCOPE_TEST);
            sink.section1_();
        }

        if (hasScope(Artifact.SCOPE_PROVIDED)) {
            sink.section1();
            doGenerateArtifactCategories(bundle, sink, Artifact.SCOPE_PROVIDED);
            sink.section1_();
        }

        if (hasScope(Artifact.SCOPE_RUNTIME)) {
            sink.section1();
            doGenerateArtifactCategories(bundle, sink, Artifact.SCOPE_RUNTIME);
            sink.section1_();
        }

        sink.body_();
        sink.flush();
        sink.close();
    }

    private boolean hasScope(String scope) {
        boolean result = false;
        for (LicenseInformation item : getLicenseInformations()) {
            if (scope.equals(item.getArtifact().getScope())) {
                result = true;
            }
        }
        return result;
    }

//The following four methods are copy&past except the isXXXX() Think hard about that!!!
    private boolean hasValidEntries (String scope) {
        boolean hasEntries = false;
        for (LicenseInformation item : getLicenseInformations()) {
            if (scope.equals(item.getArtifact().getScope())) {
                if (licenseValidator.isValid(item.getLicenses())) {
                    hasEntries = true;
                }
            }
        }
        return hasEntries;
    }
    private boolean hasInvalidEntries (String scope) {
        boolean hasEntries = false;
        for (LicenseInformation item : getLicenseInformations()) {
            if (scope.equals(item.getArtifact().getScope())) {
                if (licenseValidator.isInvalid(item.getLicenses())) {
                    hasEntries = true;
                }
            }
        }
        return hasEntries;
    }
    private boolean hasWarningEntries (String scope) {
        boolean hasEntries = false;
        for (LicenseInformation item : getLicenseInformations()) {
            if (scope.equals(item.getArtifact().getScope())) {
                if (licenseValidator.isWarning(item.getLicenses())) {
                    hasEntries = true;
                }
            }
        }
        return hasEntries;
    }
    private boolean hasUnknownEntries (String scope) {
        boolean hasEntries = false;
        for (LicenseInformation item : getLicenseInformations()) {
            if (scope.equals(item.getArtifact().getScope())) {
                if (licenseValidator.isUnknown(item.getLicenses())) {
                    hasEntries = true;
                }
            }
        }
        return hasEntries;
    }


    private void doGenerateLicenseCategories(ResourceBundle bundle, Sink sink) {
//TODO: Use bundles instead of hard coded strings.
        sink.sectionTitle1();
        sink.text("License Categories (Configured)");
        sink.sectionTitle1_();

//TODO: Show only areas which contain items!
        doGenerateReportLicensesConfiguration(bundle, sink, "Valid", licenseValidator.getValid());
        doGenerateReportLicensesConfiguration(bundle, sink, "Warning", licenseValidator.getWarning());
        doGenerateReportLicensesConfiguration(bundle, sink, "Invalid", licenseValidator.getInvalid());

    }

    private void doGenerateArtifactCategories(ResourceBundle bundle, Sink sink, String scope) {
        sink.sectionTitle1();
        sink.text("Artifact Categories (" + scope + ")");
        sink.sectionTitle1_();

        if (hasValidEntries(scope)) {
            doGenerateValidReport(bundle, sink, scope);
        }
        if (hasWarningEntries(scope)) {
            doGenerateWarningReport(bundle, sink, scope);
        }
        if (hasInvalidEntries(scope)) {
            doGenerateInvalidReport(bundle, sink, scope);
        }
        if (hasUnknownEntries(scope)) {
            doGenerateUnknownReport(bundle, sink, scope);
        }
    }

    private void doGenerateReportLicensesConfiguration(ResourceBundle bundle, Sink sink, String header, List<LicenseItem> licenseItems) {
        sink.sectionTitle2();
        sink.text(header);
        sink.sectionTitle2_();

        sink.table();

        sink.tableRow();
        headerCell(sink, "Id");
        headerCell(sink, "Description");
        headerCell(sink, "Names");
        headerCell(sink, "URL's");
        sink.tableRow_();

        for (LicenseItem item : licenseItems) {
            sink.tableRow();
            cell(sink, item.getId());
            cell(sink, item.getDescription());

            sink.tableCell();
            //List all names
            sink.list();
            for (String itemName : item.getNames()) {
                sink.listItem();
                sink.text(itemName);
                sink.listItem_();
            }
            sink.list_();
            sink.tableCell_();

            sink.tableCell();
            sink.list();
            for (String itemURL : item.getUrls()) {
                sink.listItem();
                sink.link(itemURL);
                sink.text(itemURL);
                sink.link_();
                sink.listItem_();
            }
            sink.list_();
            sink.tableCell_();

            sink.tableRow_();
        }
        sink.table_();
    }

    private void createEmptyCell(Sink sink) {
        sink.tableCell();
        sink.tableCell_();
    }

    private void createFigure(Sink sink, boolean success) {
        sink.tableCell();
        sink.figure();
        if (success) {
            sink.figureGraphics("images/icon_success_sml.gif");
        } else {
            sink.figureGraphics("images/icon_warning_sml.gif");
        }
        sink.figure_();
        sink.tableCell_();
    }

    private void doGenerateItemReport(ResourceBundle bundle, Sink sink) {
        sink.sectionTitle1();
        sink.text("Artifact License Categories");
        sink.sectionTitle1_();

        sink.sectionTitle2();
        sink.text("Overview");
        sink.sectionTitle2_();

        sink.table();

        sink.tableRow();
        headerCell(sink, "Id");
        headerCell(sink, "Scope");
        headerCell(sink, "Valid");
        headerCell(sink, "Warning");
        headerCell(sink, "Invalid");
        headerCell(sink, "Unknown");
        sink.tableRow_();

        for (LicenseInformation item : getLicenseInformations()) {
            sink.tableRow();

            cell(sink, item.getArtifact().getId()); // 1st Row item artifactId

            boolean isValid = licenseValidator.isValid(item.getLicenses());
            boolean isWarning = licenseValidator.isWarning(item.getLicenses());
            boolean isInvalid = licenseValidator.isInvalid(item.getLicenses());
            boolean isUnknown = licenseValidator.isUnknown(item.getLicenses());

            sink.tableCell();
            sink.text(item.getArtifact().getScope());
            sink.tableCell_();

            createFigure(sink, isValid);

            if (isWarning) {
                createFigure(sink, false);
            } else {
                createEmptyCell(sink);
            }

            if (isInvalid) {
                createFigure(sink, false);
            } else {
                createEmptyCell(sink);
            }

            if (isUnknown) {
                createFigure(sink, false);
            } else {
                createEmptyCell(sink);
            }

            sink.tableRow_();
        }

        sink.table_();
    }

//FIXME: The following four methods are copy&past except for licenseValidate.isXXX() Think hard about this.!
    private void doGenerateValidReport(ResourceBundle bundle, Sink sink, String scope) {
        sink.sectionTitle2();
        sink.text("Artifacts Catagorized as Valid");
        sink.sectionTitle2_();

        sink.table();

        sink.tableRow();
        headerCell(sink, "Id");
        headerCell(sink, "Scope");
        headerCell(sink, "Name");
        headerCell(sink, "URL");
        sink.tableRow_();

        for (LicenseInformation item : getLicenseInformations()) {
            if (scope.equals(item.getArtifact().getScope())) {
                if (licenseValidator.isValid(item.getLicenses())) {
                    sink.tableRow();

                    cell(sink, item.getArtifact().getId()); // 1st Row item artifactId

                    sink.tableCell();
                    sink.text(item.getArtifact().getScope());
                    sink.tableCell_();

                    if (item.getLicenses().isEmpty()) {
                        cell(sink, "");
                        cell(sink, "");
                    } else {
                        for (License license : item.getLicenses()) {
                            cell(sink, license.getName());
                            cell(sink, license.getUrl());
                        }
                    }
                    sink.tableRow_();
                }
            }
        }

        sink.table_();
    }

    private void doGenerateWarningReport(ResourceBundle bundle, Sink sink, String scope) {
        sink.sectionTitle2();
        sink.text("Artifacts Catagorized as Warning");
        sink.sectionTitle2_();

        sink.table();

        sink.tableRow();
        headerCell(sink, "Id");
        headerCell(sink, "Scope");
        headerCell(sink, "Name");
        headerCell(sink, "URL");
        sink.tableRow_();

        for (LicenseInformation item : getLicenseInformations()) {
            if (scope.equals(item.getArtifact().getScope())) {
                if (licenseValidator.isWarning(item.getLicenses())) {
                    sink.tableRow();

                    cell(sink, item.getArtifact().getId()); // 1st Row item artifactId
                    sink.tableCell();
                    sink.text(item.getArtifact().getScope());
                    sink.tableCell_();

                    if (item.getLicenses().isEmpty()) {
                        cell(sink, "");
                        cell(sink, "");
                    } else {
                        for (License license : item.getLicenses()) {
                            cell(sink, license.getName());
                            cell(sink, license.getUrl());
                        }
                    }
                    sink.tableRow_();
                }
            }
        }

        sink.table_();
    }

    private void doGenerateInvalidReport(ResourceBundle bundle, Sink sink, String scope) {
        sink.sectionTitle2();
        sink.text("Artifacts Catagorized as Invalid");
        sink.sectionTitle2_();

        sink.table();

        sink.tableRow();
        headerCell(sink, "Id");
        headerCell(sink, "Scope");
        headerCell(sink, "Name");
        headerCell(sink, "URL");
        sink.tableRow_();

        for (LicenseInformation item : getLicenseInformations()) {
            //Should be made better...
            if (scope.equals(item.getArtifact().getScope())) {
                if (licenseValidator.isInvalid(item.getLicenses())) {
                    sink.tableRow();

                    cell(sink, item.getArtifact().getId()); // 1st Row item artifactId
                    sink.tableCell();
                    sink.text(item.getArtifact().getScope());
                    sink.tableCell_();

                    if (item.getLicenses().isEmpty()) {
                        cell(sink, "");
                        cell(sink, "");
                    } else {
                        for (License license : item.getLicenses()) {
                            cell(sink, license.getName());
                            cell(sink, license.getUrl());
                        }
                    }
                    sink.tableRow_();
                }
            }
        }

        sink.table_();
    }

    private void doGenerateUnknownReport(ResourceBundle bundle, Sink sink, String scope) {
        sink.sectionTitle2();
        sink.text("Artifacts Catagorized as Unknown");
        sink.sectionTitle2_();

        sink.table();

        sink.tableRow();
        headerCell(sink, "Id");
        headerCell(sink, "Scope");
        headerCell(sink, "Name");
        headerCell(sink, "URL");
        sink.tableRow_();

        for (LicenseInformation item : getLicenseInformations()) {
            if (scope.equals(item.getArtifact().getScope())) {
                if (licenseValidator.isUnknown(item.getLicenses())) {
                    sink.tableRow();

                    cell(sink, item.getArtifact().getId()); // 1st Row item artifactId
                    sink.tableCell();
                    sink.text(item.getArtifact().getScope());
                    sink.tableCell_();

                    if (item.getLicenses().isEmpty()) {
                        cell(sink, "");
                        cell(sink, "");
                    } else {
                        for (License license : item.getLicenses()) {
                            cell(sink, license.getName());
                            cell(sink, license.getUrl());
                        }
                    }
                    sink.tableRow_();
                }
            }
        }

        sink.table_();
    }

    private void headerCell(Sink sink, String text) {
        sink.tableHeaderCell();
        sink.text(text);
        sink.tableHeaderCell_();
    }

    private void cell(Sink sink, String text) {
        sink.tableCell();
        sink.text(text);
        sink.tableCell_();
    }

    public void setProject(MavenProject project) {
        this.project = project;
    }

    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }

    public boolean canGenerateReport() {
        return true;
    }

    public void generate(Sink sink, Locale locale) throws MavenReportException {
        getLog().info("LicenseVerifierReport:generate()");
        if (project.getArtifacts().isEmpty()) {
            //If we have no artifacts at all.
            doGenerateEmptyReport(getBundle(locale), sink);
        } else {
            try {
                loadLicensesFile();
            } catch (MojoExecutionException e1) {
                throw new MavenReportException("Failure during load of the license.xml file", e1);
            }

            //Get a set with all dependent artifacts incl.
            //the transitive dependencies.
            Set depArtifacts = project.getArtifacts();

            //Get all the informations about the licenses of the artifacts.
            try {
                getDependArtifacts(depArtifacts);

                //Sorting the artifacts...
                Collections.sort(getLicenseInformations(), new ArtifactComperator());
                doGenerateReport(getBundle(locale), sink);

            } catch (MojoExecutionException e) {
                getLog().error("Something wrong (BETTER MESSAGE): ", e);
            }
        }
    }

    public String getCategoryName() {
        return MavenReport.CATEGORY_PROJECT_REPORTS;
    }

    public File getReportOutputDirectory() {
        return reportOutputDirectory;
    }

    public boolean isExternalReport() {
        return false;
    }

    public void setReportOutputDirectory(File outputDirectory) {
        this.reportOutputDirectory = outputDirectory;

    }

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            getLog().info("LicenseVerifierReport:execute()");
            RenderingContext context = new RenderingContext(getOutputDirectory(), getOutputName() + ".html");
            SiteRendererSink sink = new SiteRendererSink(context);
            Locale locale = Locale.getDefault();
            generate(sink, locale);
        } catch (MavenReportException e) {
            getLog().error("An error has occurred in " + getName(Locale.ENGLISH)
                    + " report generation:" + e.getMessage(), e);
        } catch (RuntimeException e) {
            getLog().error(e.getMessage(), e);
        }
    }
}
