package com.xored.fmaven;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

public class FanCompileCommand {

	protected List<String> env = new ArrayList<String>();
	protected List<String> args = new ArrayList<String>();
	private String fanExec;

	public FanCompileCommand() throws IllegalAccessException {
		for (String key : System.getenv().keySet()) {
			env.add(key + "=" + System.getenv(key));
		}

		fanExec = System.getenv("FAN_HOME");
		if (fanExec == null) {
			if (fanExec == null) {
				throw new IllegalAccessException(
						"Couldn't locate fantom, try setting FAN_HOME environment variable.");
			}
		}
		fanExec += File.separator + "bin" + File.separator + "fan.exe";
	}

	protected List<String> buildCommand() {
		ArrayList<String> cmd = Lists.newArrayList();
		cmd.add(fanExec);
		cmd.addAll(args);
		return cmd;
	}

	public void addArgs(String... args1) {
		for (String arg : args1) {
			this.args.add(arg);
		}
	}

	public void addEnvVar(String key, String value) {
		this.env.add(key + "=" + value);
	}
}
