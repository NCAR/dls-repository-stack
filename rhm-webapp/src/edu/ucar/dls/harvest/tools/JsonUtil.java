/*
	Copyright 2017 Digital Learning Sciences (DLS) at the
	University Corporation for Atmospheric Research (UCAR),
	P.O. Box 3000, Boulder, CO 80307

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
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
