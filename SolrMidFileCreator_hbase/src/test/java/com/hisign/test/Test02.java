package com.hisign.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.seg.Segment;
import com.hankcs.hanlp.seg.common.Term;
import com.hisign.solr.DBItemContent;
import com.hisign.solr.DataImportUtils;
import com.hisign.solr.ExcelUtil;

public class Test02 {
	public static void main(String[] args) {
		
	}
	

	public static void getEmail(){
		String stringToCheck = "1615312491@qq.com";
		String check = "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(stringToCheck);
		System.out.println(matcher.matches());
	}
	
	public static void getQQ(){
		String stringToCheck = "62285951";
		String check = "^[1-9][0-9]{4,11}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(stringToCheck);
		System.out.println(matcher.matches());
	}
	
	public static void toUp(){
		String s = "Wen_zhi_bin@126.com";
		s = s.toUpperCase();
		System.out.println(s);
	}
	
	public static void getExcel() {
		boolean isE2007 = false; // 判断是否是excel2007格式
		String fileName = "G:\\123.xlsx";
		if (fileName.endsWith("xlsx")){
			isE2007 = true;
		}
		try {
			InputStream input = new FileInputStream(fileName); // 建立输入流
			Workbook wb = null;
			// 根据文件格式(2003或者2007)来初始化
			if (isE2007){
				wb = new XSSFWorkbook(input);
			}
			else{
				wb = new HSSFWorkbook(input);
			}
			Sheet sheet = wb.getSheetAt(0); // 获得第一个表单
			Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
			while (rows.hasNext()) {
				Row row = rows.next(); // 获得行数据
				System.out.println("Row #" + row.getRowNum()); // 获得行号从0开始
				Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
				while (cells.hasNext()) {
					Cell cell = cells.next();
					System.out.println(cell);
				}
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void getE2007() {
		try {
			XSSFWorkbook wb = new XSSFWorkbook("G:\\11月杂.xls");
			int sheets = wb.getNumberOfSheets();
			System.out.println(sheets);
			XSSFSheet sheet = wb.getSheetAt(0);
			XSSFRow row;
			String cellString;
			for (int i = sheet.getFirstRowNum(); i < sheet.getPhysicalNumberOfRows(); i++) {
				row = sheet.getRow(i);
				for (int j = row.getFirstCellNum(); j < row.getPhysicalNumberOfCells(); j++) {
					cellString = row.getCell(j).toString();
					System.out.println(cellString);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void isE2003() {
		Workbook hssWB = null;
		try {
			// 2003
			hssWB = new HSSFWorkbook(new FileInputStream("G:\\11月杂.xls"));
			System.out.println("2003");
		} catch (Exception e) {
			// 2007
			try {
				hssWB = new XSSFWorkbook(new FileInputStream("G:\\11月杂.xls"));
				System.out.println("2007");
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
	}

	public static void getSeg() {
		Segment segment = HanLP.newSegment().enableAllNamedEntityRecognize(true).enableCustomDictionary(true)
				.enableOffset(true).enableIndexMode(true).enablePartOfSpeechTagging(true);
		String s = "王小姐 上海市普陀区杏山路8弄1号601室 021-52663207 13381607026 555 6217002430008593312";
		List<Term> termList = segment.seg(s);
		MultiValueMap segMap = DataImportUtils.extractNamingEntity(termList);
		for (Object key : segMap.keySet()) {
			System.out.println(key + ":" + segMap.get(key));
		}
	}

	public static void createFile() {
		String path = "G:\\middle01\\a\\a.txt";
		String p = path.substring(0, path.lastIndexOf("\\"));
		File f = new File(p);
		if (!f.exists()) {
			f.mkdirs();
		}
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
