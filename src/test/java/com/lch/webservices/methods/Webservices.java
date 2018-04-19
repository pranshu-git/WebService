package com.lch.webservices.methods;

import java.util.HashMap;

import org.apache.log4j.Logger;

import static com.jayway.restassured.RestAssured.*;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

public class Webservices {
	static Logger LOG = Logger.getLogger(Webservices.class.getName());
	
	public static Response Get(HashMap<String, String> headers,String uRI){ //uniform resources identifier 
		RequestSpecification requestSpecification = given().
				headers(headers);//RestAssured is a class and given is a static method
		requestSpecification.contentType(ContentType.JSON);
		Response response =requestSpecification.get(uRI);
		LOG.info("GET Request sent successfully");
		return response;	
	}
	
	public static Response Post(HashMap<String, String> headers,String uRI,String stringJSON){
		RequestSpecification requestSpecification = given().
				headers(headers).body(stringJSON);
		requestSpecification.contentType(ContentType.JSON);
		Response response = requestSpecification.post(uRI); //Response is a abstract interface
		LOG.info("POST Request sent successfully");
		return response;	
	}

	public static Response Put(HashMap<String, String> headers,String uRI,String stringJSON){
		RequestSpecification requestSpecification = given().headers(headers).body(stringJSON);
		requestSpecification.contentType(ContentType.JSON);
		Response response = requestSpecification.put(uRI);
		LOG.info("PUT Request sent successfully");
		return response;	
	}
	
	public static Response Delete(HashMap<String, String> headers,String uRI){  
		RequestSpecification requestSpecification = given().headers(headers);
		requestSpecification.contentType(ContentType.JSON);
		Response response =requestSpecification.delete(uRI);
		LOG.info("DELETE Request sent successfully");
		return response;	
	}
	
}
