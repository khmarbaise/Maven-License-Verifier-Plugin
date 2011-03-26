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
