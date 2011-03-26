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
package com.soebes.maven.plugins.mlv.filter;

import java.util.List;

import org.apache.maven.artifact.resolver.filter.ArtifactFilter;
import org.apache.maven.shared.artifact.filter.PatternExcludesArtifactFilter;

public class PatternExcludeFilter {
    public ArtifactFilter createFilter(List<String> patterns) {
        return new PatternExcludesArtifactFilter(patterns);
    }

    public ArtifactFilter createFilter(List<String> patterns, boolean actTransitively) {
        return new PatternExcludesArtifactFilter(patterns, actTransitively);
    }
}
