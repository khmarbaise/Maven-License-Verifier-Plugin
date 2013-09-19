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
package com.soebes.maven.plugins.mlv.licenses;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.plugin.logging.Log;

import com.soebes.maven.plugins.mlv.filter.PatternExcludeFilter;

/**
 * @author <a href="mailto:kama@soebes.de">Karl Heinz Marbaise</a>
 *
 * @since 0.5
 */
public class LicenseData {
    private List<LicenseInformation> licenseInformations;
    private List<LicenseInformation> valid;
    private List<LicenseInformation> invalid;
    private List<LicenseInformation> warning;
    private List<LicenseInformation> unknown;
    private List<LicenseInformation> excludedByConfiguration;

    private List<String> excludes;

    /**
     * The instance of the logging framework to
     * log within this in the default Maven way.
     */
    private Log log;

    private LicenseValidator licenseValidator;

    public LicenseData(LicenseValidator licenseValidaor, List<LicenseInformation> licenseInformations, List<String> excludes, Log log) {
        setLicenseInformations(licenseInformations);
        setLicenseValidator(licenseValidaor);
        setValid(new ArrayList<LicenseInformation>());
        setInvalid(new ArrayList<LicenseInformation>());
        setWarning(new ArrayList<LicenseInformation>());
        setUnknown(new ArrayList<LicenseInformation>());
        setExcludedByConfiguration(new ArrayList<LicenseInformation>());
        setExcludes(excludes);
        setLog(log);
        categorize();
    }


    public boolean hasArtifactsByScope(String scope) {
        boolean result = false;
        if (hasValidByScope(scope)) {
            result = true;
        }
        if (hasInvalidByScope(scope)) {
            result = true;
        }
        if (hasWarningByScope(scope)) {
            result = true;
        }
        if (hasUnknwonByScope(scope)) {
            result = true;
        }
        return result;
    }

