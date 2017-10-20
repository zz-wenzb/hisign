package com.hisign.solr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import com.hankcs.hanlp.corpus.tag.Nature;
import com.hankcs.hanlp.seg.common.Term;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

public class DataImportUtils {

	/**  
     * ASCII表中可见字符从!开始，偏移位值为33(Decimal)  
     */    
    static final char DBC_CHAR_START = 33; // 半角!    
    
    /**  
     * ASCII表中可见字符到~结束，偏移位值为126(Decimal)  
     */    
    static final char DBC_CHAR_END = 126; // 半角~    
    
    /**  
     * 全角对应于ASCII表的可见字符从！开始，偏移值为65281  
     */    
    static final char SBC_CHAR_START = 65281; // 全角！    
    
    /**  
     * 全角对应于ASCII表的可见字符到～结束，偏移值为65374  
     */    
    static final char SBC_CHAR_END = 65374; // 全角～    
    
    /**  
     * ASCII表中除空格外的可见字符与对应的全角字符的相对偏移  
     */    
    static final int CONVERT_STEP = 65248; // 全角半角转换间隔    
    
    /**  
     * 全角空格的值，它没有遵从与ASCII的相对偏移，必须单独处理  
     */    
    static final char SBC_SPACE = 12288; // 全角空格 12288    
    
    /**  
     * 半角空格的值，在ASCII中为32(Decimal)  
     */    
    static final char DBC_SPACE = ' '; // 半角空格  
	
	/**  
     * <PRE>  
     * 全角字符->半角字符转换    
     * 只处理全角的空格，全角！到全角～之间的字符，忽略其他  
     * </PRE>  
     */    
    public static String qj2bj(String src) {    
        if (src == null) {    
            return src;    
        }    
        StringBuilder buf = new StringBuilder(src.length());    
        char[] ca = src.toCharArray();    
        for (int i = 0; i < src.length(); i++) {    
            if (ca[i] >= SBC_CHAR_START && ca[i] <= SBC_CHAR_END) { // 如果位于全角！到全角～区间内    
                buf.append((char) (ca[i] - CONVERT_STEP));    
            } else if (ca[i] == SBC_SPACE) { // 如果是全角空格    
                buf.append(DBC_SPACE);    
            } else { // 不处理全角空格，全角！到全角～区间外的字符    
                buf.append(ca[i]);    
            }    
        }    
        return buf.toString();    
    }   
	
	/**
	 * 得到有用字段的数量
	 * 
	 * @param mobile
	 * @param id
	 * @param name
	 * @param email
	 * @param qq
	 * @param ip
	 * @return
	 */
	public static int getFieldNum(String mobile, String id, String name, String email, String qq, String ip) {
		int num = 0;
		if (!"".equals(mobile)) {
			num++;
		}
		if (!"".equals(id)) {
			num++;
		}
		if (!"".equals(name)) {
			num++;
		}
		if (!"".equals(email)) {
			num++;
		}
		if (!"".equals(qq)) {
			num++;
		}
		if (!"".equals(ip)) {
			num++;
		}
		return num;
	}

	/**
	 * 删除临时文件
	 * 
	 * @param file
	 */
	public static void deleteFile(File file) {
		if (file.isFile() && file.exists()) {
			file.delete();
		}
	}

	/**
	 * 判断此文件是否生成中间文件 通过截取字符串得到文件名里面的id
	 * 
	 * @param filePath
	 * @param id
	 * @return
	 */
	public static boolean getFileNum_(String filePath, String id) {
		File file = new File(filePath);
		String[] listFile = file.list();
		for (String f : listFile) {
			String id_sub = f.substring(f.indexOf("_") + 1, f.length());
			if (id.equals(id_sub)) {
				return true;
			}
		}
		return false;
	}

	// public static Set getFileNum(String filePath) {
	// Set set = new HashSet();
	// File file = new File(filePath);
	// String[] listFile = file.list();
	// for (int i = 0; i < listFile.length; i++) {
	// int a = listFile[i].lastIndexOf(".") - 1;
	// int b = listFile[i].lastIndexOf(".");
	// String number = listFile[i].substring(a, b);
	// set.add(number);
	// }
	// return set;
	// }

