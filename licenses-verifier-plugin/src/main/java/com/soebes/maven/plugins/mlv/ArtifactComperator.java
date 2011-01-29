package com.soebes.maven.plugins.mlv;

import java.util.Comparator;

public class ArtifactComperator implements Comparator<LicenseInformation> {

    public int compare(LicenseInformation o1, LicenseInformation o2) {
        int compareScope = o1.getArtifact().getScope().compareTo(o2.getArtifact().getScope());
        if (compareScope == 0) {
            return o1.getArtifact().getId().compareTo(o2.getArtifact().getId());
        }
        return compareScope;
    }

}
