package com.xored.fmaven.compiler;

import static com.xored.fmaven.compiler.CompileStatus.ERROR;
import static com.xored.fmaven.compiler.CompileStatus.SUCCESSFUL;
import static com.xored.fmaven.utils.PathUtils.platformPath;

import java.io.File;

import com.google.common.base.Joiner;

import fan.fmaven.Compiler;
import fan.fmaven.FanPod;
import fan.sys.List;
import fan.sys.Uri;

public class FantomCompiler {

	@SuppressWarnings("static-access")
	public CompileStatus compile(FanPod pod, File fanOutputDir, File podRepo) {
		if (!fanOutputDir.exists()) {
			fanOutputDir.mkdirs();
		}

		Compiler compiler = new Compiler().make(
				Uri.fromStr(platformPath(fanOutputDir, true)),
				Uri.fromStr(platformPath(podRepo, true)));
		compiler.pods.add(pod);

		List errors = compiler.compilePods();
		compiler.dispose();

		if (errors.isEmpty()) {
			return status(SUCCESSFUL);
		} else {
			status(ERROR, Joiner.on("; ").join(errors.toArray()));
		}
		return status(SUCCESSFUL);
	}

	private static CompileStatus status(int code, String msg) {
		return new CompileStatus(code, msg);
	}

	private static CompileStatus status(int code) {
		return new CompileStatus(code);
	}
}
