package com.lch.testscripts;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;

import com.jayway.restassured.response.Response;
import com.lch.testbase.TestBase;
import com.lch.utills.ExcelReader;
import com.lch.utills.ExcelWriter;
import com.lch.webservices.methods.Webservices;

import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;


@Guice
public class TestScripts extends TestBase {
	 static Logger LOG = Logger.getLogger(TestScripts.class.getName());
	int dataCount = 1;	
	String newJson;
	JSONParser jsonParser;
	Object jsonParseObject;
	String inputJson;
	Response response = null;
	String testcaseID;
	//Response response1 =null;
	@BeforeClass
	public void beforeSuite(){
		SetUp();
		LOG.info("Execution Started");
		CreateResultExcel();
	}
	
	
	@Test(dataProvider="testData", dataProviderClass = ExcelReader.class)
	public void testMethod(Map<Object, Object> datamap) throws ConnectException, ParseException{
		
		String apiURL = (String) datamap.get("APIURL");	
		String apiType =  (String) datamap.get("Type");
		testcaseID = (String) datamap.get("TCID");
		String expectedResponseCode = (String) datamap.get("ExpResponseCode"); 
		String validations="None";
		HashMap<String,String> headersMap=new HashMap<String,String>();
		Map<String,String> validationDataMap=new HashMap<String,String>(); 
		
		//reads the headers and attach to request
		headersMap = getHeadersInfo(datamap);
				
				switch((String) datamap.get("Type")){
				case "GET":
					response = Webservices.Get(headersMap,apiURL);
					break;
				case "POST":
					jsonRequest = jsonRequest(datamap);
					response = Webservices.Post(headersMap,apiURL,jsonRequest);
					break;
				case "PUT":
					jsonRequest = jsonRequest(datamap);
					response = Webservices.Put(headersMap,apiURL,jsonRequest);
					break;
				case "DELETE":
					response = Webservices.Delete(headersMap,apiURL);
					break;
				}
				
				TestBase.responseCode = String.valueOf(response.getStatusCode());
				TestBase.jsonResponse= response.asString();
				//TestBase.jsonResponse= "";
				TestBase.jsonRequest = "";
				
				if (expectedResponseCode.equals(TestBase.responseCode)) {
					testcaseStatus = "PASS";
				}
				else{
					testcaseStatus = "FAIL";
				}
				
				validationDataMap = ValidateResponse(datamap);
				String apiResults=null;			
				if (!validationDataMap.isEmpty()) {
					apiResults = this.verifyValidations(TestBase.jsonResponse,validationDataMap);
					System.out.println(apiResults);
					validations=apiResults;
					validationDataMap.clear();
				}
				
				if (testcaseStatus.equals("FAIL")) {
					TestBase.overallRunStatus = "FAIL";
				}
				
				testResultsData.put(String.valueOf(dataCount++), new Object[] {testcaseID, testcaseStatus, apiURL, apiType,expectedResponseCode,TestBase.responseCode,validations,TestBase.jsonRequest,TestBase.jsonResponse.length()});
				SoftAssert softAssertion = new SoftAssert();
				softAssertion.assertEquals(TestBase.responseCode, expectedResponseCode);
				softAssertion.assertAll();
				
	}
	
	/*@AfterMethod
	public void afterMethod(){
		if(testcaseID=="GetResponseJSON"){
		response1 = response;
		System.out.println(response1);
	}
	}*/
	
	
	
	@AfterSuite
	public void afterClass() {
		if ((TestBase.getProperty("report_format").equalsIgnoreCase("EXCEL")) || (TestBase.getProperty("report_format").equalsIgnoreCase("")))  {
			//writes the results in excel file.
			ExcelWriter writeInExcel = new ExcelWriter();
			writeInExcel.writeResultsInExcel(workbook, sheet, testResultsData);
			
		}
	}
	
}
