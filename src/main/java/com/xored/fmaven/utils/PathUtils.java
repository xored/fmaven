package com.xored.fmaven.utils;

import java.io.File;

public class PathUtils {

	public static String platformPath(File file) {
		return platformPath(file, false);
	}

	// remove this piece of shit
	public static String platformPath(File file, boolean trailing) {
		String path = "file:/" + file.getPath().replaceAll("\\\\", "/");
		if (!trailing) {
			return path;
		}
		return path.endsWith("/") ? path : path + "/";
	}
}
