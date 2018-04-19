package com.lch.utills;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.annotations.DataProvider;
import com.lch.testbase.TestBase;


public class ExcelReader {


	XSSFWorkbook wb;
	XSSFSheet sheet;

	@DataProvider(name = "testData")
	public Object[][] dataSupplier() throws IOException {
		String apiFileName = TestBase.apiFileName;
		Object[][] dataObject=null;
		if (apiFileName == null) {
			apiFileName="APIData.xlsx";
		}
		try 
			{
			File apiFile = new File(System.getProperty("user.dir") + "\\src\\resources\\TestData\\"+apiFileName);

			FileInputStream fis = new FileInputStream(apiFile);

			wb = new XSSFWorkbook(fis);
			sheet = wb.getSheet(TestBase.getProperty("Testing_Scope"));

			wb.close();
			int lastRowNum = sheet.getLastRowNum();
			
			int lastCellNum = sheet.getRow(0).getLastCellNum();
			dataObject = new Object[lastRowNum][1];
			String value;
			for (int i = 0; i < lastRowNum; i++) {
				//if(sheet.getRow(i+1).getCell(1)!=null){
					Map<Object, Object> datamap = new HashMap<Object, Object>();
					for (int j = 0; j < lastCellNum; j++) {
						//sets the value NA if the excel cell does not have  values
						
						if (sheet.getRow(i+1).getCell(j)==null) {
							value = "NA";
						}
						else {
							value = sheet.getRow(i+1).getCell(j).toString();
						}
						datamap.put(sheet.getRow(0).getCell(j).toString(), value);
					}
						dataObject[i][0] = datamap;
					//}
			}	
		}	
		catch(FileNotFoundException e)
			{
				e.printStackTrace();
			}
		return dataObject;
	}
}


