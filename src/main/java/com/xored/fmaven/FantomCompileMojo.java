package com.xored.fmaven;

import java.io.File;
import java.util.List;

import com.xored.fmaven.FantomCompiler.CompileStatus;

/**
 * @goal compile
 */
public class FantomCompileMojo extends FatomMojo {

	@Override
	protected void doExecute() {
		FantomCompiler compiler = new FantomCompiler();

		List<File> sources = getSourceFiles();

		getLog().info(
				String.format("Compiling %d source files to %s",
						sources.size(), fanOutputDir.getAbsolutePath()));

		long start = System.currentTimeMillis();
		CompileStatus status = compiler.compile(sources, fanOutputDir);
		long duration = (System.currentTimeMillis() - start);

		if (status.code == FantomCompiler.SUCCESSFUL) {
			getLog().info(
					"Compilation successfully completed in "
							+ String.format("[%.2fs]", duration / 1000.0));
		} else {
			getLog().info(
					String.format("Compilation finished in "
							+ String.format("[%.2fs] with error: ",
									duration / 1000.0) + status.msg));
		}
	}
}
