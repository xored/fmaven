package com.xored.fmaven.compiler;

public class CompileStatus {

	public static int ERROR = 0;
	public static int SUCCESSFUL = 1;

	public final int code;
	public final String msg;

	public CompileStatus(int code) {
		this(code, null);
	}

	public CompileStatus(int code, String msg) {
		this.code = code;
		this.msg = msg;
	}

}
