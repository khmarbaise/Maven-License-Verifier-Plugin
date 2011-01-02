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

import java.io.File;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.maven.doxia.module.xhtml.decoration.render.RenderingContext;
import org.apache.maven.doxia.siterenderer.sink.SiteRendererSink;
import org.apache.maven.model.License;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.reporting.MavenReport;
import org.apache.maven.reporting.MavenReportException;
import org.codehaus.doxia.sink.Sink;

/**
 * Generate a report about the license verifier report.
 *
 * @goal report
 * @author Karl Heinz Marbaise
 */
public class LicenseVerifierReport 
	extends AbstractLicenseVerifierPlugIn 
	implements MavenReport
	{

    /**
     * The Maven Project Object
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

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
			this.getClass().getClassLoader()
		);
	}
	
    /**
     * generates an empty report in case there are no sources to generate a report with
     *
     * @param bundle the resource bundle to retrieve report phrases from
     * @param sink   the report formatting tool
     */
    private void doGenerateEmptyReport( ResourceBundle bundle, Sink sink )
    {
        sink.head();
        sink.title();
        
        sink.text( bundle.getString( "report.mlvp.header" ) );
        sink.title_();
        sink.head_();

        sink.body();
        sink.section1();

        sink.sectionTitle1();
        sink.text( bundle.getString( "report.mlvp.mainTitle" ) );
        sink.sectionTitle1_();

        sink.paragraph();
        sink.text( "No artifacts found to create and licesen verifier report." );
        sink.paragraph_();

        sink.section1_();

        sink.body_();
        sink.flush();
        sink.close();
    }

    private void doGenerateReport (ResourceBundle bundle, Sink sink ) {

        sink.head();
        sink.title();

        sink.text( bundle.getString( "report.mlvp.header" ) );
        sink.title_();
        sink.head_();

        sink.body();

        sink.section1();
        doGenerateSummaryReport(bundle, sink);
        sink.section1_();
        
        sink.section1();
        doGenerateItemReport(bundle, sink);
        sink.section1_();
        
        sink.section1();
        doGenerateUnknownReport(bundle, sink);
        sink.section1_();

        sink.body_();
        sink.flush();
        sink.close();
    }

    private void doGenerateSummaryReport (ResourceBundle bundle, Sink sink ) {
    	HashMap<String, LicenseInformation> licenseList = getLicenseList();

    	sink.sectionTitle1();
        sink.text( bundle.getString( "report.mlvp.mainTitle" ) );
        sink.sectionTitle1_();

        sink.table();

    	for (Map.Entry<String, LicenseInformation> item : licenseList.entrySet()) {
    		if (item.getValue().getLicenses().size() > 0) {
            	sink.tableRow();
		        	cell(sink, item.getValue().getLicenses().get(0).getName());
		        	cell(sink, item.getValue().getLicenses().get(0).getUrl());
	        	sink.tableRow_();
    		}
		}

        sink.table_();
    }

    private void doGenerateItemReport(ResourceBundle bundle, Sink sink ) {
    	sink.sectionTitle1();
        sink.text( "Artifact License Categories" );
        sink.sectionTitle1_();

        sink.table();

        sink.tableRow();
        headerCell(sink, "Id");
        headerCell(sink, "Valid");
        headerCell(sink, "Warning");
        headerCell(sink, "Invalid");
        headerCell(sink, "Unknown");
        sink.tableRow_();
        
		for (LicenseInformation item : getLicenseInformations()) {
        	sink.tableRow();

       		cell(sink, item.getArtifact().getId()); // 1st Row item artifactId

        	if (licenseValidator.isValid(item.getLicenses())) {
        		cell(sink, "Yes"); // 2nd column isValid (Yes, No)
			} else {
        		cell(sink, "No"); // 2nd column isValid (Yes, No)
			}
        	if (licenseValidator.isWarning(item.getLicenses())) {
        		cell(sink, "Yes"); // 3rd column isValid (Yes, No)
			} else {
        		cell(sink, "No"); // 3rd column isValid (Yes, No)
			}
        	if (licenseValidator.isInvalid(item.getLicenses())) {
        		cell(sink, "Yes"); // 4th column isValid (Yes, No)
			} else {
        		cell(sink, "No"); // 4th column isValid (Yes, No)
			}
        	if (licenseValidator.isUnknown(item.getLicenses())) {
        		cell(sink, "Yes"); // 5th column isValid (Yes, No)
			} else {
        		cell(sink, "No"); // 5th  column isValid (Yes, No)
			}

        	sink.tableRow_();
		}

        sink.table_();
    }

    private void doGenerateUnknownReport(ResourceBundle bundle, Sink sink ) {
    	sink.sectionTitle1();
        sink.text( "Artifacts Catagorized as Unknown" );
        sink.sectionTitle1_();

        sink.table();

        sink.tableRow();
        headerCell(sink, "Id");
        headerCell(sink, "Name");
        headerCell(sink, "URL");
        sink.tableRow_();
        
		for (LicenseInformation item : getLicenseInformations()) {
        	if (licenseValidator.isUnknown(item.getLicenses())) {
            	sink.tableRow();

           		cell(sink, item.getArtifact().getId()); // 1st Row item artifactId

        		if (item.getLicenses().isEmpty()) {
        			cell(sink,"");
        			cell(sink,"");
        		} else {
        			for (License license : item.getLicenses()) {
        				cell(sink, license.getName());
        				cell(sink, license.getUrl());
        			}
        		}
            	sink.tableRow_();
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

    /**
     * method that generates the report for this mojo. This method is overridden by dev-activity and file-activity mojo
     *
     * @param changeLogSets changed sets to generate the report from
     * @param bundle        the resource bundle to retrieve report phrases from
     * @param sink          the report formatting tool
     */
//    protected void doGenerateReport( List changeLogSets, ResourceBundle bundle, Sink sink )
//    {
//        sink.head();
//        sink.title();
//        sink.text( bundle.getString( "report.changelog.header" ) );
//        sink.title_();
//        sink.head_();
//
//        sink.body();
//        sink.section1();
//
//        sink.sectionTitle1();
//        sink.text( bundle.getString( "report.changelog.mainTitle" ) );
//        sink.sectionTitle1_();
//
//        // Summary section
//        doSummarySection( changeLogSets, bundle, sink );
//
//        for ( Iterator sets = changeLogSets.iterator(); sets.hasNext(); )
//        {
//            ChangeLogSet changeLogSet = (ChangeLogSet) sets.next();
//
//            doChangedSet( changeLogSet, bundle, sink );
//        }
//
//        sink.section1_();
//        sink.body_();
//
//        sink.flush();
//        sink.close();
//    }
//
//    /**
//     * generates the report summary section of the report
//     *
//     * @param changeLogSets changed sets to generate the report from
//     * @param bundle        the resource bundle to retrieve report phrases from
//     * @param sink          the report formatting tool
//     */
//    private void doSummarySection( List changeLogSets, ResourceBundle bundle, Sink sink )
//    {
//        sink.paragraph();
//
//        sink.text( bundle.getString( "report.changelog.ChangedSetsTotal" ) );
//        sink.text( ": " + changeLogSets.size() );
//
//        sink.paragraph_();
//    }
//
//    /**
//     * generates a section of the report referring to a changeset
//     *
//     * @param set    the current ChangeSet to generate this section of the report
//     * @param bundle the resource bundle to retrieve report phrases from
//     * @param sink   the report formatting tool
//     */
//    private void doChangedSet( ChangeLogSet set, ResourceBundle bundle, Sink sink )
//    {
//        sink.section1();
//
//        doChangeSetTitle( set, bundle, sink );
//
//        doSummary( set, bundle, sink );
//
//        doChangedSetTable( set.getChangeSets(), bundle, sink );
//
//        sink.section1_();
//    }
//
//    /**
//     * Generate the title for the report.
//     *
//     * @param set    change set to generate the report from
//     * @param bundle the resource bundle to retrieve report phrases from
//     * @param sink   the report formatting tool
//     */
//    protected void doChangeSetTitle( ChangeLogSet set, ResourceBundle bundle, Sink sink )
//    {
//        sink.sectionTitle2();
//
//        SimpleDateFormat headingDateFormater = new SimpleDateFormat( headingDateFormat );
//
//        if ( "tag".equals( type ) )
//        {
//            if ( set.getStartVersion() == null || set.getStartVersion().getName() == null )
//            {
//                sink.text( bundle.getString( "report.SetTagCreation" ) );
//            }
//            else if ( set.getEndVersion() == null || set.getEndVersion().getName() == null )
//            {
//                sink.text( bundle.getString( "report.SetTagSince" ) );
//                sink.text( " '" + set.getStartVersion() + "'" );
//            }
//            else
//            {
//                sink.text( bundle.getString( "report.SetTagBetween" ) );
//                sink.text( " '" + set.getStartVersion() + "' " + bundle.getString( "report.And" ) + " '"
//                    + set.getEndVersion() + "'" );
//            }
//        }
//        else  if ( set.getStartDate() == null )
//        {
//            sink.text( bundle.getString( "report.SetRangeUnknown" ) );
//        }
//        else if ( set.getEndDate() == null )
//        {
//            sink.text( bundle.getString( "report.SetRangeSince" ) );
//            sink.text( " " + headingDateFormater.format( set.getStartDate() ) );
//        }
//        else
//        {
//            sink.text( bundle.getString( "report.SetRangeBetween" ) );
//            sink.text( " " + headingDateFormater.format( set.getStartDate() )
//                + " " + bundle.getString( "report.And" ) + " "
//                + headingDateFormater.format( set.getEndDate() ) );
//        }
//        sink.sectionTitle2_();
//    }
//
//    /**
//     * Generate the summary section of the report.
//     *
//     * @param set    change set to generate the report from
//     * @param bundle the resource bundle to retrieve report phrases from
//     * @param sink   the report formatting tool
//     */
//    protected void doSummary( ChangeLogSet set, ResourceBundle bundle, Sink sink )
//    {
//        sink.paragraph();
//        sink.text( bundle.getString( "report.TotalCommits" ) );
//        sink.text( ": " + set.getChangeSets().size() );
//        sink.lineBreak();
//        sink.text( bundle.getString( "report.changelog.FilesChanged" ) );
//        sink.text( ": " + countFilesChanged( set.getChangeSets() ) );
//        sink.paragraph_();
//    }

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
        try
        {
			getLog().info("LicenseVerifierReport:execute()");
            RenderingContext context = new RenderingContext( getOutputDirectory(), getOutputName() + ".html" );
            SiteRendererSink sink = new SiteRendererSink( context );
            Locale locale = Locale.getDefault();
            generate(sink, locale);
        }
        catch ( MavenReportException e )
        {
            getLog().error( "An error has occurred in " + getName( Locale.ENGLISH )
                    + " report generation:" + e.getMessage(), e );
        }
        catch ( RuntimeException e )
        {
            getLog().error( e.getMessage(), e );
        }
	}

}
