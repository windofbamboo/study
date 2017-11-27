package com.ai.iot.bill.common.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.List;
import shade.storm.org.apache.commons.io.FileUtils;
import com.ai.iot.bill.common.param.FileParseBean;

/**
 * 文件操作工具类
 * 
 * @author xue
 */
public class FileUtil {

	// private static Logger logger = LoggerFactory.getLogger(FileUtil.class);
	private static final int BUFFER_SIZE = 10 * 1024 * 1024;
	private static String message;

	/**
	 * 读取文本文件内容
	 * 
	 * @param filePathAndName
	 *            带有完整绝对路径的文件名
	 * @param encoding
	 *            文本文件打开的编码方式
	 * @return 返回文本文件的内容
	 */
	public static String readTxt(String filePathAndName, String encoding) {
		encoding = encoding.trim();
		StringBuilder str = new StringBuilder("");
		String st = "";
		FileInputStream fs = null;
		InputStreamReader isr=null;
		try {
			fs = new FileInputStream(filePathAndName);
			
			if (encoding.equals("")) {
				isr = new InputStreamReader(fs);
			} else {
				isr = new InputStreamReader(fs, encoding);
			}
			BufferedReader br = new BufferedReader(isr);
			try {
				String data = "";
				while ((data = br.readLine()) != null) {
					str.append(data + " ");
				}
			} catch (Exception e) {
				str.append(e.toString());
			} finally {
				br.close();
			}
			st = str.toString();
		} catch (IOException es) {
			st = "";
		} finally {
			if (null != fs) {
				try {
					fs.close();
				} catch (IOException e) {
					message = "读取文本出错";
				}
			}
			if(null!=isr){
				try {
					isr.close();
				} catch (IOException e) {
					message = "读取文本出错";
				}
			}
		}
		return st;
	}

	/**
	 * 读取文本文件内容
	 * 
	 * @param filePathAndName
	 *            带有完整绝对路径的文件名
	 * @param encoding
	 *            文本文件打开的编码方式
	 * @return 返回文本文件的每一行内容
	 */
	public static List<String> readLineTxt(String filePathAndName, String encoding) {
		encoding = encoding.trim();
		List<String> st = new ArrayList<String>();
		FileInputStream fs = null;
		InputStreamReader isr = null;
		try {
			fs = new FileInputStream(filePathAndName);
			
			if (encoding.equals("")) {
				isr = new InputStreamReader(fs);
			} else {
				isr = new InputStreamReader(fs, encoding);
			}
			BufferedReader br = new BufferedReader(isr);
			try {
				String data = "";
				while ((data = br.readLine()) != null) {
					st.add(data);
				}
			} catch (Exception e) {
				message = "读取文本出错";
			} finally {
				br.close();
			}
		} catch (IOException es) {
			st.clear();
		} finally {
			if (null != fs) {
				try {
					fs.close();
				} catch (IOException e) {
					message = "读取文本出错";
				}
			}
			if (null != isr) {
				try {
					isr.close();
				} catch (IOException e) {
					message = "读取文本出错";
				}
			}
		}
		return st;
	}

	/**
	 * 新建目录
	 * 
	 * @param folderPath
	 *            目录
	 * @return 返回目录创建后的路径
	 */
	public static String createFolder(String folderPath) {
		String txt = folderPath;
		try {
			File myFilePath = new File(txt);
			txt = folderPath;
			if (!myFilePath.exists()) {
				myFilePath.mkdir();
			}
		} catch (Exception e) {
			message = "创建目录操作出错";
		}
		return txt;
	}

	/**
	 * 多级目录创建
	 * 
	 * @param folderPath
	 *            准备要在本级目录下创建新目录的目录路径 例如 c:myf
	 * @param paths
	 *            无限级目录参数，各级目录以单数线区分 例如 a|b|c
	 * @return 返回创建文件后的路径 例如 c:myfac
	 */
	public static String createFolders(String folderPath, String paths) {
		String txts = folderPath;
		try {
			String txt;
			txts = folderPath;
			StringTokenizer st = new StringTokenizer(paths, "|");
			for (; st.hasMoreTokens();) {
				txt = st.nextToken().trim();
				if (txts.lastIndexOf("/") != -1) {
					txts = createFolder(txts + txt);
				} else {
					txts = createFolder(txts + txt + "/");
				}
			}
		} catch (Exception e) {
			message = "创建目录操作出错！";
		}
		return txts;
	}

