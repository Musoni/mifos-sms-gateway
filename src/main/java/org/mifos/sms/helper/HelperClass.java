package org.mifos.sms.helper;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.StringUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/** 
 * Main class with nested helper classes 
 * 
 * @author Emmanuel Nnaa
 **/
public class HelperClass {

	private static final Logger logger = LoggerFactory.getLogger(HelperClass.class);
	
	/** 
	 * Helper class that uses Google gson library to convert Java Objects into their JSON representation 
	 * and also to convert a JSON string to an equivalent Java object 
	 **/
	public static class JsonConverter {
		private static String dateFormat = GlobalConstants.GSON_DATE_FORMAT;
		private static Gson gson = new GsonBuilder().setDateFormat(dateFormat).create();
		
		/** 
		 * change the date format use by the GSON class
		 *
		 * @return void
		 **/
		public static void setDateFormat(String dateFormat) {
			JsonConverter.dateFormat = dateFormat;
			JsonConverter.gson = new GsonBuilder().setDateFormat(dateFormat).create();
		}
		
		/** 
		 * convert from JSON string to object 
		 * 
		 * @param jsonString JSON string
		 * @param classOfT the class of the generic Type
		 * @return map containing object, error (true/false) and error message if any
		 **/
		public static Map<String, Object> fromJson(String jsonString, Class<?> classOfT) {
			Map<String, Object> map = new HashMap<>();
			Boolean hasError = false;
			String errorMessage = "";
			map.put("object", "");
			
			if(StringUtils.isEmpty(jsonString)) {
				hasError = true;
				errorMessage = "The request body is empty";
			}
			
			else {
				try {
					Object objectList = gson.fromJson(jsonString, classOfT);
					map.put("object", objectList);
				}
				
				catch(Exception e) {
					hasError = true;
					errorMessage = e.getMessage();
					map.put("object", "");
					logger.error(e.getMessage());
				}
			}
			
			map.put("hasError", hasError);
			map.put("errorMessage", errorMessage);
			
			return map;
		}
		
		/** 
		 * convert from JSON string to object 
		 * 
		 * @param jsonString JSON string
		 * @param typeOfT The specific genericized type of src
		 * @return map containing object, error (true/false) and error message if any
		 **/
		public static Map<String, Object> fromJson(String jsonString, Type typeOfT) {
			Map<String, Object> map = new HashMap<>();
			Boolean hasError = false;
			String errorMessage = "";
			
			List<Object> objectList = new ArrayList<>();
			
			if(StringUtils.isEmpty(jsonString)) {
				hasError = true;
				errorMessage = "The request body is empty";
			}
			
			else {
				try {
					objectList = gson.fromJson(jsonString, typeOfT);
				}
				
				catch(Exception e) {
					hasError = true;
					errorMessage = e.getMessage();
					logger.error(e.getMessage());
				}
			}
			
			map.put("object", objectList);
			map.put("hasError", hasError);
			map.put("errorMessage", errorMessage);
			
			return map;
		}
		
		/** 
		 * convert from object to JSON string 
		 * 
		 * @param object the object to be converted
		 * @return Json representation of the object
		 **/
		public static String toJson(Object object) {
			String jsonString = "";
			
			try {
				jsonString = gson.toJson(object);
			}
			
			catch(Exception e) {
				logger.error(e.getMessage());
			}
			
			return jsonString;
		}
	}
	
	/** 
	 * Helper class that builds an Api response instance 
	 **/
	public static class ApiResponse {
		
		/** 
		 * @param data response data
		 * @return success response 
		 **/
		public static Response success(Object data) {
			Map<String, Object> entity = new HashMap<>(); 
			
			Integer httpStatusCode = HttpResponseStatusCode.OK.getValue();
			
			entity.put("httpStatusCode", httpStatusCode);
			entity.put("data", data);
			
			return Response.status(httpStatusCode).entity(entity).build();
		}
		
		/** 
		 * @param statusCode the HTTP response status code
		 * @param developerMessage error message for developers
		 * @return error response
		 **/
		public static Response error(HttpResponseStatusCode statusCode, String developerMessage) {
			Map<String, Object> entity = new HashMap<>(); 
			
			Integer httpStatusCode = statusCode.getValue();
			
			entity.put("httpStatusCode", httpStatusCode);
			entity.put("developerMessage", developerMessage);
			
			return Response.status(httpStatusCode).entity(entity).build();
			
		}
	}
	
	/** 
	 * JDBC helper class 
	 **/
	public static class Jdbc {
		
		/** 
		 * @return
		 **/
		public static Long getLong(final ResultSet rs, final String columnLabel) throws SQLException {
	        return (Long)JdbcUtils.getResultSetValue(rs, rs.findColumn(columnLabel), Long.class);
	    }

		/** 
		 * @return  
		 **/
	    public static Integer getInteger(final ResultSet rs, final String columnLabel) throws SQLException {
	        return (Integer)JdbcUtils.getResultSetValue(rs, rs.findColumn(columnLabel), Integer.class);
	    }
	}
}
