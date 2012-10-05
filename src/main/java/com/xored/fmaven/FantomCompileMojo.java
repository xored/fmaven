package com.xored.fmaven;

import static com.xored.fmaven.compiler.CompileStatus.ERROR;
import static com.xored.fmaven.compiler.CompileStatus.SUCCESSFUL;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.codehaus.plexus.util.FileUtils;

import com.xored.fmaven.compiler.CompileStatus;
import com.xored.fmaven.compiler.FantomCompiler;
import com.xored.fmaven.utils.PathUtils;

import fan.fmaven.FanPod;

/**
 * @goal execute
 * @phase compile
 * @threadSafe
 */
public class FantomCompileMojo extends FatomMojo {

	/**
	 * Pod name
	 * 
	 * @parameter
	 */
	private String podName;

	/**
	 * Pod version
	 * 
	 * @parameter default-value="1.0"
	 */
	private String podVersion;

	/**
	 * Pod summary
	 * 
	 * @parameter default-value=""
	 */
	private String podSummary;

	@Override
	protected void doExecute() {
		List<File> sources = getSourceFiles();
		if (sources.isEmpty()) {
			getLog().error(
					"Fantom sources could not be found. Build.fan should be stored in the "
							+ fanDir.getPath());
			return;
		}

		File buildFan = sources.get(0);
		getLog().info(
				String.format("Compiling source %s to %s", podName,
						fanOutputDir.getAbsolutePath()));

		long start = System.currentTimeMillis();
		CompileStatus status = compile(buildFan);
		long duration = (System.currentTimeMillis() - start);

		if (status.code == SUCCESSFUL) {
			getLog().info(
					"Compilation successfully completed in "
							+ String.format("[%.2fs]", duration / 1000.0));
			File pod = new File(fanOutputDir, String.format("%s.%s", podName,
					POD_EXT));
			if (!pod.exists()) {
				getLog().error("Could not find compiled pod " + pod.getPath());
			}
		} else {
			getLog().info(
					String.format("Compilation finished in "
							+ String.format("[%.2fs] with error: ",
									duration / 1000.0) + status.msg));
		}
	}

	private CompileStatus compile(File buildFan) {
		FantomCompiler compiler = new FantomCompiler();

		final FanPod fanPod = FanPod
				.makeFromStr(podName, PathUtils.platformPath(buildFan))
				.version(podVersion).summary(podSummary);
		CompileStatus status;
		try {
			status = compiler.compile(fanPod, fanOutputDir, podsRepo());
		} catch (IOException e) {
			return new CompileStatus(ERROR, "Could not create pods repo: "
					+ e.getMessage());
		}
		return status;
	}

	private File podsRepo() throws IOException {
		File repoDir = File.createTempFile("podsRepo",
				Long.toString(System.nanoTime()));
		repoDir.delete();
		if (!repoDir.exists()) {
			repoDir.mkdir();
		}
		repoDir.deleteOnExit();

		for (Artifact a : project.getDependencyArtifacts()) {
			if (a.getFile() == null) {
				continue;
			}
			try {
				FileUtils.copyFileToDirectory(a.getFile(), repoDir);
				String artifactPodName = a.getFile().getName();
				File podFile = new File(repoDir, a.getFile().getName());
				podFile.renameTo(new File(repoDir, getPodName(artifactPodName)));
			} catch (IOException e) {
			}
		}
		return repoDir;
	}

	private String getPodName(String podWithVersion) {
		String[] parts = podWithVersion.split("-");
		return parts.length > 1 ? parts[0] + ".pod" : podWithVersion;
	}
}
