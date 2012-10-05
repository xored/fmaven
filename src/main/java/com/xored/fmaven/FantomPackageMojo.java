package com.xored.fmaven;

import java.io.File;

/**
 * @goal package
 * @threadSafe
 * @phase package
 */
public class FantomPackageMojo extends FatomMojo {

	@Override
	protected void doExecute() {
		File destination = new File(fanOutputDir, String.format("%s.pod",
				project.getArtifactId()));
		project.getArtifact().setFile(destination);
	}
}
