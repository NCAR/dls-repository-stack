package edu.ucar.dls.harvest.tools;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

/** 
 * Helper class to convert JSON to different formats
 *
 */
public class JsonUtil{
	/**
	 * Convert json String to a HashMap. Where booleans are converted to Boolean objects,
	 * floats to Float, ints to Intgers and strings to Strings
	 * @param jsonString
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static Map<String,Object> convertToMap(String jsonString) throws JsonParseException, JsonMappingException, IOException
	{
		JsonFactory factory = new JsonFactory(); 
	    ObjectMapper mapper = new ObjectMapper(factory); 
	    
	    TypeReference<HashMap<String,Object>> typeRef 
	          = new TypeReference< 
	                 HashMap<String,Object> 
	               >() {}; 
	    HashMap<String,Object> o 
	         = mapper.readValue(jsonString, typeRef); 
	    return o;
	}
	
	/**
	 * Convert a JSON string to XML. No direct mapping could be found. So we do a convert
	 * to map then use the xmlMapper to convert the Map to XML
	 * @param jsonString
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static String convertToXML(String jsonString) throws JsonParseException, JsonMappingException, IOException
	{
	    XmlMapper xmlMapper = new XmlMapper();
	    return xmlMapper.writeValueAsString(JsonUtil.convertToMap(jsonString));
	}
	
	/**
	 * Converts a Map into a JSON string. 
	 * @param jsonHashMap
	 * @return
	 * @throws JsonProcessingException
	 */
	public static String convertToJson(Map jsonHashMap) throws JsonProcessingException
	{
		JsonFactory factory = new JsonFactory(); 
		ObjectMapper mapper = new ObjectMapper(factory); 
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		return mapper.writeValueAsString(jsonHashMap);
	}
	
}