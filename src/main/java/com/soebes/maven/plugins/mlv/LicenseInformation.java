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

import java.util.ArrayList;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.License;
import org.apache.maven.project.MavenProject;

/**
 * This class will hold together the information
 * about <b>Project</b>, <b>artifact</b> and the
 * <b>Licenses</b>.
 * @author Karl Heinz Marbaise
 *
 */
public class LicenseInformation {

	private Artifact artifact;
	private MavenProject project;
	private ArrayList<License> licenses = new ArrayList<License>();

	public void setArtifact(Artifact artifact) {
		this.artifact = artifact;
	}
	public Artifact getArtifact() {
		return artifact;
	}
	public void setLicenses(ArrayList<License> licenses) {
		this.licenses = licenses;
	}
	public ArrayList<License> getLicenses() {
		return licenses;
	}
	
	public void addLicense(License license) {
		getLicenses().add(license);
	}
	public void setProject(MavenProject project) {
		this.project = project;
	}
	public MavenProject getProject() {
		return project;
	}
}
