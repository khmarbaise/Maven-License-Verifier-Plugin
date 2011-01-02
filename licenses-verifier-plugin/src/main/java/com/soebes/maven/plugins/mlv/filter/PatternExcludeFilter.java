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
