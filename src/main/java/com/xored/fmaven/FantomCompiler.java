package com.xored.fmaven;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.Executor;

public class FantomCompiler {
	public static int NOTHING_TO_COMPILE = -1;
	public static int ERROR = 0;
	public static int SUCCESSFUL = 1;

	public static class CompileStatus {

		public final int code;
		public final String msg;

		public CompileStatus(int code) {
			this(code, null);
		}

		private CompileStatus(int code, String msg) {
			this.code = code;
			this.msg = msg;
		}
	}

	public CompileStatus compile(List<File> sources, File fanOutputDir) {
		if (!fanOutputDir.exists()) {
			fanOutputDir.mkdirs();
		}

		if (sources.size() < 1) {
			return status(NOTHING_TO_COMPILE);
		}

		FanCompileCommand compileCmd;
		try {
			compileCmd = new FanCompileCommand();
			for (File f : sources) {
				compileCmd.addArgs(f.getAbsolutePath());
			}

			List<String> cmd = compileCmd.buildCommand();
			CommandLine cl = new CommandLine("\"" + cmd.get(0) + "\"");
			for (int i = 1; i < cmd.size(); i++) {
				cl.addArgument(cmd.get(i));
			}

			Executor exec = new DefaultExecutor();
			int exitValue = exec.execute(cl);
			if (exitValue != 0) {
				return status(ERROR, "Command line returned non-zero value:"
						+ exitValue);
			}
		} catch (IllegalAccessException ex) {
			return status(ERROR, ex.getMessage());
		} catch (ExecuteException ex) {
			return status(ERROR, ex.getMessage());
		} catch (IOException ex) {
			return status(ERROR, ex.getMessage());
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
