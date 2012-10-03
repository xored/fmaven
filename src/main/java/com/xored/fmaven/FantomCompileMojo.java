package com.xored.fmaven;

import java.io.File;
import java.util.List;

import com.xored.fmaven.compiler.FantomCompiler;
import com.xored.fmaven.compiler.FantomCompiler.CompileStatus;

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
		FantomCompiler compiler = new FantomCompiler();

		List<File> sources = getSourceFiles();
		if (sources.isEmpty()) {
			getLog().error(
					"Fantom sources could not be found. Build.fan should be stored in the main/fan directory");
			return;
		}

		File buildFan = sources.get(0);
		getLog().info(
				String.format("Compiling source %s to %s", podName,
						fanOutputDir.getAbsolutePath()));

		// TODO: use normal path
		final FanPod fanPod = FanPod
				.make(podName,
						"file:/" + buildFan.getPath().replaceAll("\\\\", "/"))
				.version(podVersion).summary(podSummary);

		long start = System.currentTimeMillis();
		CompileStatus status = compiler.compile(fanPod, fanOutputDir);
		long duration = (System.currentTimeMillis() - start);

		if (status.code == FantomCompiler.SUCCESSFUL) {
			getLog().info(
					"Compilation successfully completed in "
							+ String.format("[%.2fs]", duration / 1000.0));
			File pod = new File(fanOutputDir, String.format("%s.%s",
					fanPod.podName, POD_EXT));
			if (pod.exists()) {
				pod.renameTo(new File(fanOutputDir, String.format("%s-%s.pod",
						project.getArtifactId(), project.getVersion())));
				pod.delete();
			} else {
				getLog().error("Could not find compiled pod " + pod.getPath());
			}
		} else {
			getLog().info(
					String.format("Compilation finished in "
							+ String.format("[%.2fs] with error: ",
									duration / 1000.0) + status.msg));
		}
	}
}
