package com.lch.testbase;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;

public class TestBase {

	protected XSSFWorkbook workbook;
	//define an Excel Work sheet
	protected XSSFSheet sheet;
	//define a test result data object
	protected Map<String, Object[]> testResultsData;
	HashMap<String,String> headersMap=new HashMap<String,String>();
	JSONParser jsonParser;
	Object jsonParseObject;
	String inputJson;
	DocumentContext jsonString=null;
	boolean isParameterised = false;
	protected Map<String,String> validationDataMap=new HashMap<String,String>(); 
	String validations="None";
	
	public void SetUp(){
		String log4jConfPath = System.getProperty("user.dir")+"\\log4j.properties";
		PropertyConfigurator.configure(log4jConfPath);
	}
	
	public void CreateResultExcel(){
		
		workbook = new XSSFWorkbook();
		//create a new work sheet
		sheet = workbook.createSheet("Test Result");
		sheet.setColumnWidth(0,2000);
		sheet.setColumnWidth(1,2000);
		sheet.setColumnWidth(2,10000);
		sheet.setColumnWidth(3,2000);
		sheet.setColumnWidth(4,2000);
		sheet.setColumnWidth(5,2000);
		sheet.setColumnWidth(6, 10000);
		sheet.setColumnWidth(7, 10000);
		sheet.setColumnWidth(8, 10000);
	
		testResultsData = new LinkedHashMap<String, Object[]>();
		//add test result excel file column header
		//write the header in the first row
		testResultsData.put("Headers", new Object[] {"TestCaseID","TestCaseStatus","APIURL", "Request Type", "Expected Response Code","Response Code","Validations","Request","Response"});		
	}

	public static String getTimeStamp() {
		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a");
		String formattedDate = dateFormat.format(date);
		return formattedDate.replace("/", "_").replace(" ", "_").replace(":", "_");
	}
	
	private static Properties defaultProps = new Properties();
	static {
		try {
	
			FileInputStream in = new FileInputStream (System.getProperty("user.dir") + "\\src\\resources\\config.properties");
			defaultProps.load(in);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String getProperty(String key) {
		return defaultProps.getProperty(key);
	}
	
	public String verifyValidations(String jsonToBeParsed, Map<String,String> validationMap) {

		String allValidations = "";
		for(Object key : validationMap.keySet()){

			String keyName = key.toString();
			String validation = (String) validationMap.get(key);
			String[] keyAndValue = validation.split("=");
			if (keyAndValue.length>0) {
				//System.out.println(keyAndValue[0].substring(1));
				if (JsonPath.parse(jsonToBeParsed).read("$."+keyAndValue[0])!=null) {
					
					String validatiion = JsonPath.parse(jsonToBeParsed).read("$."+keyAndValue[0]).toString();
					if (validatiion.contains(keyAndValue[1])) {
						allValidations = allValidations + keyName + " -> " + "PASSED |";
					}
					else {
						allValidations = allValidations + keyName + " -> " + "FAIL (actual = "+keyAndValue[1]+" - expected = "+validatiion+") |";
						testcaseStatus = "FAIL";
					}
				}
			}
		}
		return allValidations;
	}
	
	
	
	public HashMap<String, String> getHeadersInfo(Map<Object, Object> datamap){
		String headerName = (String) datamap.get("Header");
		if (headerName != null) {
			String[] headers = TestBase.getProperty(headerName).split(",");
			for (int i=0;i<headers.length;i++) {
				String[] headersKeyValue = headers[i].split(":");
				headersMap.put(headersKeyValue[0], String.valueOf(headersKeyValue[1]));	
			}
		}
	return headersMap;
	}
	
	public String jsonRequest(Map<Object, Object> datamap) throws ParseException{
	
	@SuppressWarnings("deprecation")
	JSONParser jsonParser = new JSONParser();

	//reads the json file and parse it
	try {
		//if(apiType=="POST"){
		jsonParseObject = jsonParser
				.parse(new FileReader(System.getProperty("user.dir") + 
						"\\src\\resources\\JSONFiles\\"+datamap.get("APIName")+".json"));
	
	JSONObject jsonObject = (JSONObject) jsonParseObject;
	String jsonInput = jsonObject.toJSONString();
		
	//looping for setting the specified attributes
	for(Object key : datamap.keySet()){

		String keyName = key.toString();
		String param = (String) datamap.get(key);
		
		if (keyName.contains("Param")) {
			if (!param.equalsIgnoreCase("NA")) {
				String[] keyAndValue = param.split("=");
				//modify the value for the attribute
				if (keyAndValue.length > 0) {
					jsonString = JsonPath.parse(jsonInput).set("$."+keyAndValue[0], keyAndValue[1]);
				}
				inputJson = new Gson().toJson(jsonString.read("$"));
				isParameterised = true;
			}
		}
		
		/*if (keyName.contains("Validation")) {
			if (!param.equalsIgnoreCase("NA")) {
				validationDataMap.put(keyName,param);
			}
		}*/
	}
	
	//convert the string to Json string
	if (isParameterised==true) {
		TestBase.jsonRequest = new Gson().toJson(jsonString.read("$"));
	}
	else {
		jsonString = JsonPath.parse(jsonInput);
		TestBase.jsonRequest = new Gson().toJson(jsonString.read("$"));
	}
	}
	catch (IOException ex) {
		TestBase.jsonResponse = ex.getMessage() +  " URL -" +"" ;
		
	}
	return jsonRequest;
}
		
	public Map<String, String> ValidateResponse(Map<Object, Object> datamap){
		
		for(Object key : datamap.keySet()){

			String keyName = key.toString();
			String param = (String) datamap.get(key);
			
			if (keyName.contains("Validation")) {
				if (!param.equalsIgnoreCase("NA")) {
					validationDataMap.put(keyName,param);
				}
			}
		}
		return validationDataMap;
				
	}
	
	public String testcaseStatus = "FAIL";
	public static String responseCode;
	public static String jsonRequest;
	public static String jsonResponse;
	public static String JsonFileName;
	public static String token;
	public static String apiFileName;
	public static String overallRunStatus;
}