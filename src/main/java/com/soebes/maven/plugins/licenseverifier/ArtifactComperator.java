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
package com.soebes.maven.plugins.licenseverifier;

import java.util.Comparator;

import com.soebes.maven.plugins.licenseverifier.licenses.LicenseInformation;

/**
 * The comparator for the artifacts.
 *
 * @author <a href="mailto:kama@soebes.de">Karl Heinz Marbaise</a>
 *
 */
public class ArtifactComperator implements Comparator<LicenseInformation> {

    public int compare(LicenseInformation o1, LicenseInformation o2) {
        int compareScope = o1.getArtifact().getScope().compareTo(o2.getArtifact().getScope());
        if (compareScope == 0) {
            return o1.getArtifact().getId().compareTo(o2.getArtifact().getId());
        }
        return compareScope;
    }

}