	/**
	 * 新建文件
	 * 
	 * @param filePathAndName
	 *            文本文件完整绝对路径及文件名
	 * @param fileContent
	 *            文本文件内容
	 * @return
	 */
	public static void createFile(String filePathAndName, String fileContent) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				if (!myFilePath.createNewFile()) {
					return;
				}
			}
			FileWriter resultFile = null;
			try {
				resultFile = new FileWriter(myFilePath);
			} catch (Exception e) {
				message = "创建文件操作出错";
			} finally {
				if (null != resultFile) {
					resultFile.close();
				}
			}

			PrintWriter myFile = null;
			try {
				myFile = new PrintWriter(resultFile);
				String strContent = fileContent;
				myFile.println(strContent);
			} catch (Exception e) {
				message = "创建文件操作出错";
			} finally {
				if (null != myFile) {
					myFile.close();
				}
			}
		} catch (Exception e) {
			message = "创建文件操作出错";
		}
	}

	/**
	 * 有编码方式的文件创建
	 * 
	 * @param filePathAndName
	 *            文本文件完整绝对路径及文件名
	 * @param fileContent
	 *            文本文件内容
	 * @param encoding
	 *            编码方式 例如 GBK 或者 UTF-8
	 * @return
	 */
	public static void createFile(String filePathAndName, String fileContent, String encoding) {

		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			File myFilePath = new File(filePath);
			if (!myFilePath.exists()) {
				if (!myFilePath.createNewFile()) {
					return;
				}
			}
			PrintWriter myFile = null;
			try {
				myFile = new PrintWriter(myFilePath, encoding);
				String strContent = fileContent;
				myFile.println(strContent);
			} catch (Exception e) {
				message = "创建文件操作出错";
			} finally {
				if (null != myFile) {
					myFile.close();
				}
			}

		} catch (Exception e) {
			message = "创建文件操作出错";
		}
	}

	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            文本文件完整绝对路径及文件名
	 * @return Boolean 成功删除返回true遭遇异常返回false
	 */
	public static boolean delFile(String filePathAndName) {
		boolean bea = false;
		try {
			String filePath = filePathAndName;
			File myDelFile = new File(filePath);
			if (myDelFile.exists()) {
				if (!myDelFile.delete()) {
					return false;
				}
				bea = true;
			} else {
				bea = true;
			}
		} catch (Exception e) {
			message = (filePathAndName + "删除文件操作出错 " + e.toString());
		}
		return bea;
	}

	/**
	 * 删除文件夹
	 * 
	 * @param folderPath
	 *            文件夹完整绝对路径
	 * @return
	 */
	public static void delFolder(String folderPath) {
		try {
			delAllFile(folderPath); // 删除完里面所有内容
			String filePath = folderPath;
			filePath = filePath.toString();
			java.io.File myFilePath = new java.io.File(filePath);
			// 删除空文件夹
			if (!myFilePath.delete()) {
				return;
			}
		} catch (Exception e) {
			message = ("删除文件夹操作出错");
		}
	}

	/**
	 * 删除指定文件夹下所有文件
	 * 
	 * @param path
	 *            文件夹完整绝对路径
	 * @return
	 * @return
	 */
	public static boolean delAllFile(String path) {
		boolean bea = false;
		File file = new File(path);
		if (!file.exists()) {
			return bea;
		}
		if (!file.isDirectory()) {
			return bea;
		}
		String[] tempList = file.list();
		File temp = null;
		for (int i = 0; i < tempList.length; i++) {
			if (path.endsWith(File.separator)) {
				temp = new File(path + tempList[i]);
			} else {
				temp = new File(path + File.separator + tempList[i]);
			}
			if (temp.isFile()) {
				if (!temp.delete()) {
					return false;
				}
			}
			if (temp.isDirectory()) {
				delAllFile(path + "/" + tempList[i]);// 先删除文件夹里面的文件
				delFolder(path + "/" + tempList[i]);// 再删除空文件夹
				bea = true;
			}
		}
		return bea;
	}

	/**
	 * 复制单个文件
	 * 
	 * @param oldPathFile
	 *            准备复制的文件源
	 * @param newPathFile
	 *            拷贝到新绝对路径带文件名
	 * @return
	 */
	public static void copyFile(String oldPathFile, String newPathFile) {
		InputStream inStream = null;
		FileOutputStream fs = null;

		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPathFile);
			if (oldfile.exists()) { // 文件存在时
				inStream = new FileInputStream(oldPathFile); // 读入原文件
				fs = new FileOutputStream(newPathFile);
				byte[] buffer = new byte[1024];
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
				fs.close();
			}
		} catch (Exception e) {
			message = ("复制单个文件操作出错");
		} finally {
			try {
				if (null != inStream) {
					inStream.close();
				}
			} catch (IOException e) {
				message = ("复制单个文件操作出错");
			}
			try {
				if (null != fs) {
					fs.close();
				}
			} catch (IOException e) {
				message = ("复制单个文件操作出错");
			}
		}
	}

	/**
	 * 复制整个文件夹的内容
	 * 
	 * @param oldPath
	 *            准备拷贝的目录
	 * @param newPath
	 *            指定绝对路径的新目录
	 * @return
	 */
	public static void copyFolder(String oldPath, String newPath) {
		try {
			new File(newPath).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			String[] file = a.list();
			File temp = null;
			for (int i = 0; i < file.length; i++) {
				if (oldPath.endsWith(File.separator)) {
					temp = new File(oldPath + file[i]);
				} else {
					temp = new File(oldPath + File.separator + file[i]);
				}
				if (temp.isFile()) {
					FileInputStream input = null;
					FileOutputStream output = null;
					try {
						input = new FileInputStream(temp);
						output = new FileOutputStream(newPath + "/" + (temp.getName()).toString());
						byte[] b = new byte[1024 * 5];
						int len;
						while ((len = input.read(b)) != -1) {
							output.write(b, 0, len);
						}
						output.flush();
					} catch (Exception e) {
						message = "复制整个文件夹内容操作出错";
					} finally {
						if (null != output) {
							output.close();
						}
						if (null != input) {
							input.close();
						}
					}
				}
				if (temp.isDirectory()) {// 如果是子文件夹
					copyFolder(oldPath + "/" + file[i], newPath + "/" + file[i]);
				}
			}
		} catch (Exception e) {
			message = "复制整个文件夹内容操作出错";
		}
	}

	/**
	 * 移动文件
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static void moveFile(String oldPath, String newPath) {
		copyFile(oldPath, newPath);
		delFile(oldPath);
	}

	/**
	 * 移动文件到目录
	 * 
	 * @param filePath
	 * @param destDirectory
	 * @return
	 */
	public static void moveFileToDirectory(String filePath, String destDirectory) {
		File file = new File(filePath);
		try {
			if (file.exists()) {
				FileUtils.copyFileToDirectory(file, new File(destDirectory));
				FileUtils.deleteQuietly(file);
			}
		} catch (IOException e) {
			message = "Failed to move files to the directory, ".concat(e.getMessage());
		}
	}
	
	/**
	 * 拷贝文件到目录
	 * 
	 * @param filePath
	 * @param destDirectory
	 * @return
	 */
	public static void copyFileToDirectory(String filePath, String destDirectory) {
		File file = new File(filePath);
		try {
			if (file.exists()) {
				FileUtils.copyFileToDirectory(file, new File(destDirectory));
			}
		} catch (IOException e) {
			message = "Failed to copy files to the directory, ".concat(e.getMessage());
		}
	}

	/**
	 * 移动目录
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static void moveFolder(String oldPath, String newPath) {
		copyFolder(oldPath, newPath);
		delFolder(oldPath);
	}

	/**
	 * 移动目录下所有文件到另外一个目录
	 * 
	 * @param oldPath
	 * @param newPath
	 * @return
	 */
	public static boolean moveFolderFile(String oldPath, String newPath) {
		try {
			new File(newPath).mkdirs(); // 如果文件夹不存在 则建立新文件夹
			File a = new File(oldPath);
			File[] files = a.listFiles();
			if (newPath.lastIndexOf("/") == -1) {
				newPath = newPath + "/";
			}
			for (int i = 0; i < files.length; i++) {
				String fileName = files[i].getName();
				if (!files[i].renameTo(new File(newPath + fileName))) {
					return false;
				}
			}
		} catch (Exception e) {
			message = "剪切整个文件夹内容操作出错" + e.getMessage();
			return false;
		}
		return true;
	}

	/**
	 * 得到错误信息
	 */
	public static String getMessage() {
		return message;
	}

	/**
	 * 按批次读取文件，一般适用于大文件
	 * 
	 * @param start
	 *            开始位置，指的是offset而非行号
	 * @param maxBatch
	 *            一批次读取的最大行数
	 * @param file
	 *            文件对象
	 * @return 文件批次对象
	 * @throws IOException
	 */
	public static FileParseBean readFileByBatch(int start, int maxBatch, File file) {
		FileParseBean batchReadFile = new FileParseBean();
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			String line = "";
			isr = new InputStreamReader(new FileInputStream(file), Const.UTF8);
			br = new BufferedReader(isr, BUFFER_SIZE);
			List<String> batchDataList = new ArrayList<String>();
			int lineNumber = 1;
			br.skip(start);
			while ((line = br.readLine()) != null) {
				batchDataList.add(line);
				start = start + line.length() + 2;
				if (lineNumber == maxBatch) {
					break;
				}
				lineNumber++;
			}
			batchReadFile.setBatchDataList(batchDataList);
			batchReadFile.setOffset(start);
			if ((line = br.readLine()) == null) {
				batchReadFile.setFinished(true);
			} else {
				batchReadFile.setFinished(false);
			}
		} catch (IOException e) {
			message = "Failed to read file by batch, ".concat(e.getMessage());
		} finally {
			try {
				if (br != null) {
					br.close();
				}
				if (isr != null) {
					isr.close();
				}
			} catch (IOException e) {
				message = "Failed to close file read stream, ".concat(e.getMessage());
			}
		}
		return batchReadFile;
	}

	/**
	 * 按行号读取一行的内容
	 * 
	 * @param lineNumber
	 *            行号，从1开始计算
	 * @param filePath
	 *            文件路径
	 * @return 行内容
	 * @throws IOException
	 */
	public static String readLineByNumber(int lineNumber, File file) {
		String line = "";
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file), BUFFER_SIZE);
			int currentLineNumber = 1;
			while ((line = br.readLine()) != null) {
				if (currentLineNumber == lineNumber) {
					break;
				}
				currentLineNumber++;
			}
		} catch (IOException e) {
			message = "Failed to read file by line number, ".concat(e.getMessage());
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				message = "Failed to close file read stream, ".concat(e.getMessage());
			}
		}
		return line;
	}

	/**
	 * 按列表写入文件，数据列表的一行对应文件的一行
	 * 
	 * @param dataList
	 *            数据列表
	 * @param filePath
	 *            文件路径
	 * @return 写入结果
	 * @throws Exception
	 */
	public static boolean writeFile(List<String> dataList, File file) {
		return writeFile(dataList, file, "\r\n");
	}

	/**
	 * 按列表写入文件，数据列表的一行对应文件的一行
	 * 
	 * @param dataList
	 *            数据列表
	 * @param filePath
	 *            文件路径
	 * @param lineSplit
	 *            行分隔符
	 * @return 写入结果
	 * @throws Exception
	 */
	public static boolean writeFile(List<String> dataList, File file, String lineSplit) {
		boolean success = true;
		OutputStream os = null;
		OutputStreamWriter writer = null;
		BufferedWriter bw = null;
		try {
			os = new FileOutputStream(file);
			writer = new OutputStreamWriter(os);
			bw = new BufferedWriter(writer);
			for (String line : dataList) {
				bw.write(line.concat(lineSplit));
			}
			bw.flush();
		} catch (Exception e) {
			success = false;
			message = "Failed to write file, ".concat(e.getMessage());
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (writer != null) {
					writer.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				message = "Failed to close file write stream, ".concat(e.getMessage());
			}
		}
		return success;
	}

	/**
	 * 向文件末尾写入字符串数据
	 * 
	 * @param data
	 *            数据
	 * @param file
	 *            文件对象
	 * @param append
	 *            追加还是覆盖模式 true-追加 ，false-覆盖
	 * @return 写入结果
	 * @throws IOException
	 */
	public static boolean writeFile(String data, File file, boolean append) {

		boolean success = true;
		OutputStream os = null;
		OutputStreamWriter writer = null;
		BufferedWriter bw = null;
		try {
			os = new FileOutputStream(file, append);
			writer = new OutputStreamWriter(os);
			bw = new BufferedWriter(writer);
			bw.write(data);
			bw.flush();
		} catch (Exception e) {
			success = false;
			message = "Failed to write file, ".concat(e.getMessage());
		} finally {
			try {
				if (bw != null) {
					bw.close();
				}
				if (writer != null) {
					writer.close();
				}
				if (os != null) {
					os.close();
				}
			} catch (IOException e) {
				message = "Failed to close file write stream, ".concat(e.getMessage());
			}
		}
		return success;
	}
	
	/**
	 * 通过目录和文件名称拼接文件绝对路径
	 * @param path 目录
	 * @param fileName 文件名
	 * @return 文件的绝对路径
	 */
	public static String getFilePath(String path, String fileName) {
		StringBuffer sb = new StringBuffer();
		path = path.replaceAll("\\", "/");
		if (path.lastIndexOf("/") == -1) {
			sb.append(path).append("/").append(fileName);
		} else {
			sb.append(path).append(fileName);
		}
		return sb.toString();
	}
	
}
