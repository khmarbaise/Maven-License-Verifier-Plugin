package com.soebes.maven.plugins.mlv;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;

import com.soebes.maven.plugins.mlv.filter.PatternExcludeFilter;
import com.soebes.maven.plugins.mlv.licenses.LicenseValidator;

public class LicenseData {
    private ArrayList<LicenseInformation> licenseInformations;
    private ArrayList<LicenseInformation> valid;
    private ArrayList<LicenseInformation> invalid;
    private ArrayList<LicenseInformation> warning;
    private ArrayList<LicenseInformation> unknown;
    
    private List<String> excludes;
    private ArrayList<LicenseInformation> excludedByConfiguration; //Check this?
    
    private LicenseValidator licenseValidator;

    public LicenseData(LicenseValidator licenseValidaor, ArrayList<LicenseInformation> licenseInformations, List<String> excludes) {
        setLicenseInformations(licenseInformations);
        setLicenseValidator(licenseValidaor);
        setValid(new ArrayList<LicenseInformation>());
        setInvalid(new ArrayList<LicenseInformation>());
        setWarning(new ArrayList<LicenseInformation>());
        setUnknown(new ArrayList<LicenseInformation>());
        setExcludedByConfiguration(new ArrayList<LicenseInformation>());
        setExcludes(excludes);
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

    private void categorize() {
        PatternExcludeFilter patternExcludeFilter = new PatternExcludeFilter();
        ArtifactFilter filter = patternExcludeFilter.createFilter(getExcludes());

        for (LicenseInformation license : getLicenseInformations()) {
            if (getLicenseValidator().isValid(license.getLicenses())) {
                getValid().add(license);
            } else if (getLicenseValidator().isInvalid(license.getLicenses())) {
                getInvalid().add(license);
            } else if (getLicenseValidator().isWarning(license.getLicenses())) {
                getWarning().add(license);
            } else if (getLicenseValidator().isUnknown(license.getLicenses())) {
                getUnknown().add(license);
            } else if (!filter.include(license.getArtifact())) {
                getExcludedByConfiguration().add(license);
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

    public void setLicenseInformations(ArrayList<LicenseInformation> licenseInformations) {
        this.licenseInformations = licenseInformations;
    }

    public ArrayList<LicenseInformation> getLicenseInformations() {
        return licenseInformations;
    }

    public void setLicenseValidator(LicenseValidator licenseValidator) {
        this.licenseValidator = licenseValidator;
    }

    public LicenseValidator getLicenseValidator() {
        return licenseValidator;
    }

    public void setValid(ArrayList<LicenseInformation> valid) {
        this.valid = valid;
    }

    public ArrayList<LicenseInformation> getValid() {
        return valid;
    }

    public void setInvalid(ArrayList<LicenseInformation> invalid) {
        this.invalid = invalid;
    }

    public ArrayList<LicenseInformation> getInvalid() {
        return invalid;
    }

    public void setWarning(ArrayList<LicenseInformation> warning) {
        this.warning = warning;
    }

    public ArrayList<LicenseInformation> getWarning() {
        return warning;
    }

    public void setUnknown(ArrayList<LicenseInformation> unknown) {
        this.unknown = unknown;
    }

    public ArrayList<LicenseInformation> getUnknown() {
        return unknown;
    }

    public void setExcludedByConfiguration(ArrayList<LicenseInformation> excludedByConfiguration) {
        this.excludedByConfiguration = excludedByConfiguration;
    }

    public ArrayList<LicenseInformation> getExcludedByConfiguration() {
        return excludedByConfiguration;
    }


    public void setExcludes(List<String> excludes) {
        this.excludes = excludes;
    }


    public List<String> getExcludes() {
        return excludes;
    }
    
}
