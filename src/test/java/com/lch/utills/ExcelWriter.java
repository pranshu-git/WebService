package com.lch.utills;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.lch.testbase.TestBase;

public class ExcelWriter {
	XSSFWorkbook book;

	public void writeResultsInExcel(XSSFWorkbook workbook, XSSFSheet sheet, Map<String, Object[]> testResultsData) {

		// creating the format of the excel for reporting
		Set<String> keyset = testResultsData.keySet();
		int rownum = 0;
		for (String key : keyset) {
			Row row = sheet.createRow(rownum++);
			Object [] objArr = testResultsData.get(key);
			int cellnum = 0;
			for (Object obj : objArr) {
				Cell cell = row.createCell(cellnum++);
				if(obj instanceof Date) 
					cell.setCellValue((Date)obj);
				else if(obj instanceof Boolean)
					cell.setCellValue((Boolean)obj);
				else if(obj instanceof String)
					cell.setCellValue((String)obj);
				else if(obj instanceof Double)
					cell.setCellValue((Double)obj);

				if(key.equalsIgnoreCase("Headers")) {
					/* specify a background cell color */
					CellStyle cellStyle = cell.getCellStyle();
					cellStyle.setFillBackgroundColor(IndexedColors.ROYAL_BLUE.getIndex());
					cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);					
					cell.setCellStyle(cellStyle);

				}
				else {
					
				}

			}
		}
		try {
			FileOutputStream out =new FileOutputStream(new File(System.getProperty("user.dir") + "\\test-output\\TestResult_"+TestBase.getTimeStamp()+".xlsx"));
			workbook.write(out);
			out.close();
			System.out.println("Excel written successfully..");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