	/**
	 * 中间文件生成，插入数据到数据库
	 * 
	 * @param fileName
	 * @param md5
	 * @param userName
	 * @param type
	 * @param updateTime
	 */
	public static void updateSQL(String fileName, String md5, String userName, String type, String gnrtTime) {
		Connection conn = null;
		try {
			conn = SQLManager.getConnection();
			String sql = "insert into file_task(file_name,file_md5,type,user_name,gnrt_time) values('" + fileName + "','" + md5
					+ "','" + type + "','" + userName + "','" + gnrtTime + "')";
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.executeUpdate();
			ps.close();
			SQLManager.closeConnection(conn);
		} catch (SQLException e) {
			SQLManager.closeConnection(conn);
			e.printStackTrace();
		}
	}

	/**
	 * 写中间文件
	 * 
	 * @param str
	 * @param sb_
	 * @param sb_c
	 * @param num
	 * @param flag
	 * @param filepath1
	 * @param filepath2
	 */
	public static void method(String[] str, String sb_, int flag, String filepath1, String filepath2,
			String mountDirName,String id) {
		String uuid = System.currentTimeMillis() + UUID.randomUUID().toString().replace("-", "");
		
		Date date = new Date();
		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String time = s.format(date);
		BufferedWriter bufferWritter = null;
		try {
			if (flag == 1) {// 写到s
				FileWriter fw = new FileWriter(filepath1, true);
				bufferWritter = new BufferedWriter(fw);
				
				for (int i = 0; i < str.length; i++) {
					if (i % 2 == 0) {
						if (str[i] != null) {
							bufferWritter.write(str[i]);
						}
						bufferWritter.write((char)1);
					}
				}
				bufferWritter.write(uuid+(char)1+mountDirName+(char)1+time+(char)1+id+(char)1);
				bufferWritter.write(sb_);
				bufferWritter.write("\n");
			} else {// 写到d
				FileWriter fw = new FileWriter(filepath2, true);
				bufferWritter = new BufferedWriter(fw);
				for (int i = 0; i < str.length; i++) {
					if (str[i] != null) {
						bufferWritter.write(str[i]);
					}
					bufferWritter.write((char)1);
				}
				bufferWritter.write(uuid+(char)1+mountDirName+(char)1+time+(char)1+id+(char)1);
				bufferWritter.write(sb_);
				bufferWritter.write("\n");
			}

			bufferWritter.flush();
			bufferWritter.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*
	 * extract file extension part from path
	 */
	public static String getExtensionName(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length() - 1))) {
				return filename.substring(dot + 1);
			}
		}
		return filename;
	}

	/*
	 * extract file name without extension
	 */
	public static String getFileNameNoEx(String filename) {
		if ((filename != null) && (filename.length() > 0)) {
			int dot = filename.lastIndexOf('.');
			if ((dot > -1) && (dot < (filename.length()))) {
				return filename.substring(0, dot);
			}
		}
		return filename;
	}

	/**
	 * 匹配身份证号码(不做校验码运算)
	 * 
	 * @param strTerm
	 * @return 是否合理的身份证号码
	 */
	public static boolean matchIdCardNo(String strTerm) {
		Pattern p = null;
		Matcher m = null;
		boolean b = false;

		if (strTerm.length() == 17) {
			strTerm += "X";
		}

		if (strTerm.length() == 18) {
			p = Pattern.compile("^[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))((0[1-9]|[1|2]\\d)|3[0-1])\\d{3}(\\d|X)$");
			m = p.matcher(strTerm);
			b = m.matches();
		} else if (strTerm.length() == 15) {
			p = Pattern.compile("^[1-9]\\d{7}((0[1-9])|(1[0-2]))((0[1-9]|[1|2]\\d)|3[0-1])\\d{3}$");
			m = p.matcher(strTerm);
			b = m.matches();
		}

		return b;
	}

	/**
	 * only office file or text can be processed by tika
	 * 
	 * @param file
	 *            name to be judge type from extension
	 * @return is supported format
	 */
	public static boolean isProcessableFile(File file) {
		String fileExt = getExtensionName(file.getName());
		if (fileExt.equalsIgnoreCase("doc") || fileExt.equalsIgnoreCase("docx") || fileExt.equalsIgnoreCase("xls")
				|| fileExt.equalsIgnoreCase("xlsx") || fileExt.equalsIgnoreCase("ppt")
				|| fileExt.equalsIgnoreCase("pptx") || fileExt.equalsIgnoreCase("csv")
				|| fileExt.equalsIgnoreCase("pdf") || fileExt.equalsIgnoreCase("txt")) {
			return true;
		}
		return false;
	}

	public static boolean IsEmailSeg(String segToCheck) {
		String check = "^[_.0-9a-zA-Z-]+$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(segToCheck);
		return matcher.matches();
	}

	public static boolean IsEmailFormat(String segToCheck) {
		String check = "^([0-9a-zA-Z]+[_.0-9a-zA-Z-]+)@([a-zA-Z0-9-]+[.])+([a-zA-Z]{2,3})$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(segToCheck);
		return matcher.matches();
	}

	/**
	 * judge the validation of bank card
	 * 
	 * @param numString
	 *            number string to be validated
	 * @return true for valid bank card number
	 */
	public static boolean IsBankCardNum(String numString) {
		if (numString.length() < 12 || numString.length() > 19) {
			return false;
		}
		// China UnionPay start with 62, 16 ~ 19 length, no validation
		// algorithm.
		String check = "^(62)\\d{14}\\d?\\d?\\d?$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(numString);
		if (matcher.matches()) {
			return true;
		}

		//
		check = "^(2014|2149)\\d{11}$";
		regex = Pattern.compile(check);
		matcher = regex.matcher(numString);
		if (matcher.matches()) {
			return true;
		}

		char bit = getBankCardCheckCode(numString.substring(0, numString.length() - 1));
		if (bit == 'N') {
			return false;
		}
		return numString.charAt(numString.length() - 1) == bit;
	}

	/**
	 * 从不含校验位的银行卡卡号采用 Luhm 校验算法获得校验位
	 * 
	 * @param nonCheckCodeCardId
	 * @return
	 */
	public static char getBankCardCheckCode(String nonCheckCodeCardId) {
		if (nonCheckCodeCardId == null || nonCheckCodeCardId.trim().length() == 0
				|| !nonCheckCodeCardId.matches("\\d+")) {
			// 如果传的不是数据返回N
			return 'N';
		}
		char[] chs = nonCheckCodeCardId.trim().toCharArray();
		int luhmSum = 0;
		for (int i = chs.length - 1, j = 0; i >= 0; i--, j++) {
			int k = chs[i] - '0';
			if (j % 2 == 0) {
				k *= 2;
				k = k / 10 + k % 10;
			}
			luhmSum += k;
		}
		return (luhmSum % 10 == 0) ? '0' : (char) ((10 - luhmSum % 10) + '0');
	}

	/**
	 * 车牌号全名
	 * 
	 * @param stringToCheck
	 * @return
	 */
	public static boolean IsValidPlateNum_t(String stringToCheck) {
		String check = "[京津沪渝冀豫云辽黑湘皖鲁新苏浙赣鄂桂甘晋蒙陕吉闽贵粤青藏川宁琼使领a-zA-Z]{1}[a-zA-Z]{1}[a-zA-Z0-9]{4}[a-zA-Z0-9挂学警港澳]{1}";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(stringToCheck);
		return matcher.matches();
	}

	/**
	 * 车牌号
	 * 
	 * @param stringToCheck
	 * @return
	 */
	public static boolean IsValidPlateNum(String stringToCheck) {
		String check = "^[\u4e00-\u9fa5]{1}[a-zA-Z]{1}[a-zA-Z_0-9]{5}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(stringToCheck);
		return matcher.matches();
	}

	public static boolean IsPossibleMobile_bak(String stringToCheck) {
		String check = "(^|[^0-9])(13[0-9]|14[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}($|[^0-9])";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(stringToCheck);
		return matcher.matches();
	}

	/**
	 * 匹配手机号
	 * 
	 * @param stringToCheck
	 * @return
	 */
	public static boolean IsPossibleMobile(String stringToCheck) {
		String check = "^(13[0-9]|14[0-9]|15[0-9]|18[0-9]|17[0-9])\\d{8}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(stringToCheck);
		return matcher.matches();
	}

	public static boolean IsPossibleTeleNum(String stringToCheck) {
		String check = "^[0]\\d{8,11}$|^[1-9]\\d{5,7}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(stringToCheck);
		return matcher.matches();
	}

	public static boolean IsPossibleTeleNum01(String stringToCheck) {
		String check = "(0\\d{2,3}-)?\\d{7,8}(-\\d{3,4})?";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(stringToCheck);
		return matcher.matches();
	}

	/**
	 * ip
	 * 
	 * @param stringToCheck
	 * @return
	 */
	public static boolean IsPossibleIp(String stringToCheck) {
		String check = "^((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(stringToCheck);
		return matcher.matches();
	}

	/**
	 * 发动机验证
	 * 
	 * @param stringToCheck
	 *            stringToCheck
	 * @return
	 */
	public static String GetEngineNum(String extraceSrc) {
		Pattern regex = Pattern.compile("[a-zA-Z0-9 ]{5,18}");
		Matcher matcher = regex.matcher(extraceSrc);
		return matcher.find() ? matcher.group() : "";
	}

	/**
	 * QQ号验证
	 * 
	 * @param numToCheck
	 *            numToCheck
	 * @return
	 */
	public static boolean IsQQNumPossible(String numToCheck) {
		String check = "^[1-9]\\d{4,11}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(numToCheck);
		return matcher.matches();
	}

	/**
	 * 提取微信号
	 * 
	 * @param extraceSrc
	 * @return
	 */
	public static String GetWeChatNumber(String extraceSrc) {
		Pattern regex = Pattern.compile("[a-zA-Z]{1}[a-zA-Z0-9_-]{3,20}");
		Matcher matcher = regex.matcher(extraceSrc);
		return matcher.find() ? matcher.group() : "";
	}

	/**
	 * extract content to text string
	 * 
	 * @param file
	 *            path name for the file to be extract content
	 * @return file content or null
	 */
	public static String fileToTxt(File file) {
		AutoDetectParser parser = new AutoDetectParser();
		BodyContentHandler handler = new BodyContentHandler(-1);
		InputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			Metadata metadata = new Metadata();
			parser.parse(inputStream, handler, metadata, new ParseContext());
			inputStream.close();
			return handler.toString();
		} catch (TikaException e) {
			e.printStackTrace();
			if (e.getCause() instanceof IllegalArgumentException) {
				return null;
			} else {
				return "";
			}
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		} finally {
			if (null != inputStream) {
				try {
					inputStream.close();
				} catch (IOException e1) {
					// e1.printStackTrace();
				}
			}
		}
	}

	public static MultiValueMap extractNamingEntity(List<Term> termList) {
		MultiValueMap segMap = new MultiValueMap();
		for (int index = 0; index < termList.size(); ++index) {
			Term term = termList.get(index);
			switch (term.nature) {
			case n:
				if (term.word.startsWith("微信")) {
					int checkPos = 1;
					int frontIndex = index;
					StringBuffer tmpData = new StringBuffer();
					while (tmpData.toString().length() < 30 && index + checkPos < termList.size()) {
						if (termList.get(index + checkPos).offset < termList.get(frontIndex).offset
								+ termList.get(frontIndex).word.length()) {
							++checkPos;
							continue;
						}
						tmpData.append(termList.get(index + checkPos).word);
						frontIndex = index + checkPos;
						++checkPos;
					}
					String extract = DataImportUtils.GetWeChatNumber(tmpData.toString());
					if (extract.length() > 3) {
						segMap.put("wechatnum", extract);
						int step = 1;
						while (index + step < termList.size()
								&& term.offset + term.length() + extract.length() < termList.get(index + step).offset) {
							++step;
						}
						index += (step - 1);
					}
				} else if (term.word.startsWith("发动机")) {
					int checkPos = 1;
					int frontIndex = index;
					StringBuffer tmpData = new StringBuffer();
					while (tmpData.toString().length() < 25 && index + checkPos < termList.size()) {
						if (termList.get(index + checkPos).offset < termList.get(frontIndex).offset
								+ termList.get(frontIndex).word.length()) {
							++checkPos;
							continue;
						}

						if (tmpData.toString().length() > 6
								&& termList.get(index + checkPos).nature.toString().startsWith("w")) {
							break;
						}
						tmpData.append(termList.get(index + checkPos).word);
						frontIndex = index + checkPos;
						++checkPos;
					}
					String extract = DataImportUtils.GetEngineNum(tmpData.toString());
					if (extract.length() > 5) {
						segMap.put("enginenum", extract);
						int step = 1;
						while (index + step < termList.size()
								&& term.offset + term.length() + extract.length() < termList.get(index + step).offset) {
							++step;
						}
						index += (step - 1);
					}
				}
				break;
			case nr:
			case nrj:
			case nrf:
			case nr1:
			case nr2:
				if (term.word.length() > 1 && !term.word.startsWith("，")) {
					segMap.put("name", term.word);
				}
				break;
			case ns:
			case nsf:
				segMap.put("location", term.word);
				break;
			case nt:
				segMap.put("company", term.word);
				break;
			case nto:
			case ntu:
			case nts:
			case nth:
			case ntc:
			case ntcf:
			case ntcb:
			case ntch:
			case nis:
				if (term.word.startsWith("微信号")) {
					int checkPos = 1;
					int frontIndex = index;
					StringBuffer tmpData = new StringBuffer();
					while (tmpData.toString().length() < 30 && index + checkPos < termList.size()) {
						if (termList.get(index + checkPos).offset < termList.get(frontIndex).offset
								+ termList.get(frontIndex).word.length()) {
							++checkPos;
							continue;
						}
						tmpData.append(termList.get(index + checkPos).word);
						frontIndex = index + checkPos;
						++checkPos;
					}
					String extract = DataImportUtils.GetWeChatNumber(tmpData.toString());
					if (extract.length() > 3) {
						segMap.put("wechatnum", extract);
						int step = 1;
						while (index + step < termList.size()
								&& term.offset + term.length() + extract.length() < termList.get(index + step).offset) {
							++step;
						}
						index += (step - 1);
					}
				} else {
					segMap.put("organization", term.word);
				}
				break;
			case nf:
			case gp:
				segMap.put("goods", term.word);
				break;
			case m:
				if (term.word.length() == 11 && term.word.startsWith("1")) {
					segMap.put("mobilenum", term.word);
				} else if ((term.word.length() == 17 || term.word.length() == 18 || term.word.length() == 15)
						&& DataImportUtils.matchIdCardNo(term.word)) {
					if (term.word.length() == 17) {
						segMap.put("idnum", term.word + "X");
					} else {
						segMap.put("idnum", term.word);
					}
				} else if ((term.word.length() == 6 // no zone number
						|| term.word.length() == 7 // no zone number
						|| term.word.length() == 8 // no zone number
						|| ((term.word.length() == 11 // with zone number
								|| term.word.length() == 10 // with zone number
								|| term.word.length() == 12) // with zone number
								&& term.word.startsWith("0")))
						&& DataImportUtils.IsPossibleTeleNum01(term.word)) {
					segMap.put("telenum", term.word);
				} else if ((term.word.length() == 16 || term.word.length() == 17 || term.word.length() == 18
						|| term.word.length() == 19) && DataImportUtils.IsBankCardNum(term.word)) {
					segMap.put("bankcard", term.word);
				} else if (term.word.length() == 4) {
					if ((index + 6 < termList.size()
							&& DataImportUtils.IsBankCardNum(term.word + termList.get(index + 2).word
									+ termList.get(index + 4).word + termList.get(index + 6).word))) {
						segMap.put("bankcard", term.word + termList.get(index + 2).word + termList.get(index + 4).word
								+ termList.get(index + 6).word);
						index += 6;
					} else if ((index + 8 < termList.size() && DataImportUtils
							.IsBankCardNum(term.word + termList.get(index + 2).word + termList.get(index + 4).word
									+ termList.get(index + 6).word + termList.get(index + 8).word))) {
						segMap.put("bankcard", term.word + termList.get(index + 2).word + termList.get(index + 4).word
								+ termList.get(index + 6).word + termList.get(index + 8).word);
						index += 8;
					}
				}

				break;
			case nx:
				if (term.word.indexOf("@") != -1) {
					String mailString = new String();

					int checkPos = 1;
					while (index - checkPos > 0 && DataImportUtils.IsEmailSeg(termList.get(index - checkPos).word)) {
						mailString = termList.get(index - checkPos).word + mailString;
						++checkPos;
					}
					mailString = mailString + term.word;
					if (mailString.endsWith("\n")) {
						while (mailString.endsWith("\n")) {
							mailString = mailString.substring(0, (mailString.length() - 1));
						}
					} else {
						checkPos = 1;
						while (index + checkPos < termList.size()) {
							String wordInList = termList.get(index + checkPos).word;
							while (wordInList.endsWith("\n")) {
								wordInList = wordInList.substring(0, (wordInList.length() - 1));
							}

							if (DataImportUtils.IsEmailSeg(wordInList)) {
								mailString = mailString + wordInList;
								++checkPos;
							} else {
								break;
							}
						}
					}

					if (DataImportUtils.IsEmailFormat(mailString)) {
						segMap.put("email", mailString);
						index += (checkPos - 1);
					}
				} else if (term.word.indexOf("qq") != -1 || term.word.indexOf("QQ") != -1) {
					int checkPos = 1;
					while (checkPos < 4 && index + checkPos < termList.size()) {
						if (termList.get(index + checkPos).nature == Nature.m) {
							if (DataImportUtils.IsQQNumPossible(termList.get(index + checkPos).word)) {
								segMap.put("QQnum", termList.get(index + checkPos).word);
							}
							index += checkPos;
							break;
						}
						++checkPos;
					}
				}
				break;
			case b:
				if (!term.word.startsWith("鄂")) {
					break;
				}
			case j:
				StringBuffer tmpString = new StringBuffer();
				tmpString.append(term.word);
				int checkPos = 1;
				int flagCount = 0;
				while (tmpString.toString().length() < 7 && index + checkPos < termList.size() && flagCount < 3) {
					if (termList.get(index + checkPos).nature.toString().startsWith("w")) {
						++flagCount;
					} else {
						tmpString.append(termList.get(index + checkPos).word);
					}
					++checkPos;
				}

				if (DataImportUtils.IsValidPlateNum(tmpString.toString())) {
					segMap.put("platenum", tmpString.toString());
					index += checkPos;
				}
				break;
			case nnt:
				segMap.put("occupation", term.word);
				break;
			case xu:
				if (term.word.length() > 5 && !term.word.startsWith("...")) {
					segMap.put("webaddr", term.word);
				}
				break;
			case nz:
				segMap.put("rawstr", term.word);
				break;
			default:
				/*
				 * if(!term.nature.toString().startsWith("w") &&
				 * !term.nature.toString().startsWith("q") &&
				 * !term.nature.toString().startsWith("a") &&
				 * !term.nature.toString().startsWith("uj") &&
				 * !term.nature.toString().startsWith("c") &&
				 * !term.nature.toString().startsWith("d") &&
				 * !term.nature.toString().startsWith("v")) {
				 * System.out.println(term.nature.toString() + ": " +
				 * term.word); }
				 */
				break;
			}
		}
		return segMap;
	}
}
