package com.hisign.solr;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * 操作excel文件（2003/2007）
 * 
 * @author Administrator
 *
 */
public class ExcelUtil {

	public static int getSheets(String filePath) {
		boolean isE2007 = false; // 判断是否是excel2007格式
		if (filePath.endsWith("xlsx")) {
			isE2007 = true;
		}
		try {
			InputStream input = new FileInputStream(filePath); // 建立输入流
			Workbook wb = null;
			int sheets = 0;
			if (isE2007) {
				wb = new XSSFWorkbook(input);
				sheets = wb.getNumberOfSheets();
			} else {
				wb = new HSSFWorkbook(input);
				sheets = wb.getNumberOfSheets();
			}
			return sheets;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static String getContent(String filePath) {
		int fg = 0;
		StringBuffer sb = new StringBuffer();
		if (new File(filePath).length() == 0) {
			return null;
		}
		boolean isE2007 = false; // 判断是否是excel2007格式
		if (filePath.endsWith("xlsx")) {
			isE2007 = true;
		}
		try {
			InputStream input = new FileInputStream(filePath); // 建立输入流
			Workbook wb = null;
			int sheets = 0;
			// 根据文件格式(2003或者2007)来初始化
			if (isE2007) {
				wb = new XSSFWorkbook(input);
			} else {
				wb = new HSSFWorkbook(input);
			}
			sheets = wb.getNumberOfSheets();
			// String[] array = new String[sheets];
			for (int a = 0; a < sheets; a++) {
				// String fileName = new File(filePath).getName();
				// fileName = fileName.substring(0, fileName.lastIndexOf("."));
				// 获取Sheet表
				Sheet sheet = wb.getSheetAt(a);
				// 获得合并单元格加入list中
				List<CellRangeAddress> list = getCombineCell(sheet);
				Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
				while (rows.hasNext()) {
					Row row = rows.next(); // 获得行数据
					Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
					while (cells.hasNext()) {
						boolean trueOrFalse = false;
						Cell cell = cells.next();
						// 判断是否为合并单元格
						boolean flag = isCombineCell(list, cell, sheet);
						if (flag) {
							// 如果为合并单元格，将设置单元格内容
							trueOrFalse = setCellValue(list, cell, sheet);
						}
						String cell_value = setCellStyle(cell);
						sb.append(cell_value).append("\t");
					}
					sb.append("\n");
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getContent01(String filePath) {
		StringBuffer sb = new StringBuffer();
		if (new File(filePath).length() == 0) {
			return null;
		}
		boolean isE2007 = false; // 判断是否是excel2007格式
		if (filePath.endsWith("xlsx")) {
			isE2007 = true;
		}
		try {
			InputStream input = new FileInputStream(filePath); // 建立输入流
			Workbook wb = null;
			int sheets = 0;
			// 根据文件格式(2003或者2007)来初始化
			if (isE2007) {
				wb = new XSSFWorkbook(input);
			} else {
				wb = new HSSFWorkbook(input);
			}
			sheets = wb.getNumberOfSheets();
			for (int a = 0; a < sheets; a++) {
				// 获取Sheet表
				Sheet sheet = wb.getSheetAt(a);
				// 获得合并单元格加入list中
				List<CellRangeAddress> list = getCombineCell(sheet);
				// 首尾两行行数
				int firstRow = sheet.getFirstRowNum();
				int endRow = sheet.getLastRowNum();
				for (int aa = 0; aa <= endRow; aa++) {
					Row row = sheet.getRow(aa);
					if (row != null) {
						int endCell = row.getLastCellNum();
						for (int bb = 0; bb < endCell; bb++) {
							boolean trueOrFalse = false;
							Cell cell = row.getCell(bb);
							boolean flag = false;
							if (cell != null) {
								// 判断是否为合并单元格
								flag = isCombineCell(list, cell, sheet);
							}

							// System.out.println(flag);
							if (flag) {
								// 如果为合并单元格，将设置单元格内容
								trueOrFalse = setCellValue(list, cell, sheet);
							}
							if (cell == null || cell.getCellType() == Cell.CELL_TYPE_BLANK) {
								sb.append("\t");
							} else {
								String cellValue = setCellStyle(cell);
								sb.append(cellValue).append("\t");
							}
						}
					} else {
						continue;
					}
					sb.append("\n");
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将单元格格式转换
	 * 
	 * @param cell
	 * @return
	 */
	public static String setCellStyle(Cell cell) {
		String cellValue = "";
		if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
			DecimalFormat df = new DecimalFormat("#");
			cellValue = df.format(cell.getNumericCellValue());
			cellValue = cellValue.replace(" ", "|").replace("\n", "|").replace("\r", "|").replace("\t", "|");
			cellValue = DataImportUtils.qj2bj(cellValue);
		} else {
			cell.setCellType(HSSFCell.CELL_TYPE_STRING);
			String c = cell.toString().replace(" ", "|").replace("\n", "|").replace("\r", "|").replace("\t", "|");
			cellValue = DataImportUtils.qj2bj(c);
		}
		return cellValue;
	}

	/**
	 * 合并单元格处理--加入list
	 * 
	 * @param sheet
	 * @return
	 */
	public static List<CellRangeAddress> getCombineCell(Sheet sheet) {
		List<CellRangeAddress> list = new ArrayList<>();
		// 获得一个 sheet 中合并单元格的数量
		int sheetmergerCount = sheet.getNumMergedRegions();
		// 遍历合并单元格
		for (int i = 0; i < sheetmergerCount; i++) {
			// 获得合并单元格加入list中
			CellRangeAddress ca = sheet.getMergedRegion(i);
			list.add(ca);
		}
		return list;
	}

	/**
	 * 判断单元格是否为合并单元格
	 * 
	 * @param listCombineCell
	 *            存放合并单元格的list
	 * @param cell
	 *            需要判断的单元格
	 * @param sheet
	 *            sheet
	 * @return
	 */
	public static Boolean isCombineCell(List<CellRangeAddress> listCombineCell, Cell cell, Sheet sheet) {
		int firstC = 0;
		int lastC = 0;
		int firstR = 0;
		int lastR = 0;
		for (CellRangeAddress ca : listCombineCell) {
			// 获得合并单元格的起始行, 结束行, 起始列, 结束列
			firstC = ca.getFirstColumn();
			lastC = ca.getLastColumn();
			firstR = ca.getFirstRow();
			lastR = ca.getLastRow();
			if (cell.getColumnIndex() <= lastC && cell.getColumnIndex() >= firstC) {
				if (cell.getRowIndex() <= lastR && cell.getRowIndex() >= firstR) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 得到合并单元格首行首列的值，并设置给所有合并单元格
	 * 
	 * @param listCombineCell
	 * @param cell
	 * @param sheet
	 * @return
	 */
	public static boolean setCellValue(List<CellRangeAddress> listCombineCell, Cell cell, Sheet sheet) {
		int firstC = 0;
		int lastC = 0;
		int firstR = 0;
		int lastR = 0;
		for (CellRangeAddress ca : listCombineCell) {
			// 获得合并单元格的起始行, 结束行, 起始列, 结束列
			firstC = ca.getFirstColumn();
			lastC = ca.getLastColumn();
			firstR = ca.getFirstRow();
			lastR = ca.getLastRow();
			if (cell.getColumnIndex() <= lastC && cell.getColumnIndex() >= firstC) {
				if (cell.getRowIndex() <= lastR && cell.getRowIndex() >= firstR) {

					Row row = sheet.getRow(firstR);
					Cell cell_ = row.getCell(firstC);

					String cellValue = "";
					if (cell_.getCellType() == Cell.CELL_TYPE_NUMERIC) {
						DecimalFormat df = new DecimalFormat("#");
						cellValue = df.format(cell_.getNumericCellValue());
						cellValue = cellValue.replace(" ", "|").replace("\n", "|").replace("\r", "|").replace("\t",
								"|");
						cellValue = DataImportUtils.qj2bj(cellValue);
					} else {
						cell_.setCellType(HSSFCell.CELL_TYPE_STRING);
						cellValue = cell_.toString().replace(" ", "|").replace("\n", "|").replace("\r", "|")
								.replace("\t", "|");
						cellValue = DataImportUtils.qj2bj(cellValue);
					}
					// 设置存入内容为字符串
					cell.setCellType(HSSFCell.CELL_TYPE_STRING);
					// 向单元格中放入值
					cell.setCellValue(cellValue);
					return true;
				}
			}
		}
		return false;
	}

	public static String[] getTemPath(String filePath) {
		Properties prop = PropertiesUtil.getProp();
		String path = prop.getProperty("newpath");
		if (new File(filePath).length() == 0) {
			return null;
		}
		BufferedWriter bufferWritter = null;
		boolean isE2007 = false; // 判断是否是excel2007格式
		if (filePath.endsWith("xlsx")) {
			isE2007 = true;
		}
		try {
			InputStream input = new FileInputStream(filePath); // 建立输入流
			Workbook wb = null;
			int sheets = 0;
			// 根据文件格式(2003或者2007)来初始化
			if (isE2007) {
				wb = new XSSFWorkbook(input);
				sheets = wb.getNumberOfSheets();
			} else {
				wb = new HSSFWorkbook(input);
				sheets = wb.getNumberOfSheets();
			}
			String[] array = new String[sheets];
			for (int a = 0; a < sheets; a++) {
				String fileName = new File(filePath).getName();
				fileName = fileName.substring(0, fileName.lastIndexOf("."));
				String newpath = path + fileName + "_" + a + ".log";
				FileWriter fileWritter = new FileWriter(newpath, true);
				bufferWritter = new BufferedWriter(fileWritter);
				// 获取Sheet表
				Sheet sheet = wb.getSheetAt(a);

				array[a] = newpath;
				Iterator<Row> rows = sheet.rowIterator(); // 获得第一个表单的迭代器
				while (rows.hasNext()) {
					Row row = rows.next(); // 获得行数据
					Iterator<Cell> cells = row.cellIterator(); // 获得第一行的迭代器
					while (cells.hasNext()) {
						Cell cell = cells.next();
						if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
							DecimalFormat df = new DecimalFormat("#");
							String cellValue = df.format(cell.getNumericCellValue());
							cellValue = cellValue.replace(" ", "");
							bufferWritter.write(cellValue);
							bufferWritter.write(" ");
						} else {
							cell.setCellType(HSSFCell.CELL_TYPE_STRING);
							String c = cell.toString().replace(" ", "");
							bufferWritter.write(c);
							bufferWritter.write(" ");
						}
					}
					bufferWritter.write("\n");
					bufferWritter.flush();
				}

			}
			return array;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferWritter != null) {
					bufferWritter.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