    /**
     * This will check if we have artifacts which
     * is available in the given scope.
     * @param scope The scope we would like to check for Artifact.SCOPE...
     * @return true if found false otherwise.
     */
    public boolean hasValidByScope(String scope) {
        boolean result = false;
        for (LicenseInformation information : getValid()) {
            if (information.getArtifact().getScope().equals(scope)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * This will get a list of licenses which are in the category
     * <b>Valid</b> and in the given scope.
     * @param scope The scope you would like to check.
     * @return The list of items which are in the given scope and
     *   category. In case if there is no item in that category
     *   and scope you will get an empty list.
     */
    public List<LicenseInformation> getValidByScope(String scope) {
        ArrayList<LicenseInformation> result = new ArrayList<LicenseInformation>();
        for (LicenseInformation information : getValid()) {
            if (information.getArtifact().getScope().equals(scope)) {
                result.add(information);
            }
        }
        return result;
    }


    /**
     * This will check if we have artifacts which are
     * in the category <b>Invalid</b> and in the given scope.
     * @param scope The scope we would like to check for Artifact.SCOPE...
     * @return true if found false otherwise.
     */
    public boolean hasInvalidByScope(String scope) {
        boolean result = false;
        for (LicenseInformation information : getInvalid()) {
            if (information.getArtifact().getScope().equals(scope)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * This will get a list of licenses which are in the category
     * <b>Invalid</b> and in the given scope.
     * @param scope The scope you would like to check.
     * @return The list of items which are in the given scope and
     *   category. In case if there is no item in that category
     *   and scope you will get an empty list.
     */
    public List<LicenseInformation> getInvalidByScope(String scope) {
        ArrayList<LicenseInformation> result = new ArrayList<LicenseInformation>();
        for (LicenseInformation information : getInvalid()) {
            if (information.getArtifact().getScope().equals(scope)) {
                result.add(information);
            }
        }
        return result;
    }

    /**
     * This will check if we have artifacts which are
     * in the category <b>Warning</b> and in the given scope.
     * @param scope The scope we would like to check for Artifact.SCOPE...
     * @return true if found false otherwise.
     */
    public boolean hasWarningByScope(String scope) {
        boolean result = false;
        for (LicenseInformation information : getWarning()) {
            if (information.getArtifact().getScope().equals(scope)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * This will get a list of licenses which are in the category
     * <b>Warning</b> and in the given scope.
     * @param scope The scope you would like to check.
     * @return The list of items which are in the given scope and
     *   category. In case if there is no item in that category
     *   and scope you will get an empty list.
     */
    public List<LicenseInformation> getWarningByScope(String scope) {
        ArrayList<LicenseInformation> result = new ArrayList<LicenseInformation>();
        for (LicenseInformation information : getWarning()) {
            if (information.getArtifact().getScope().equals(scope)) {
                result.add(information);
            }
        }
        return result;
    }

    /**
     * This will check if we have artifacts which are
     * in the category <b>Unknown</b> and in the given scope.
     * @param scope The scope we would like to check for Artifact.SCOPE...
     * @return true if found false otherwise.
     */
    public boolean hasUnknwonByScope(String scope) {
        boolean result = false;
        for (LicenseInformation information : getUnknown()) {
            if (information.getArtifact().getScope().equals(scope)) {
                result = true;
            }
        }
        return result;
    }

    /**
     * This will get a list of licenses which are in the category
     * <b>Unknown</b> and in the given scope.
     * @param scope The scope you would like to check.
     * @return The list of items which are in the given scope and
     *   category. In case if there is no item in that category
     *   and scope you will get an empty list.
     */
    public List<LicenseInformation> getUnknownByScope(String scope) {
        ArrayList<LicenseInformation> result = new ArrayList<LicenseInformation>();
        for (LicenseInformation information : getUnknown()) {
            if (information.getArtifact().getScope().equals(scope)) {
                result.add(information);
            }
        }
        return result;
    }

    /**
     * This will give you a list with all artifacts without the
     * excluded artifacts.
     * @return List with artifacts. Never null.
     */
    public List<LicenseInformation> getAllWithoutExcluded() {
        ArrayList<LicenseInformation> result = new ArrayList<LicenseInformation>();

        result.addAll(getValid());
        result.addAll(getInvalid());
        result.addAll(getWarning());
        result.addAll(getUnknown());
        return result;
    }

    /**
     * This will go through all licenses and categorize them
     * into their appropriate group.
     *
     */
    private void categorize() {
        PatternExcludeFilter patternExcludeFilter = new PatternExcludeFilter();
        ArtifactFilter filter = patternExcludeFilter.createFilter(getExcludes());

        for (LicenseInformation license : getLicenseInformations()) {
            if (!filter.include(license.getArtifact())) {
                getExcludedByConfiguration().add(license);
                getLog().debug("artifact " + license.getArtifact().getId() + " exculded by configuration.");
                continue;
            }

            if (getLicenseValidator().isValid(license.getLicenses())) {
                getValid().add(license);
                getLog().debug("artifact " + license.getArtifact().getId() + " has been categorized as Valid.");
            } else if (getLicenseValidator().isInvalid(license.getLicenses())) {
                getInvalid().add(license);
                getLog().debug("artifact " + license.getArtifact().getId() + " has been categorized as Invalid.");
            } else if (getLicenseValidator().isWarning(license.getLicenses())) {
                getWarning().add(license);
                getLog().debug("artifact " + license.getArtifact().getId() + " has been categorized as Warning.");
            } else if (getLicenseValidator().isUnknown(license.getLicenses())) {
                getUnknown().add(license);
                getLog().debug("artifact " + license.getArtifact().getId() + " has been categorized as Unknown.");
            }
        }
    }

    public boolean hasValid() {
        return !getValid().isEmpty();
    }
    public boolean hasInvalid() {
        return !getInvalid().isEmpty();
    }
    public boolean hasWarning() {
        return !getWarning().isEmpty();
    }
    public boolean hasUnknown() {
        return !getUnknown().isEmpty();
    }

    public boolean hasExcludedByConfiguration() {
        return !getExcludedByConfiguration().isEmpty();
    }

    public void setLicenseInformations(List<LicenseInformation> licenseInformations) {
        this.licenseInformations = licenseInformations;
    }

    public List<LicenseInformation> getLicenseInformations() {
        return licenseInformations;
    }

    public void setLicenseValidator(LicenseValidator licenseValidator) {
        this.licenseValidator = licenseValidator;
    }

    public LicenseValidator getLicenseValidator() {
        return licenseValidator;
    }

    public void setValid(List<LicenseInformation> valid) {
        this.valid = valid;
    }

    public List<LicenseInformation> getValid() {
        return valid;
    }

    public void setInvalid(List<LicenseInformation> invalid) {
        this.invalid = invalid;
    }

    public List<LicenseInformation> getInvalid() {
        return invalid;
    }

    public void setWarning(ArrayList<LicenseInformation> warning) {
        this.warning = warning;
    }

    public List<LicenseInformation> getWarning() {
        return warning;
    }

    public void setUnknown(List<LicenseInformation> unknown) {
        this.unknown = unknown;
    }

    public List<LicenseInformation> getUnknown() {
        return unknown;
    }

    public void setExcludedByConfiguration(ArrayList<LicenseInformation> excludedByConfiguration) {
        this.excludedByConfiguration = excludedByConfiguration;
    }

    public List<LicenseInformation> getExcludedByConfiguration() {
        return excludedByConfiguration;
    }


    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }


    public List<String> getExcludes() {
        return excludes;
    }


    public void setLog(Log log) {
        this.log = log;
    }


    public Log getLog() {
        return log;
    }

}
