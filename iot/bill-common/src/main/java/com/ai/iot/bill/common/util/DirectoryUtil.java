package com.ai.iot.bill.common.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 目录工具类
 * @author xue
 *
 */
public class DirectoryUtil {

	private static String message;
	
	/**
	 * 获取某一路径下所有的文件
	 * @param filePath
	 * @return list
	 */
	public static List<String> getFileNames(String filePath) {
		List<String> fileList = new ArrayList<>();
		try {
			File root = new File(filePath);
			File[] files = root.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					fileList.add(file.getAbsolutePath());
				}
			}
		} catch (Exception e) {
			message = e.toString();
		}

		return fileList;
	}
	
	/**
	 * 获取某一路径下所有的文件
	 * @param filePath
	 * @return list
	 */
	public static List<File> getFiles(String filePath) {
		List<File> fileList = new ArrayList<>();
		try {
			File root = new File(filePath);
			File[] files = root.listFiles();
			for (File file : files) {
				if (file.isFile()) {
					fileList.add(file);
				}
			}

		} catch (Exception e) {
			message = e.toString();
		}

		return fileList;
	}

	public static String getMessage() {
		return message;
	}

}
