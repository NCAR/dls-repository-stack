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
package edu.ucar.dls.dds;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class MapEntryConverter implements Converter{
		public boolean canConvert(Class clazz) {
		    return AbstractMap.class.isAssignableFrom(clazz);
		}

		public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
		    AbstractMap<String,String> map = (AbstractMap<String,String>) value;
		    for (Entry<String, String> entry : map.entrySet()) {
		        writer.startNode(entry.getKey().toString());
		        writer.setValue(entry.getValue().toString());
		        writer.endNode();
		    }
		}

		public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
		    Map<String, String> map = new HashMap<String, String>();

		    while(reader.hasMoreChildren()) {
		        reader.moveDown();
		        map.put(reader.getNodeName(), reader.getValue());
		        reader.moveUp();
		    }
		    return map;
		}
	}
